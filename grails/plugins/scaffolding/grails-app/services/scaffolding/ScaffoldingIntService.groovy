package scaffolding
/**
 * 生成Int类型的表单插件
 * @author lichb
 * @version 2014-09-02
 */
class ScaffoldingIntService {
    static transactional = false

    def grailsApplication
    def scaffoldingUtilService

    String Scaffolding(Map field, String mode,String domainName){
        Map constraint = field.constraint
        String output = """{xtype: 'numberfield', fieldLabel: '${field.cn}', name: '${field.name}', allowDecimals: false"""

        if(field.widget == "radio"){
            output = """{xtype: 'radiogroup',fieldLabel: '${field.cn}', columns: [120,120,120,120], name:'${field.name}${mode}Grp', items: ["""

            String detail = ""
            if(mode == "detail")
            {
                detail = "readOnly:true,"
            }

            for(int i=0;i < constraint.inList.size(); i++)
            {
                String defaultVal = ""
                if(constraint.default==constraint.inList[i]){
                    defaultVal = "checked: true,"
                }

                output =output + """{boxLabel:'${constraint.inListLabel[i]}', ${detail} ${defaultVal} name: '${field.name}', inputValue:${constraint.inList[i]}}${i<(constraint.inList.size()-1)?",":""}"""
            }

            output =output + """]"""
        }else if(constraint.inList && constraint.inListLabel){
            output = """{xtype: 'combo',fieldLabel: '${field.cn}', name: '${field.name}', editable:false,store: Ext.create('Ext.data.Store', { fields: ['display', 'value'],data: ["""

            for(int i=0;i < constraint.inList.size(); i++)
            {
                output =output + """{'display':'${constraint.inListLabel[i]}', 'value':${constraint.inList[i]}}${i<(constraint.inList.size()-1)?",":""}"""
            }

            output =output + """]}),queryMode: 'local',valueField: 'value',displayField: 'display',value:${constraint.default?: constraint.inList[0]}"""
        }

        if(constraint.max!=null)
        {
            output = output + """, maxValue: ${constraint.max}"""
        }

        if(constraint.min!=null)
        {
            output = output + """, minValue: ${constraint.min}"""
        }

        if(constraint.blank!=true || constraint.nullable!=true)
        {
            output = output +""", allowBlank: false, afterLabelTextTpl: required"""
        }

        if(mode == "detail")
        {
            output = output + ", readOnly:true"
        }else if(constraint.default!=null){
            output = output + ", value:${constraint.default}"
        }

        if(field.register || field.trigger){
            output = output + """
                ,listeners: {
                    ${scaffoldingUtilService.getTriggerFieldScript(field, mode, domainName)}
                    ${scaffoldingUtilService.getRegisterFieldsScript(field, mode, domainName)}
                }
            """
        }

        output = output + "}"

        return output
    }
}
