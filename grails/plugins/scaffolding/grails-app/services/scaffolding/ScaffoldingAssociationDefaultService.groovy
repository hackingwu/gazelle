package scaffolding

import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * 生成Float类型的表单插件
 * @author lichb
 * @version 2014-09-02
 */
class ScaffoldingAssociationDefaultService {
    static transactional = false

    def grailsApplication
    def ModelService
    def scaffoldingUtilService

    String Scaffolding(Map field, String mode,String domainName){
        def g = grailsApplication.mainContext.getBean(ApplicationTagLib.class)
        Map domain=ModelService.GetModel(field.type)
        Map constraint=field.constraint
        String action = constraint.action?:"list"
        List includeLst = field.include?.split(",")

        String output = ""
        String columnsString = ""

        for(int i=0; i<domain.fields.size();i++)
        {
            Map cons = domain.fields[i].constraint

            for(int k=0;k<includeLst?.size();k++){
                if(domain.fields[i].name ==includeLst[k]){
                    String func = ""
                    if(domain.fields[i].type=='boolean'){ //控制boolean的grid显示内容
                        func = ",renderer:function(value){\r\nreturn (value?'是':'否');\r\n}"
                    }else if(cons.inList && cons.inListLabel){  //控制带缩写的列表在grid列里的显示内容
                        String listVal = "{";
                        for(int m=0;m<cons.inList.size();m++){
                            listVal += """ '${cons.inList[m]}':'${cons.inListLabel[m]}'${m<(cons.inList.size()-1) ?",":"}"}"""
                        }
                        func = """,renderer:function(value){\r\nvar listArr=${listVal};\r\nreturn listArr[value];\r\n}"""
                    }else if(cons.relation && domain.fields[i].name=='parentId'){
                        func = ",renderer:function(value){\r\nreturn (value==''?'无':value);\r\n}"
                    }

                    if(cons.relation && cons.domain){
                        columnsString=columnsString+"""{header: '${domain.fields[i].cn}',flex: ${domain.fields[i].flex}, sortable: true, dataIndex: '${domain.fields[i].name}Value'"""+func+"""},"""
                    }else{
                        columnsString=columnsString+"""{header: '${domain.fields[i].cn}',flex: ${domain.fields[i].flex}, sortable: true, dataIndex: '${domain.fields[i].name}'"""+func+"""},"""
                    }

                    break
                }
            }
        }

        String fieldsString = ""
        for(int i=0; i<domain.fields.size();i++)
        {
            Map cons=domain.fields[i].constraint

            for(int k=0;k<includeLst?.size();k++){
                if(domain.fields[i].name ==includeLst[k]){
                    if(cons.relation && cons.domain){
                        fieldsString=fieldsString+"""'${domain.fields[i].name}Value',"""
                    }else{
                        fieldsString=fieldsString+"""'${domain.fields[i].name}',"""
                    }
                    break
                }
            }
        }

        output = """
                {
                    xtype: 'selectfield',
                    fieldLabel: '${field.cn}',
                    mode:'${mode}',
                    name: '${field.name}',
                    displayField: '${constraint.displayField?constraint.displayField:(includeLst?includeLst[0]:'name')}',
                    valueField: '${constraint.valueField?constraint.valueField:'id'}',
                    columns:[
                        ${columnsString.size()==0?"""{header: '${domain.fields[0].cn}',flex: ${domain.fields[0].flex}, sortable: true, dataIndex: '${domain.fields[0].name}'}""":(columnsString.substring(0,columnsString.size()-1))}
                    ],
                    fields:[
                        ${fieldsString.size()==0?'"name"':(fieldsString.substring(0,fieldsString.size()-1))}
                    ],
                    url: '${g.createLink([controller: field.type.capitalize(), action:"${action}"])}'
            """

        if(constraint.blank!=true && constraint.nullable!=true)
        {
            output = output +""", allowBlank: false, afterLabelTextTpl: required"""
        }

        if(field.register || field.trigger){
            output = output + """
                ,listeners: {
                    ${scaffoldingUtilService.getTriggerFieldScript(field, mode, domainName)}
                    ${scaffoldingUtilService.getRegisterFieldsScript(field, mode, domainName)}
                }
            """
        }

        output = output + " }"

        return output
    }
}
