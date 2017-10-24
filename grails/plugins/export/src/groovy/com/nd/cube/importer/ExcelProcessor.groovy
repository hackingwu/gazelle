package com.nd.cube.importer

import com.nd.cube.common.validator.ValidatorUtil

import java.text.SimpleDateFormat

/**
 * Created by Administrator on 2015/2/4.
 */
class ExcelProcessor {
    Excel excel
    int strategy
    List<Map> validator = new ArrayList<>()

    List<Map> converter = new ArrayList<>()
    Map labels //无序
    Map fields //保存excel列与field name的映射
    enum ExcelProcessorStrategy{
        ignore,cancel,ignoreAndReport,cancelAndReport
    }
    ExcelProcessor(Excel excel,int strategy) {
        this.excel = excel
        this.strategy = strategy
    }

    ExcelProcessor(Excel excel, int strategy, List<Map> validator, List<Map> converter) {
        this.excel = excel
        this.strategy = strategy
        this.validator = validator
        this.converter = converter
    }

    List read(){
        List<Map> contentMap = this.getRawContentMap()

        for(int k=(excel.header?1:0);k<contentMap.size();k++){
            //校验
            Map errorMessage = ValidatorUtil.getErrorMessage(validator,contentMap[k])
            if(errorMessage.isEmpty()){
                getFields().each {it ->
                    String key = it.key
                    //转换
                    Map conMap = converter.find{item->item.containsKey(key)}
                    if(conMap!=null){
                        contentMap[k].put(it.key, convert(it.key, contentMap[k].get(it.key),conMap))
                    }
                }
            }else{
                //todo:导出
                contentMap.remove(k)
                println("Excel第${k+2}行数据非法:${errorMessage}")
            }
        }

        return contentMap
    }

    Map getFields() {
        if(fields==null){
            fields = [:]
            if (labels!=null && excel.header){
                List contentHeader = excel.getContentHeader()
                labels.each {it->
                    for(int k=0;k<contentHeader.size();k++){
                        if(it.key.equals(contentHeader[k])){
                            fields.put(it.value,k)
                            break
                        }
                    }
                }
            }
        }
        return fields
    }

    public  List<Map> getRawContentMap(){
        List<Map> contentMap = []

        int index = (excel.header?1:0)

        List allContent = excel.getContent()

        while(index < allContent.size()){
            Map map = new HashMap()
            getFields().each{it->
                List rowData = allContent.get(index)
                String cellValue = ((rowData.size()>it.value)?rowData.get(it.value):"") //保证每行都有值
                map.put(it.key, cellValue)
            }

            contentMap.add(map)

            index++
        }

        return contentMap
    }

    void setFields(Map fields) {
        this.fields = fields
    }

    def convert(String key, String value, Map rule){
        String type = rule.get(key)
        if(type == "list"){
            return Integer.valueOf(rule.listMap.get(value))
        }else if(type == "date"){
            try{
                return new SimpleDateFormat("yyyy-MM-dd").parse(value)
            }catch (e){
                return null
            }
        }else if(type == "datetime"){
            try{
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value)
            }catch (e){
                return null
            }
        }
    }

}
