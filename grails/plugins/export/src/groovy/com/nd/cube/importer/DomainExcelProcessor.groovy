package com.nd.cube.importer

import framework.ModelService
import grails.util.Holders
import org.codehaus.groovy.grails.commons.GrailsClass

/**
 * Created by wuzj on 2015/2/6.
 */
class DomainExcelProcessor {
    String domainName
    ExcelProcessor excelProcessor
    GrailsClass domain
    List<Map> converter
    List validator
    DomainExcelProcessor(String domainName, ExcelProcessor excelProcessor) {
        this.domainName = domainName
        this.excelProcessor = excelProcessor
        domain = Holders.getGrailsApplication().getArtefactByLogicalPropertyName("Domain",domainName)
    }

    List get(){
        List result = excelProcessor.read()
        List domainList = new ArrayList()
        result.each {
            domainList.add(domain.clazz.newInstance(it))
        }
        return domainList
    }
    List getWithoutValidation(){
        excelProcessor.setConverter(Collections.emptyMap())
        excelProcessor.setValidator(Collections.emptyList())
        return get()
    }
    List getWithValidation(){
        excelProcessor.setConverter(getConverter())
        excelProcessor.setValidator(getValidator())
        return get()
    }

    /**
     * 暂时支持inlist和date的转换
     * @return
     */
    List<Map> getConverter(){
        if (converter == null){
            converter = []
            List transFields = ModelService.GetModel(domainName).get("transFields")
            transFields.each {it->
                Map item = [:]
                if (it.type == "int" && it.constraint.inList){
                    Map listMap = [:]
                    it.constraint.inListLabel.eachWithIndex {label,idx ->
                        listMap.put(label, it.constraint.inList[idx])
                    }
                    item.put(it.name,"list")
                    item.put('listMap',listMap)
                }else if(it.type == "date" && it.widget == "datefield"){
                    item.put(it.name,"date")
                }else if(it.type == "date" && it.widget == "datetimefield"){
                    item.put(it.name,"datetime")
                }

                if(item.size()!=0) converter.push(item)
            }
        }
        return converter
    }

    List getValidator(){
        if (validator == null){
//            List supportMethod = ['nullable','blank','maxSize','minSize','inList','',] //支持的校验规则
            validator = new ArrayList()
            List transFields = ModelService.GetModel(domainName).get("transFields")
            Map excelFields = excelProcessor.getFields()

            transFields.each {field->
                if(excelFields.containsKey(field.name)) {

                    field.constraint.findAll {it.value!=null;it.key!='inList'&& it.key!='blank' && it.key!='matches' && it.key!='email'}?.each {constraint-> //跳过校验inList的约束
                        Map map = new HashMap()

                        map.put("field",field.name)
                        Map rule = new HashMap()
                        rule.put(constraint.key,constraint.value)
                        map.put("rule",rule)
                        map.put("message","字段（${field.cn}）的值非法：${constraint.key}=${constraint.value}")

                        validator.push(map)
                    }
                }
            }
        }

        return validator
    }
}
