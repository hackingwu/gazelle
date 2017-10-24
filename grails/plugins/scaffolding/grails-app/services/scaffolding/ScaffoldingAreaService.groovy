package scaffolding

import framework.util.StringUtil
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * 生成区域插件
 * @author lichb
 * @version 2014-08-01
 */
class ScaffoldingAreaService {
    def grailsApplication
    def scaffoldingUtilService

    String Scaffolding(Map field, String mode,String domainName){

        String output = ""
        Map constraint = field.constraint

        String required = "";
        String allowBlank = "true";
        if(constraint.blank!=true || constraint.nullable!=true){
            required = "afterLabelTextTpl: required,"
            allowBlank = "false"
        }

        //一次性生成4级区域联动
	    if(!StringUtil.isEmpty(constraint.areaCombo)){
		    List areaNames = constraint.areaCombo.split(',')
            Map<Map> defaultProperties = constraint.defaultProperties
            //如果是隐藏起来则hidden为true，同时value也为必填
            Map property0 = defaultProperties?.get(areaNames[0])
            Map property1 = defaultProperties?.get(areaNames[1])
            Map property2 = defaultProperties?.get(areaNames[2])
            Map property3 = defaultProperties?.get(areaNames[3])
            output += """
                {
                    xtype: 'fieldcontainer',
                    fieldLabel: '${field.cn}',
                    ${required}
                    name: '${areaNames[0]}Ctn',
                    layout: 'hbox',
                    combineErrors: true,
                    items: [
                        {
                            xtype: 'combo',
                            name: '${areaNames[0]}',
                            itemId: '${mode}_combo_${areaNames[0]}',
                            displayField: 'areaName',
                            valueField: 'areaId',
                            ${property0?.get("hidden")?"hidden:true,":""}
                            ${property0?.get("value")?"value:"+property0.get("value")+",":""}
                            editable: false,
                            ${mode=='detail'?'readOnly:true,':''}
                            allowBlank: ${allowBlank},
                            store: Ext.create('Ext.data.Store', {
                                autoLoad: true,
                                fields: ['id','areaId','areaName', 'parentId', 'level'],
                                pageSize:1000,
                                proxy: {
                                    type: 'ajax',
                                    url: '${grailsApplication.mainContext.getBean(ApplicationTagLib.class).createLink(controller: constraint.domain, action: constraint.action)}',
                                    reader: {
                                        type: 'json',
                                        root: 'data',
                                        successProperty: 'success',
                                        messageProperty: 'message',
                                        totalProperty: 'totalCount'
                                    }
                                },
                                listeners: {
                                    beforeload: function (proxy, response, operation) {
                                        Ext.apply(this.proxy.extraParams, {search:'[{"key":"parentId","value":"-1"}]'});
                                    }
                                    ,
                                    load:function(_self,records,successful){
                                        if(successful){
                                            var selfCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[0]}")[0];
                                            ${
                                                if(property0 && property0.get("value")){
                                                    """
                                                        var isExist = false;
                                                        for(var k=0;k<records.length;k++){
                                                            if(records[k].data.areaId==${property0.get("value")}){
                                                                isExist=true;
                                                                break;
                                                            }
                                                        }
                                                        if(isExist){
                                                            ${property0.get("value")?"selfCombo.setValue("+property0.get("value")+")":""}
                                                        }
                                                    """
                                                }
                                            }
                                        }
                                    }
                                }
                            }),
                            listeners: {
                                change: function(me, newVal, oldVal, opts){
                                    if(newVal){ //Bug fixed:防止置空时，激活联动
                                        var combo1 = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[1]}")[0];
                                        var combo2 = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[2]}")[0];
                                        var combo3 = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[3]}")[0];
                                        if(newVal == 100000){
                                            combo1.allowBlank = ${allowBlank};
                                            combo2.allowBlank = ${allowBlank};
                                            combo3.allowBlank = ${allowBlank};
                                            combo1.show();
                                            combo2.show();
                                            combo3.show();
                                            combo1.getStore().reload();
                                        }else{ //其他，则无下级联动
                                            combo1.clearValue();
                                            combo2.clearValue();
                                            combo3.clearValue();
                                            combo1.allowBlank = true;
                                            combo2.allowBlank = true;
                                            combo3.allowBlank = true;
                                            combo1.hide();
                                            combo2.hide();
                                            combo3.hide();
                                        }
                                    }
                                }
                            }
                        },
                        {
                            xtype: 'combo',
                            name: '${areaNames[1]}',
                            itemId: '${mode}_combo_${areaNames[1]}',
                            displayField: 'areaName',
                            valueField: 'areaId',
                            ${property1?.get("hidden")?"hidden:true,":""}
//                            ${property1?.get("value")?"value:"+property1.get("value")+",":""}
                            editable: false,
                            ${mode=='detail'?'readOnly:true,':''}
                            allowBlank: ${allowBlank},
                            store: Ext.create('Ext.data.Store', {
                                autoLoad: false,
                                fields: ['id','areaId','areaName', 'parentId', 'level'],
                                pageSize:1000,
                                proxy: {
                                    type: 'ajax',
                                    url: '${grailsApplication.mainContext.getBean(ApplicationTagLib.class).createLink(controller: constraint.domain, action: constraint.action)}',
                                    reader: {
                                        type: 'json',
                                        root: 'data',
                                        successProperty: 'success',
                                        messageProperty: 'message',
                                        totalProperty: 'totalCount'
                                    }
                                },
                                listeners: {
                                    beforeload: function (proxy, response, operation) {
                                        var preCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[0]}")[0];
                                        var value = preCombo.getValue();
                                         if(!value){ //Bug fixed:防止前一级还没加载就加载下一级数据
                                            return false;
                                        }
                                        Ext.apply(this.proxy.extraParams, {search:'[{"key":"parentId","value":"100000"}]'});
                                    }
                                    ,
                                    load:function(_self,records,successful){
                                        if(successful){
                                            var selfCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[1]}")[0];
                                            ${
                                                if(property1 && property1.get("value")){
                                                    """
                                                        var isExist = false;
                                                        for(var k=0;k<records.length;k++){
                                                            if(records[k].data.areaId==${property1.get("value")}){
                                                                isExist=true;
                                                                break;
                                                            }
                                                        }
                                                        if(isExist){
                                                            ${property1.get("value")?"selfCombo.setValue("+property1.get("value")+")":""}
                                                        }
                                                    """
                                                }
                                            }
                                        }

                                    }
                                }
                            }),
                            listeners: {
                                change: function(me, newVal, oldVal, opts){
                                    var nextCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[2]}")[0];
                                    ${mode=='create'?'nextCombo.clearValue();':''}
                                    nextCombo.getStore().reload();
                                }
                            }
                        },
                        {
                            xtype: 'combo',
                            name: '${areaNames[2]}',
                            itemId: '${mode}_combo_${areaNames[2]}',
                            displayField: 'areaName',
                            valueField: 'areaId',
                            ${property2?.get("hidden")?"hidden:true,":""}
//                            ${property2?.get("value")?"value:"+property2.get("value")+",":""}
                            editable: false,
                            allowBlank: ${allowBlank},
                            ${mode=='detail'?'readOnly:true,':''}
                            store: Ext.create('Ext.data.Store', {
                                autoLoad: false,
                                fields: ['id','areaId','areaName', 'parentId', 'level'],
                                pageSize:1000,
                                proxy: {
                                    type: 'ajax',
                                    url: '${grailsApplication.mainContext.getBean(ApplicationTagLib.class).createLink(controller: constraint.domain, action: constraint.action)}',
                                    reader: {
                                        type: 'json',
                                        root: 'data',
                                        successProperty: 'success',
                                        messageProperty: 'message',
                                        totalProperty: 'totalCount'
                                    }
                                },
                                listeners: {
                                    beforeload: function (proxy, response, operation) {
                                        var preCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[1]}")[0];
                                        var value = preCombo.getValue();
                                        if(!value){ //Bug fixed:防止前一级还没加载就加载下一级数据
                                            return false;
                                        }

                                        Ext.apply(this.proxy.extraParams, {search:'[{"key":"parentId","value":"'+value+'"}]'});
                                    },
                                    load: function(_self,records,success){


                                        var selfCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[2]}")[0];
                                        var nextCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[3]}")[0];


                                            ${
                                                if(property2 && property2.get("value")){
                                                    """
                                                        if(success){
                                                            var isExist = false;
                                                            for(var k=0;k<records.length;k++){
                                                                if(records[k].data.areaId==${property2.get("value")}){
                                                                    isExist=true;
                                                                    break;
                                                                }
                                                            }
                                                            if(isExist){
                                                                ${property2.get("value")?"selfCombo.setValue("+property2.get("value")+")":""}
                                                            }
                                                        }
                                                    """
                                                }
                                            }

                                        if(!records || records.length==0){
                                            nextCombo.hide();
                                            selfCombo.hide();
                                            nextCombo.clearValue();
                                            nextCombo.getStore().removeAll();
                                            selfCombo.clearValue();
                                            selfCombo.allowBlank = true;
                                            nextCombo.allowBlank = true;
                                        }else{
                                            selfCombo.show();
                                            selfCombo.allowBlank = false;
                                            ${mode=='update'?'var isExist = false;for(var k=0;k<records.length;k++){if(records[k].data.areaId==selfCombo.getValue()){isExist=true;break;}}if(!isExist){selfCombo.clearValue();}':''}
                                        }
                                    }
                                }
                            }),
                            listeners: {
                                change: function(me, newVal, oldVal, opts){
                                    var nextCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[3]}")[0];
                                    ${mode=='create'?'nextCombo.clearValue();':''}
                                    nextCombo.getStore().reload();
                                }
                            }
                        },
                        {
                            xtype: 'combo',
                            name: '${areaNames[3]}',
                            itemId: '${mode}_combo_${areaNames[3]}',
                            displayField: 'areaName',
                            valueField: 'areaId',
                            ${property3?.get("hidden")?"hidden:true,":""}
//                            ${property3?.get("value")?"value:"+property3.get("value")+",":""}
                            editable: false,
                            allowBlank: ${allowBlank},
                            ${mode=='detail'?'readOnly:true,':''}
                            store: Ext.create('Ext.data.Store', {
                                autoLoad: false,
                                fields: ['id','areaId','areaName', 'parentId', 'level'],
                                pageSize:1000,
                                proxy: {
                                    type: 'ajax',
                                    url: '${grailsApplication.mainContext.getBean(ApplicationTagLib.class).createLink(controller: constraint.domain, action: constraint.action)}',
                                    reader: {
                                        type: 'json',
                                        root: 'data',
                                        successProperty: 'success',
                                        messageProperty: 'message',
                                        totalProperty: 'totalCount'
                                    }
                                },
                                listeners: {
                                    beforeload: function (proxy, response, operation) {
                                        var preCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[2]}")[0];
                                        var value = preCombo.getValue();

                                        if(!value){ //Bug fixed:防止前一级还没加载就加载下一级数据
                                            return false;
                                        }

                                        Ext.apply(this.proxy.extraParams, {search:'[{"key":"parentId","value":"'+(value==""?"-2":value)+'"}]'});
                                    },
                                    load: function(_self,records,success){
                                        var selfCombo = Ext.ComponentQuery.query("#${mode}_combo_${areaNames[3]}")[0];
                                        ${
                                            if(property3 && property3.get("value")){
                                                """
                                                    if(success){
                                                        var isExist = false;
                                                        for(var k=0;k<records.length;k++){
                                                            if(records[k].data.areaId==${property3.get("value")}){
                                                                isExist=true;
                                                                break;
                                                            }
                                                        }
                                                        if(isExist){
                                                            ${property3.get("value")?"selfCombo.setValue("+property3.get("value")+")":""}
                                                        }
                                                    }
                                                """
                                            }
                                        }

                                        if(!records || records.length==0){
                                            selfCombo.clearValue();
                                            selfCombo.allowBlank = true;
                                            selfCombo.hide();
                                        }else{
                                            selfCombo.show();
                                            selfCombo.allowBlank = false;
                                            ${mode=='update'?'var isExist = false;for(var k=0;k<records.length;k++){if(records[k].data.areaId==selfCombo.getValue()){isExist=true;break;}}if(!isExist){selfCombo.clearValue();}':''}
                                        }
                                    }
                                }
                            })
                        }
                    ],
                    listeners:{
                        ${scaffoldingUtilService.getRegisterFieldsScript(field, mode, domainName)}
                    }
                }
            """
        }else{
            output += ""
        }
        return output
    }
}
