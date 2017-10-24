package scaffolding

import framework.ModelService
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.util.StringUtils

/**
 * 生成String类型的表单插件
 * @author lichb
 * @version 2014-09-02
 */
class ScaffoldingStringService {
    static transactional = false

    def grailsApplication
    def scaffoldingUtilService

    String Scaffolding(Map field, String mode, String domainName){
        def g = grailsApplication.mainContext.getBean(ApplicationTagLib.class)
        String controllerName = g.controllerName
        Map constraint = field.constraint

        String output = ""

        if(constraint.inList && constraint.inListLabel){
            output = output + """{name: '${field.name}', xtype: 'combo',fieldLabel: '${field.cn}',editable:false,store: Ext.create('Ext.data.Store', { fields: ['display', 'value'],data: ["""

            for(int i=0;i < constraint.inList.size(); i++)
            {
                output =output + """{'display':'${constraint.inListLabel[i]}', 'value':'${constraint.inList[i]}'}${i<(constraint.inList.size()-1)?",":""}"""
            }

            output =output + """]}),queryMode: 'local',valueField: 'value',displayField: 'display',value:"${constraint.default?: constraint.inList[0]}"${mode=="detail"?", readOnly:true":""}"""
        }else if(constraint.inList){
            output = output + """{name: '${field.name}', xtype: 'combo',fieldLabel: '${field.cn}',editable:false,store: Ext.create('Ext.data.Store', { fields: ['value'],data: ["""

            for(int i=0;i < constraint.inList.size(); i++)
            {
                output =output + """{'value':'${constraint.inList[i]}'}${i<(constraint.inList.size()-1)?",":""}"""
            }

            output =output + """]}),queryMode: 'local',valueField: 'value',displayField: 'value',value:"${constraint.default?: constraint.inList[0]}"${mode=="detail"?", readOnly:true":""}"""
        }else if(field.widget == "checkbox"){ //对string类型为checkbox的处理
            String items = ""

            String detail = ""
            if(mode == "detail")
            {
                detail = "readOnly:true,"
            }

            if(constraint.items){ //items已在domain class中定义
                List<Map> itemsList=constraint.items
                for(int i=0;i < itemsList.size(); i++){
                    items += """{ boxLabel: '${itemsList[i].label}', ${detail} name: '${field.name}', inputValue: '${itemsList[i].value}' }${(itemsList.size-1==i)?'':','}"""
                }
            }else{ //items未定义,从domain取
                List modelDatas = ModelService.GetModelDataByDomainName(constraint.domain)
                for(int i=0;i < modelDatas.size(); i++){
                    items += """{ boxLabel: '${modelDatas[i][constraint.displayField]}', ${detail} name: '${field.name}', inputValue: '${modelDatas[i][constraint.valueField]}' }${(modelDatas.size-1==i)?'':','}"""
                }
            }

            output =output + """
                {
                    xtype: 'checkboxgroup',
                    fieldLabel: '${field.cn}',
                    columns: ${constraint.columns?:'4'},
                    items: [
                        ${items}
                    ]
                """
            if(constraint.blank!=true || constraint.nullable!=true)
            {
                output = output +""", allowBlank: false, blankText: '请至少选择一个项目', afterLabelTextTpl: required"""
            }
            output=output+"""
                }
            """
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

        }else if(field.widget == "combo"){ //对string类型为combo的处理
            String items =""
            if(constraint.items) { //items已在domain class的field字段中定义
                //to do
            }else{ //items未定义,通过ajax取
                String displayField = constraint.displayField?:"text"
                String valueField = constraint.valueField?:"id"
                String dataFields = constraint.fields?:"['id', 'text']"
                String action = constraint.action?:"association"

                output = """
                {
                    xtype: 'combo',
                    fieldLabel: '${field.cn}',
                    name: '${field.name}',
                    editable: false,
                    pageSize:2,
                    typeAhead:true,
                    minChars:1,
                    queryParam:'searchValue',
                    displayField:'${displayField}',
                    valueField:'${valueField}',
                    store:Ext.create('Ext.data.Store', {
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
                    })
                 """
                if(constraint.blank!=true || constraint.nullable!=true)
                {
                    output = output +""", allowBlank: false, afterLabelTextTpl: required"""
                }

                if(constraint.default!=null){
                    output = output + ", value:'${constraint.default}'"
                }

                output = output + "}"
            }
        }else if(field.widget == "fileuploader"){

            if(mode == "create") {
                output = output + """{xtype:'fileuploadfield',fieldLabel: '${field.cn}',name: '${field.name}',type:'upload',url:'${g.createLink(controller:controllerName , action: "fileUpload")}'}"""
            }else if (mode == "update"){
                output = output + """{xtype:'fileuploadfield',fieldLabel: '${field.cn}',name: '${field.name}',type:'all',url:'${g.createLink(controller: controllerName, action: "fileUpload")}'}"""
            }else{
                output = output + """{xtype:'fileuploadfield',fieldLabel: '${field.cn}',name: '${field.name}',type:'download',url:'${g.createLink(controller: controllerName, action: "fileUpload")}'}"""
            }
            //output = output + "${mode=="detail"?", readOnly:true":""}"
        }else if(field.widget == "imageuploader"){
            if(mode == "create") {
                output = output + """{xtype: 'hiddenfield',name: '${field.name}',itemId:'${field.name}'},{xtype:'imageuploadfield',fieldLabel: '${field.cn}',name: '${field.name}Btn',type:'upload',url:'${g.createLink(controller: controllerName, action: "imageUpload")}'}"""
            }else if (mode == "update"){
                output = output + """{xtype:'imageuploadfield',fieldLabel: '${field.cn}',name: '${field.name}Update',type:'all',url:'${g.createLink(controller: controllerName, action: "imageUpload")}'}"""
            }else{
                output = output + """{xtype:'imageuploadfield',fieldLabel: '${field.cn}',name: '${field.name}Detail',type:'preview',url:'${g.createLink(controller: controllerName, action: "imageUpload")}'}"""
            }
        }else if(field.widget == "multifileuploader"){
            output = output + """
                    {
                        xtype: 'fieldcontainer',
                        fieldLabel: '${field.cn}',
                        layout: 'hbox',
                        combineErrors: true,
                        items: [
                            {
                                xtype: 'button',
                                text: '上传',
                                handler: function(){
                                    var upload = Ext.create('Ext.multiUpload.Panel', {
                                        height: 300,
                                        uploadConfig: {
                                            url: '${g.resource([plugin: "scaffolding", dir: "js/multiUpload", file: "Upload.swf"])}',
                                            buttonImagePath: '${g.resource([plugin: "scaffolding", dir: "js/multiUpload", file: "button.gif"])}',
                                            buttonImageHoverPath: '${g.resource([plugin: "scaffolding", dir: "js/multiUpload", file: "button_hover.gif"])}',
                                            uploadUrl:'${g.createLink(controller: constraint.controller, action: constraint.action)}', //上传路径
                                            maxFileSize: 4 * 1024 * 1024, //可上传的文件大小
                                            maxQueueLength: 5//, //可同时上传的文件数
                                            //fileFilters: 'Images (*.jpg)|*.jpg', //可上传的文件类型
                                        }
                                    });
                                    Ext.create('Ext.Window', {
                                        title: '文件上传',
                                        width: 550,
                                        modal: true,
                                        resizable: false,
                                        buttonAlign: 'center',
                                        items:[upload],
                                        buttons: [{text: '关闭',handler: function(){ this.up('window').close();}}]
                                    }).show();
                                }
                            }
                        ]
                    }
                """
        }else{
            String edtHeight = ""
            if(constraint.height){
                edtHeight = "height:${constraint.height},"
            }
            output ="""{fieldLabel: '${field.cn==""?"&nbsp;":field.cn}', ${edtHeight} name: '${field.name}'"""

            if(field.cn==""){
                output += """, width:465, labelSeparator: ''"""
            }

            if(field.widget)
            {
                output = output + """, xtype: '${field.widget}'"""
            }

            if(mode!="detail")
            {
                if(constraint.blank!=true || constraint.nullable!=true)
                {
                    output = output +""", allowBlank: false"""
                    if(field.cn!=""){
                        output += """, afterLabelTextTpl: required"""
                    }
                }

                if(constraint.maxSize || constraint.max)
                {
                    output = output +""", maxLength: ${constraint.maxSize?:constraint.max}"""
                }

                if(constraint.minSize || constraint.min)
                {
                    output = output +""", minLength: ${constraint.minSize?:constraint.min}"""
                }

                if(constraint.default!=null){
                    output = output + ", value:'${constraint.default}'"
                }

                if(constraint.matches)
                {
                    output = output +""", regex:/${constraint.matches}/"""

                    if(constraint.notice)
                    {
                        output = output +""", regexText:'${constraint.notice}'"""
                    }
                    if(constraint.password)
                    {
                        output = output + """,inputType: 'password' """
                    }
//	                if(constraint.maskRe)
//	                {
//		                output = output +""", maskRe:/${constraint.maskRe}/"""
//	                }
                }else if(constraint.mobile)
                {
                    output = output + """, maskRe: /[\\d]/, regex: /^1\\d{10}\$/, regexText: '请输入正确的手机号码'"""
                }else if(constraint.email)
                {
                    output = output + """, vtype: 'email'"""
                }
                if(constraint.maskRe)
                {
                    output = output +""", maskRe:/${constraint.maskRe}/"""
                }

                if(field.vtype!=null){
                    String modeStr = StringUtils.capitalize(mode)
                    output = output + """,vtype:'${field.vtype}',${field.vtype}:{form:'${controllerName}${modeStr}Wnd'}"""
                }
                if(field.blur!=null){
                    output = output + """
                      , listeners: {
                            blur: function (self, e, opts) {
                                ${field.blur}(self, e, opts);
                            }
                        }
                    """
                }

            }else{
                output = output + ", readOnly:true"
            }
        }

        if(field.widget != "fileuploader" && field.widget != "imageuploader" && field.widget != "multifileuploader" && field.widget!="checkbox" && field.widget!="combo" && field.widget!="singleselectfield") {
            if(field.register || field.trigger){
                output = output + """
                    ,listeners: {
                        ${scaffoldingUtilService.getTriggerFieldScript(field, mode, domainName)}
                        ${scaffoldingUtilService.getRegisterFieldsScript(field, mode, domainName)}
                    }
                """
            }
            output = output + "}"


//            if(field.cn==""){
              if(constraint.afterEditorLabel){
                output = """
                    {
                        xtype: 'fieldcontainer',
                        combineErrors: true,
                        layout: 'hbox',
                        defaultType:'textfield',
                        items: [
                            ${output},
                            {xtype: 'displayfield',name:'${field.name}Field',value: '（${constraint.afterEditorLabel}）'
                """

                if(field.register || field.trigger){
                    output += """
                    ,listeners: {
                        ${scaffoldingUtilService.getTriggerFieldScript(field, mode, domainName)}
                        ${scaffoldingUtilService.getRegisterFieldsScript(field, mode, domainName)}
                    }
                """
                }

                output +="""}
                        ]
                    }"""
            }
        }

        return output
    }
}
