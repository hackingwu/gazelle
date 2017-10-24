package scaffolding

import framework.ModelService
import org.codehaus.groovy.grails.commons.GrailsDomainClass

/**
 * Scaffolding模块公共处理函数
 * @author lichb
 * @version 2014-09-05
 */
class ScaffoldingUtilService {
    static transactional = false

    def grailsApplication

    /**
     * 判断当前字段是否属于对象关联
     * @param type 字段类型
     * @return true 是对象字段，false 不是对象字段
     */
    boolean isOne2OneFieldType(String type){
        boolean isObj = true

        type = type.toLowerCase()

        if(type=='string' || type=='date' || type=='int' || type=='long' || type=='float' || type=='double' || type=='boolean' || type == 'list'){
            isObj = false
        }

        return isObj
    }

    boolean isOne2ManyFieldType(String type){
        boolean isObj = true

        type = type.toLowerCase()

        if(type=='string' || type=='date' || type=='int' || type=='long' || type=='float' || type=='double' || type=='boolean'){
            isObj = false
        }

        return isObj
    }

    /**
     * 返回关联对象的脚步处理
     * @param mode 操作类型：‘update’，‘detail’
     * @param fields 某个domain对象的关联字段
     * @return 关联对象的动态脚步
     */
    String getAllOne2OneFieldsScript(String mode, List<Map> fields=[]){
        //关联对象代码生成
        String one2OneFieldString = "try{"
        for(int i=0;i<fields.size();i++)
        {
            if(this.isOne2OneFieldType(fields[i].type)){
                one2OneFieldString += """
                        var ${fields[i].name}Combo = Ext.ComponentQuery.query("#${fields[i].name}${mode}SelCombo")[0];
                        Ext.apply(${fields[i].name}Combo.getStore().proxy.extraParams, {search:'[{"key":"id","value":"'+action.result.data.${fields[i].name}.id+'"}]'});
                        ${fields[i].name}Combo.getStore().reload();
                """
            }
        }

        one2OneFieldString += """
            }catch(e){
                //error
            }
        """

        return one2OneFieldString
    }

    /**
     * 返回通过trigger注册的事件
     * @param field 当前字段名
     * @param mode 操作类型：‘update’，‘detail’
     * @param domainName  当前field所在domain对象的名称
     * @return 关联对象的动态脚步
     */
    static String getTriggerFieldScript(Map field, String mode, String domainName){
        String outputScript = ""
        if(field.trigger){
            List nameList=[]
            Map domain=ModelService.GetModel(domainName)
            for(int i=0; i<domain.fields.size();i++){
                if(domain.fields[i].register && domain.fields[i].register.listeners == field.trigger.trigger){
                    if(domain.fields[i].widget=='area'){
                        nameList.push(domain.fields[i].name+"Ctn")
                    }else{
                        nameList.push(domain.fields[i].name)
                        if(domain.fields[i].constraint.afterEditorLabel){ //afterEditorLabel控件也要做隐藏操作
                            nameList.push(domain.fields[i].name+"Field")
                        }
                    }
                }
            }

            outputScript = """
                '${field.trigger.listeners}': function(){
                    var triggerObjList = "${nameList.join(",")}";
                    Ext.eventcore.fireEvents("${field.trigger.trigger}", ${domainName}${mode.capitalize()}Form, triggerObjList,this.getValue().${field.name});
                }
            """
        }

        return outputScript
    }

    /**
     * 返回通过register注册的事件
     * @param field 当前字段名
     * @param mode 操作类型：‘update’，‘detail’
     * @param domainName  当前field所在domain对象的名称
     * @return 关联对象的动态脚步
     */
    String getRegisterFieldsScript(Map field, String mode, String domainName){
        String outputScript = ""
        String action = ""
        String func = ""

        if(field.register){
            if(field.register.func){
                func = """
                if(typeof(${field.register.func})=='function'){
                    ${field.register.func}(arg,this);
                }
            """
            }

            if(field.register.defaultAction){
                //是否必填，根据domain定义决定
                String allowBlank = "true"
                if(field.constraint.blank!=true || field.constraint.nullable!=true)
                {
                    allowBlank = "false"
                }

                action = """switch(arg.value.toString()){"""
                for(int m=0;m< field.register.defaultAction.size();m++){

                    String bankStript = """this.allowBlank = ${field.register.defaultAction[m].action=='hide'?'true':allowBlank};"""
                    if(!(['int','long','string','date'].contains(field.type))){ //一对一或一对多控件
                        bankStript = """Ext.ComponentQuery.query("#${field.name}${mode}SelCombo")[0].allowBlank=${field.register.defaultAction[m].action=='hide'?'true':allowBlank};"""
                    }else if(field.widget=='area'){ //区域类型可选
                        List cons = field.constraint.areaCombo?.split(",")
                        Map<Map> defaultProperties = field.constraint.defaultProperties
                        if(defaultProperties != null){
                            defaultProperties.each {
                                if(it.value.hidden){
                                    cons.remove(it.key)
                                }
                            }
                        }
                        if(field.register.defaultAction[m].action=='hide'){
                            for(int k=0;k<cons.size();k++){
                                bankStript += """
                                    var selfCombo${k} = Ext.ComponentQuery.query("#${mode}_combo_${cons[k]}")[0];
                                    selfCombo${k}.clearValue();
                                    selfCombo${k}.allowBlank = true;
                                    selfCombo${k}.hide();
                                """
                            }
                        }else{
                            for(int k=0;k<cons.size();k++){
                                bankStript += """
                                    var selfCombo${k} = Ext.ComponentQuery.query("#${mode}_combo_${cons[k]}")[0];
                                    selfCombo${k}.allowBlank = ${allowBlank};
                                    selfCombo${k}.show();
                                """
                            }
                        }
                    }

                    action += """
                    case '${field.register.defaultAction[m].value}':
                        ${bankStript}
                        this.${field.register.defaultAction[m].action}();
                        break;
                """
                }
                action +="""}"""
            }

            outputScript = """
                 ${field.trigger?",":""}
                '${field.register.listeners}': function(arg){
                    ${action}
                    ${func}
                }
            """
        }

        return outputScript
    }

    String getCheckbox(Map field,String mode){
        String items = ""
        Map constraint = field.constraint
        String detail = ""
        if (mode == "detail"){
            detail = "readOnly:true,"
        }

        List modelDatas = ModelService.GetModelDataByDomainName(constraint.domain)
        for(int i=0;i < modelDatas.size(); i++){
            items += """{ boxLabel: '${modelDatas[i][constraint.displayField]}', ${detail} name: '${field.name}', inputValue: '${modelDatas[i][constraint.valueField]}' }${(modelDatas.size()-1==i)?'':','}"""
        }
        return """
                {
                    xtype: 'checkboxgroup',
                    fieldLabel: '${field.cn}',
                    columns: ${constraint.columns?:'4'},
                    items: [
                        ${items}
                    ]
                    ${!constraint.nullable?",afterLabelTextTpl:required, blankText: '请至少选择一个项目', allowBlank: false":""}
                }
                """
    }

}
