package scaffolding

import framework.ModelService

/**
 * 生成Boolean类型的表单插件
 * @author lichb
 * @version 2014-09-02
 */
class ScaffoldingBooleanService {
    static transactional = false

    def grailsApplication
    def scaffoldingUtilService

    String Scaffolding(Map field, String mode, String domainName){
        Map constraint = field.constraint
//	    String width= attrs.width?: "100"
        if(constraint.isCheckbox){
            String output = """{xtype: 'checkboxgroup', name: '${field.name}', fieldLabel: '${field.cn}', items:[{boxLabel:"${(constraint.inListLabel)?constraint.inListLabel[0]:'示例'}",inputValue: true"""
            if(constraint.default!=null){
                if(constraint.default){
                    output += """,checked: true}]"""
                }else{
                    output += """}]"""
                }
            }else{
                output += """}]"""
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
        }else{
            String output = """{xtype: 'radiogroup', fieldLabel: '${field.cn}', columns: [120,120], items:[{boxLabel:"${(constraint.inListLabel)?constraint.inListLabel[0]:'是'}",name: '${field.name}',inputValue: 'true'"""
            if(constraint.default!=null){
                if(constraint.default){
                    output += """,checked: true},{boxLabel:"${(constraint.inListLabel)?constraint.inListLabel[1]:'否'}",name: '${field.name}',inputValue: 'false'}]"""
                }else{
                    output += """},{boxLabel:"${(constraint.inListLabel)?constraint.inListLabel[1]:'否'}",name: '${field.name}',inputValue: 'false',checked: true}]"""
                }
            }else{
                output += """},{boxLabel:"${(constraint.inListLabel)?constraint.inListLabel[1]:'否'}",name: '${field.name}',inputValue: 'false'}]"""
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
}
