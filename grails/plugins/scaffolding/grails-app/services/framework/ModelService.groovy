package framework

import grails.util.Holders
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.validation.ConstrainedProperty

/**
 * 模型服务，用于获取：Domain和ViewObject的属性、类型和约束;
 * @author linyu, lichb
 * @version 2014-07-22
 */
class ModelService {
    static transactional = false

    static Map domainModels = [:]

    //TODO: lichb，注释，另外建议GetMessageByCode独立成一个服务，国际化
    static String GetMsgByCode(String code){
        try{
            Holders.grailsApplication.mainContext.getMessage("${code}",null,null)
        }catch (e){
            null
        }
    }

    /**
     * 根据模型名称取出模型的的值
     * @param model 类package.Domain的名称，如：test.Teacher
     * @return [] 返回的list格式由相应domain class的toString()定义
     */
    static List GetModelDataByDomainName(String model){
        GrailsApplication grailsApplication = Holders.grailsApplication
        return grailsApplication.getArtefactByLogicalPropertyName("Domain",model).clazz.findAll()
    }

    /**
     * 根据模型名称取出，模型的属性、类型和约束
     * @param model 类package.Domain的名称，如：test.Teacher
     * @return [m:[domain:[cn:教师], layout:[type:standard], filter:[], search:[fields:[name, description]]], fields:[[name:name, prop:[DefaultGrailsDomainClassProperty@341bdd4c name = 'name', type = String, persistent = true, optional = false, association = false, bidirectional = false, association-type = [null]], cp:[ConstrainedProperty@115dc34eTeacher'name'Stringmap['size' -> [SizeConstraint@22c13ecalist[2, 3, 4, 5, 6]], 'nullable' -> [NullableConstraint@3d015199false]]], cn:姓名], [name:gender, prop:[DefaultGrailsDomainClassProperty@21943319 name = 'gender', type = String, persistent = true, optional = false, association = false, bidirectional = false, association-type = [null]], cp:[ConstrainedProperty@8a6bf88Teacher'gender'Stringmap['blank' -> [BlankConstraint@7b687e27false], 'nullable' -> [NullableConstraint@93271bdfalse]]], cn:性别], [name:description, prop:[DefaultGrailsDomainClassProperty@4ec93402 name = 'description', type = String, persistent = true, optional = false, association = false, bidirectional = false, association-type = [null]], cp:[ConstrainedProperty@7b7597b6Teacher'description'Stringmap['size' -> [SizeConstraint@653b6d0clist[1, 2, 3, 4, 5, 6]], 'nullable' -> [NullableConstraint@207462c0false]]], cn:描述]]]
     */
    static Map GetModel(String model) {

        //是否已经有缓存?
        if (domainModels[model] == null) {

            GrailsApplication grailsApplication = Holders.grailsApplication

            //通过Grails接口取出Domain
            //GrailsDomainClass domainClass =grailsApplication.getArtefact("Domain", model)
            GrailsDomainClass domainClass = grailsApplication.getArtefactByLogicalPropertyName("Domain", model)
            Map domainMap = [:]

            if (domainClass != null) {
                //取domain的meta数据
                Map m = domainClass.clazz["m"]?:""

                //取domain约束
                Map<String, ConstrainedProperty>  mConstraints = domainClass.getConstrainedProperties()

                //查找所有合适显示的属性
                List<String> excluded = ["version", "id"]
                List<String> allowed = domainClass.persistentProperties*.name

                //支持在views的grid上显示transients里的字段，默认不显示
                List<String> allProperties = domainClass.properties*.name << "dateCreated" << "lastUpdated"
                List<String> transProperties = allProperties - allowed - excluded
                GrailsDomainClassProperty[] properties = domainClass.properties.findAll {
                    allProperties.contains(it.name) && !excluded.contains(it.name)
                }

                //根据约束定义顺序排序
                properties = properties.sort { mConstraints[it.name].getOrder(); }
                //properties=properties.reverse()

                domainMap = [className: domainClass.name, propertyName: domainClass.propertyName, m: m, fields: [], transFields: [], fieldsMap: [:]]

                //对所有属性进行循环
                properties.each { GrailsDomainClassProperty prop ->
                    Map map = [
                            name: prop.name,
                            cn  : mConstraints[prop.name].attributes.cn==""?mConstraints[prop.name].attributes.cn:(GetMsgByCode("${domainClass.propertyName}.${prop.name}.lang")?:(mConstraints[prop.name].attributes.cn ?: prop.naturalName)),
                            type: prop.typePropertyName,
                            widget: mConstraints[prop.name].attributes.widget,  //domain class上定义的widget类型
                            flex: mConstraints[prop.name].attributes.flex ?: 1,  //grid的flex
                            renderer: mConstraints[prop.name].attributes.renderer,  //Grid列内容显示自定义回调接口
                            blur:mConstraints[prop.name].attributes.blur,  //控件失去焦点时，自定义回调接口
                            include:mConstraints[prop.name].attributes.include,  //定义关联其他domain的select控件显示的列
                            vtype:mConstraints[prop.name].attributes.vtype, //定义关联其他domain的select控件显示的列
                            scaffolding:mConstraints[prop.name].attributes.scaffolding,  //表单生成自定义接口
                            trigger:mConstraints[prop.name].attributes.trigger, //联动控件触发器
                            register:mConstraints[prop.name].attributes.register, //联动控件监听器
                            sort:mConstraints[prop.name].attributes.sort, //grid是否允许排序
                    ]

                    if (map.type == "string") {
                        map["constraint"] = [
                                nullable  : mConstraints[prop.name].nullable,
                                blank     : mConstraints[prop.name].blank,
                                inList    : mConstraints[prop.name].inList,
                                inListLabel: mConstraints[prop.name].attributes.inListLabel,
                                maxSize   : mConstraints[prop.name].maxSize,
                                minSize   : mConstraints[prop.name].minSize,
                                matches   : mConstraints[prop.name].attributes.matches? mConstraints[prop.name].attributes.matches :mConstraints[prop.name].attributes.regex,
                                notice    : mConstraints[prop.name].attributes.notice,
                                maskRe    : mConstraints[prop.name].attributes.maskRe,
                                mobile    : mConstraints[prop.name].attributes.mobile,
                                email     : mConstraints[prop.name].attributes.email,
                                password  : mConstraints[prop.name].attributes.password,
                                default   : mConstraints[prop.name].attributes.default,
                                unique    : mConstraints[prop.name].attributes.unique,
                                relation: mConstraints[prop.name].attributes.relation,
                                domain : mConstraints[prop.name].attributes.domain,
                                controller: mConstraints[prop.name].attributes.controller,
                                action    : mConstraints[prop.name].attributes.action,
                                gridview  : mConstraints[prop.name].attributes.gridview,
                                height    : mConstraints[prop.name].attributes.height,
                                displayField    : mConstraints[prop.name].attributes.displayField,
                                valueField      : mConstraints[prop.name].attributes.valueField,
                                items           : mConstraints[prop.name].attributes.items,
                                columns         : mConstraints[prop.name].attributes.columns, //用于定义一行显示几个checkbox
                                fields    : mConstraints[prop.name].attributes.fields,
                                fromDomain    : mConstraints[prop.name].attributes.fromDomain,
                                fromDomainId    : mConstraints[prop.name].attributes.fromDomainId,
                                afterEditorLabel : mConstraints[prop.name].attributes.afterEditorLabel
                        ]
                    } else if (map.type == "int") {
                        map["constraint"] = [
                                nullable  :  mConstraints[prop.name].attributes.nullable,
                                blank     : mConstraints[prop.name].blank,
                                max    : mConstraints[prop.name].max,
                                min    : mConstraints[prop.name].min,
                                default: mConstraints[prop.name].attributes.default,
                                gridview: mConstraints[prop.name].attributes.gridview,
                                inList    : mConstraints[prop.name].inList,
                                inListLabel: mConstraints[prop.name].attributes.inListLabel,
                                fromDomain    : mConstraints[prop.name].attributes.fromDomain,
                                fromDomainId    : mConstraints[prop.name].attributes.fromDomainId
                        ]
                    } else if (map.type == "long") {
                        map["constraint"] = [
                                max    : mConstraints[prop.name].max,
                                min    : mConstraints[prop.name].min,
                                default: mConstraints[prop.name].attributes.default,
                                relation: mConstraints[prop.name].attributes.relation,
                                domain : mConstraints[prop.name].attributes.domain,
                                inList    : mConstraints[prop.name].inList,
                                inListLabel: mConstraints[prop.name].attributes.inListLabel,
                                action    : mConstraints[prop.name].attributes.action,
                                displayField    : mConstraints[prop.name].attributes.displayField,
                                valueField    : mConstraints[prop.name].attributes.valueField,
                                fields    : mConstraints[prop.name].attributes.fields,
                                areaCombo   :   mConstraints[prop.name].attributes.areaCombo,
                                defaultProperties : mConstraints[prop.name].attributes?.defaultProperties,
                                nullable  :  mConstraints[prop.name].attributes.nullable,  //mConstraints[prop.name].nullable,
                                blank     : mConstraints[prop.name].blank
                        ]
                    } else if (map.type == "float") {
                        map["constraint"] = [
                                max   : mConstraints[prop.name].max,
                                min   : mConstraints[prop.name].min,
                                default: mConstraints[prop.name].attributes.default,
                                factor: mConstraints[prop.name].attributes.factor
                        ]
                    } else if (map.type == "double") {
                        map["constraint"] = [
                                max: mConstraints[prop.name].max,
                                min: mConstraints[prop.name].min,
                                default: mConstraints[prop.name].attributes.default
                        ]
                    } else if (map.type == "boolean") {
                        map["constraint"] = [
		                        isCheckbox: mConstraints[prop.name].attributes.isCheckbox,
                                inList    : mConstraints[prop.name].inList,
                                inListLabel: mConstraints[prop.name].attributes.inListLabel,
                                default: mConstraints[prop.name].attributes.default,
                                gridview: mConstraints[prop.name].attributes.gridview,
                                fromDomain    : mConstraints[prop.name].attributes.fromDomain,
                                fromDomainId    : mConstraints[prop.name].attributes.fromDomainId
                        ]
                    } else if (map.type == "date") {
                        map["constraint"] = [
                                nullable: mConstraints[prop.name].nullable,
                                minDate: mConstraints[prop.name].attributes.minDate,
                                maxDate: mConstraints[prop.name].attributes.maxDate,
                                default: mConstraints[prop.name].attributes.default
                        ]
                    }else if (map.type == "area") {
                        map["constraint"] = [
                                domain : mConstraints[prop.name].attributes.domain,
                                action    : mConstraints[prop.name].attributes.action,
                                fields    : mConstraints[prop.name].attributes.fields
                        ]
                    }else{ //其他的插件类型
                        map["constraint"] = [
                                nullable  : (mConstraints[prop.name].attributes.nullable!=null?mConstraints[prop.name].attributes.nullable:mConstraints[prop.name].nullable),
                                btnText:mConstraints[prop.name].attributes.btnText,
                                blank: mConstraints[prop.name].attributes.blank,
                                input:mConstraints[prop.name].attributes.input,
                                displayField    : mConstraints[prop.name].attributes.displayField,
                                valueField      : mConstraints[prop.name].attributes.valueField,
                                action:mConstraints[prop.name].attributes.action
                        ]
                    }

                    boolean isTrans = false
                    for (int k = 0; k < transProperties.size(); k++) {
                        if (prop.name == transProperties[k]) {
                            isTrans = true
                            break
                        }
                    }

                    if (isTrans) {
                        domainMap.transFields << map
                        return //在each循环里相当于continue
                    }

                    domainMap.transFields << map
                    domainMap.fields << map
                    domainMap.fieldsMap[prop.name] = map
                }
            } else {
                domainMap = [className: model, propertyName: model, m: [domain: [cn: Holders.grailsApplication.config.viewobjects."${model}".title], layout: [type: Holders.grailsApplication.config.viewobjects."${model}".type]], fields: [], transFields: [], fieldsMap: [:]]

                List<Map> fullFields = Holders.grailsApplication.config.viewobjects."${model}".fields

                for (int k = 0; k < fullFields.size(); k++) {
                    Map map = [
                            name: fullFields[k].name,
                            cn  : fullFields[k].cn,
                            type: fullFields[k].type,
                            widget: fullFields[k].widget,
                            flex: fullFields[k].flex ?: 1
                    ]

                    //TODO: lichb不需要给ViewObject约束吧，只读情况下，好像意义不大
                    Map mConstraints = fullFields[k].constraints ?: [:]

                    if (map.type == "string") {
                        map["constraint"] = [
                                nullable  : fullFields[k].nullable,
                                blank     : mConstraints.blank,
                                inList    : mConstraints.inList,
                                inListLabel: mConstraints.attributes?.inListLabel,
                                maxSize   : mConstraints.maxSize,
                                minSize   : mConstraints.minSize,
                                matches   : mConstraints.attributes?.matches? mConstraints.attributes?.matches: mConstraints.attributes?.regex,
                                notice    : mConstraints.attributes?.notice,
                                maskRe    : mConstraints.attributes?.maskRe,
                                mobile    : mConstraints.attributes?.mobile,
                                email     : mConstraints.attributes?.email,
                                password  : mConstraints.attributes?.password,
                                default   : mConstraints.attributes?.default,
                                unique    : mConstraints.attributes?.unique,
                                controller: mConstraints.attributes?.controller,
                                action    : mConstraints.attributes?.action,
                                gridview  : mConstraints.attributes?.gridview,
                                height    : mConstraints.attributes?.height,
                        ]
                    } else if (map.type == "int") {
                        map["constraint"] = [
                                max    : mConstraints.max,
                                min    : mConstraints.min,
                                default: mConstraints.attributes?.default,
                                gridview: mConstraints.attributes?.gridview,
                        ]
                    } else if (map.type == "long") {
                        map["constraint"] = [
                                max    : mConstraints.max,
                                min    : mConstraints.min,
                                default: mConstraints.attributes?.default,
                                relation: mConstraints.attributes?.relation,
                                domain : mConstraints.attributes?.domain,
		                        areaCombo  : mConstraints.attributes?.areaCombo,
                                defaultProperties : mConstraints.attributes?.defaultProperties
                        ]
                    } else if (map.type == "float") {
                        map["constraint"] = [
                                max   : mConstraints.max,
                                min   : mConstraints.min,
                                default: mConstraints.attributes?.default,
                                factor: mConstraints.attributes?.factor
                        ]
                    } else if (map.type == "double") {
                        map["constraint"] = [
                                max: mConstraints.max,
                                min: mConstraints.min,
                                default: mConstraints.attributes?.default
                        ]
                    } else if (map.type == "boolean") {
                        map["constraint"] = [
		                        isCheckbox: mConstraints.attributes?.isCheckbox,
                                inList    : mConstraints.inList,
                                inListLabel: mConstraints.attributes?.inListLabel,
                                default: mConstraints.attributes?.default
                        ]
                    } else if (map.type == "date") {
                        map["constraint"] = [
                                nullable: mConstraints.nullable,
                                minDate: mConstraints.attributes?.minDate,
                                maxDate: mConstraints.attributes?.maxDate,
                                default: mConstraints.attributes?.default
                        ]
                    }

                    domainMap.transFields << map
                    domainMap.fields << map
                    domainMap.fieldsMap[fullFields[k].name] = map
                }
            }

            domainModels[model] = domainMap
        }

        return domainModels[model]
    }
}