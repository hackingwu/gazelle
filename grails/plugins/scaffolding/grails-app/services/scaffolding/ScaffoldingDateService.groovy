package scaffolding

import framework.ModelService
import grails.transaction.Transactional
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * 生成Date类型的表单插件
 * @author lichb
 * @version 2014-09-02
 */
class ScaffoldingDateService {
    static transactional = false
    def grailsApplication
    def scaffoldingUtilService

    String Scaffolding(Map field, String mode,String domainName){
        Map constraint = field.constraint

        String output = """{fieldLabel: '${field.cn}', name: '${field.name}'"""

        if(field.widget){
            if(field.widget=='datefield'){
                output += """, format: 'Y-m-d'"""
            }
            output += """,editable: false, xtype:'${field.widget}'""" //=datefield or datetimefield
        }

        if(constraint.nullable!=true)
        {
            output += """, allowBlank: false, afterLabelTextTpl: required"""
        }

        if(constraint.maxDate!=null)
        {
            output += """, maxValue: Ext.Date.add(new Date(), Ext.Date.DAY, ${constraint.maxDate})"""
        }

        if(constraint.minDate!=null)
        {
            output += """, minValue: Ext.Date.add(new Date(), Ext.Date.DAY, ${constraint.minDate})"""
        }

        if(mode == "detail")
        {
            output += ", readOnly:true"
        }else if(constraint.default!=null){
            output += ", value:Ext.Date.add(new Date(), Ext.Date.DAY, ${constraint.default})"
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
