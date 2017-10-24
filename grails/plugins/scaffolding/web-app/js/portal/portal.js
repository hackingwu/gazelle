/**
 * @class Ext.app.Portal
 * @extends Object
 * A sample portal layout application class.
 */

Ext.define('Ext.app.Portal', {

    extend: 'Ext.container.Viewport',
    requires: ['Ext.app.PortalPanel', 'Ext.app.PortalColumn', 'Ext.app.GridPortlet', 'Ext.app.ChartPortlet'],

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

    initComponent: function(){
        var content = '<div class="portlet-content">content内容</div>';


        Ext.apply(this, {
            id: 'app-viewport',
            layout: {
                type: 'border',
                padding: '0 5 5 5' // pad the layout from the window edges
            },
            items: [{
                id: 'app-header',
                xtype: 'box',
                region: 'north',
                height: 40,
                html: '我的工作台'
            },{
                xtype: 'container',
                region: 'center',
                layout: 'border',
                items: [{
                    id: 'app-options',
                    title: 'Options',
                    region: 'west',
                    animCollapse: true,
                    width: 200,
                    minWidth: 150,
                    maxWidth: 400,
                    split: true,
                    collapsible: true,
                    layout:{
                        type: 'accordion',
                        animate: true
                    },
                    items: [{
                        html: content,
                        title:'Navigation',
                        autoScroll: true,
                        border: false,
                        iconCls: 'nav'
                    },{
                        title:'Settings',
                        html: content,
                        border: false,
                        autoScroll: true,
                        iconCls: 'settings'
                    }]
                },{
                    id: 'app-portal',
                    xtype: 'portalpanel',
                    region: 'center',
                    items: [{
                        id: 'col-1',
                        items: [{
                            id: 'portlet-1',
                            title: 'Grid Portlet',
                            tools: this.getTools(),
                            items: Ext.create('Ext.app.GridPortlet'),
                            listeners: {
                                'close': Ext.bind(this.onPortletClose, this)
                            }
                        },{
                            id: 'portlet-2',
                            title: 'Portlet 2',
                            tools: this.getTools(),
                            html: content,
                            listeners: {
                                'close': Ext.bind(this.onPortletClose, this)
                            }
                        }]
                    },{
                        id: 'col-2',
                        items: [{
                            id: 'portlet-3',
                            title: 'Portlet 3',
                            tools: this.getTools(),
                            html: '<div class="portlet-content">Ext.example.bogusMarkup</div>',
                            listeners: {
                                'close': Ext.bind(this.onPortletClose, this)
                            }
                        }]
                    },{
                        id: 'col-3',
                        items: [{
                            id: 'portlet-4',
                            title: 'Stock Portlet',
                            tools: this.getTools(),
                            items: Ext.create('Ext.app.ChartPortlet'),
                            listeners: {
                                'close': Ext.bind(this.onPortletClose, this)
                            }
                        }]
                    },{
                        id:'col-4',
                        items:[
                            {
                                id:'portlet-5',
                                title:'门店信息',
                                tools:function(){
                                    Ext.msg('强大suggest')
                                },
                                html:'htmlgg',
                                listeners: {
                                    'close': Ext.bind(this.onPortletClose, this)
                                }
                            },{
                                id:'portlet-6',
                                title:'动态信息',
                                height:300,
                                tools:function(){
                                    Ext.msg('强大suggest')
                                },
                                html:'动态信息',
                                listeners: {
                                    'close': Ext.bind(this.onPortletClose, this)
                                }
                            },
                            {
                                id:'portlet-7',
                                title:'未来一周气温变化',
                                height:300,
                                tools: this.getTools(),
                                html:'<div id="echart" style="width: 100%;height: 100%"></div>',
                                listeners: {
                                    'close': Ext.bind(this.onPortletClose, this)
                                }
                            }
                        ]
                    }]
                }]
            }]
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
