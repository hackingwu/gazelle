Ext.define('Esm.app.Desktop', {
    extend: 'Ext.container.Viewport',
    requires: ['Ext.app.PortalPanel', 'Ext.app.PortalColumn', 'Esm.portlet.GridPortlet', 'Ext.app.ChartPortlet',
        'Esm.portlet.Line','Esm.portlet.Bar','Esm.portlet.HtmlPortlet','Esm.portlet.ChinaMap','Esm.portlet.ChinaMapSwitch','Esm.portlet.CountUp',
        'Esm.portlet.Pie','Esm.portlet.DynLine','Esm.portlet.Net','Esm.portlet.Gauge','Esm.portlet.MultiGauge','Esm.portlet.EChartPanel'],

//    'Ext.app.Portlet',
//    'Ext.app.PortalColumn',
//    'Ext.app.PortalPanel',
//    'Ext.app.Portlet',
//    'Ext.app.PortalDropZon
    getTools: function(){
        return [{
            xtype: 'tool',
            type: 'gear',
            handler: function(e, target, header, tool){
                var portlet = header.ownerCt;
                portlet.setLoading('Loading...');
                Ext.defer(function() {
                    portlet.setLoading(false);
                }, 2000);
            }
        }];
    },

    config :{
        rows : 3,
        columns : 3,
        items : []
    },
    initComponent: function(){
        var me = this;

        var height = 300;

        if(document.documentElement && document.documentElement.clientHeight){
            height = parseInt((document.documentElement.clientHeight-20*me.rows)/ me.rows);
        }
        var portalPanel = {
            id: 'app-portal',
            xtype: 'portalpanel',
            items :[]

        }

        var portlet = {
            title:'portlet',
            closable : false,
            height:height
           // items:[{html:''}],
//            listeners: {
//                'close': Ext.bind(me.onPortletClose, me)
//            }
        };

        var portalColumns = [];

        for(var i= 0;i< me.columns;i++){
            portalColumns.push({id:'col-' + i,items:[]});
        }

        var items = this.items;

        for(var i = 0;i < items.length;){
            for(var j = 0;j < me.columns && i < items.length;j++,i++){
                var item =items[i];
                if(item.xtype){
                    item.items = { xtype:item.xtype,dataUrl:me.dataUrl,parent:me,option:item.options,params:item.options.params};
                    //item.items =Ext.createByAlias(item.xtype);
                    delete item.xtype;
                }
                portalColumns[j].items.push(Ext.apply({id:'portlet-'+ j + '-' + i,border:false },item,portlet));
            }
        }

        portalPanel.items = portalColumns;

        Ext.apply(this, {
            items : [portalPanel]
        });
        this.callParent(arguments);
    },

    onPortletClose: function(portlet) {
        this.showMsg('"' + portlet.title + '" was removed');
    },

    showMsg: function(msg) {
        var el = Ext.get('app-msg'),
            msgId = Ext.id();

        this.msgId = msgId;
        el.update(msg).show();

        Ext.defer(this.clearMsg, 3000, this, [msgId]);
    },

    clearMsg: function(msgId) {
        if (msgId === this.msgId) {
            Ext.get('app-msg').hide();
        }
    }
});
