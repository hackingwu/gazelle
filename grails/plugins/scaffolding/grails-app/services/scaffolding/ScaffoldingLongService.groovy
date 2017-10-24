package scaffolding

import framework.ModelService
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * 生成Long类型的表单插件
 * @author lichb
 * @version 2014-09-02
 */
class ScaffoldingLongService {
    static transactional = false

    def grailsApplication

    String Scaffolding(Map field, String mode,String domainName){
        def g = grailsApplication.mainContext.getBean(ApplicationTagLib.class)
        String controllerName = g.controllerName
        Map constraint = field.constraint
        String output = """{xtype: 'numberfield', fieldLabel: '${field.cn}', name: '${field.name}', allowDecimals: false"""

        if(constraint.inList && constraint.inListLabel){
            output = """{xtype: 'combo',fieldLabel: '${field.cn}', name: '${field.name}', editable:false,store: Ext.create('Ext.data.Store', { fields: ['display', 'value'],data: ["""

            for(int i=0;i < constraint.inList.size(); i++)
            {
                output =output + """{'display':'${constraint.inListLabel[i]}', 'value':${constraint.inList[i]}}${i<(constraint.inList.size()-1)?",":""}"""
            }

            output =output + """]}),queryMode: 'local',valueField: 'value',displayField: 'display',value:${constraint.default?: constraint.inList[0]}"""
        }else if(constraint.relation && constraint.domain){ //存在与其他表的关联关系时，不显示id，而显示名称，例如:oneToManyUpDown
            String displayField = constraint.displayField?:"text"
            String valueField = constraint.valueField?:"id"
            String dataFields = constraint.fields?:"['id', 'text']"
            String action = constraint.action?:"association"

            String required = ""
            if(!constraint.nullable || !constraint.blank){
                required = "allowBlank: false, afterLabelTextTpl: required,"
            }

            output = """{xtype: 'hiddenfield', name: '${field.name}',itemId:'${field.name}',value:-1},
                        {xtype: 'combo', fieldLabel: '${field.cn}', name: '${field.name}Value', editable: false, pageSize:2, typeAhead:true, minChars:1, queryParam:'searchValue',${required}
                        listeners: {
                            change: function(me, newVal, oldVal, opts){
                                if(!isNaN(newVal)){
                                    this.up().down("#${field.name}").setValue(newVal);
                                }
                            }
                        },
                         displayField:'${displayField}', valueField:'${valueField}', store:Ext.create('Ext.data.Store', {
                            autoLoad: true,
                            pageSize:10,
                            fields: ${dataFields},
                            proxy: {
                                type: 'ajax',
                                url: '${g.createLink([controller: constraint.domain, action:"${action}"])}',
                                reader: {
                                    type: 'json',
                                    root: 'data',
                                    successProperty: 'success',
                                    messageProperty: 'message',
                                    totalProperty: 'totalCount'
                                }
                            }
                        })"""
        }else if((field.widget == "singleselectfield") || (field.widget == "multilselectfield")){ //单选 //todo:多选
            Map domain=ModelService.GetModel(constraint.domain)
            String action = constraint.action?:"list"
            List includeLst = field.include?.split(",")

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
                    url: '${g.createLink([controller: constraint.domain, action:"${action}"])}'
            """

            if(constraint.blank!=true || constraint.nullable!=true)
            {
                output = output +""", allowBlank: false, afterLabelTextTpl: required"""
            }

            output = output + " }"

        }else{
            if(constraint.max != null){
                output = output + """, maxText: '最大值不能大于 {0}', maxValue: ${constraint.max}"""
            }

            if(constraint.min != null){
                output = output + """, minText: '最小值不能小于 {0}' ,negativeText: '值不能为负数' , minValue: ${constraint.min}"""
            }

            if(constraint.blank!=true || constraint.nullable!=true)
            {
                output = output +""", allowBlank: false, afterLabelTextTpl: required"""
            }

            if(constraint.default!=null)
            {
                output = output + ", value:'${constraint.default}'"
            }
        }

        if(field.widget!="singleselectfield") {
            if(mode == "detail")
            {
                output = output + ", readOnly:true"
            }else if(constraint.default!=null){
                output = output + ", value:${constraint.default}"
            }
            output = output + "}"
        }

        return output
    }
}
