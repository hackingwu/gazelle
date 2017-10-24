/**
 * 这是一个扩展类，实现了编辑框的单选/多选操作
 * 调用方式:
    {
        xtype:'selectfield', //扩展类
        fieldLabel: '就读学校', //必选
        name: 'fileurl',  //必选
        url:'/creative/student/upload' //文件上传路径，type=download时，为可选
    }
 */

    Ext.define('Ext.selectField.Select', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.selectfield',
    keyValue: '',

    layout: 'hbox',
    combineErrors: true,

    initComponent: function () {
        var self = this;
        Ext.apply(this, this.option, {
            items: [
                this.createCombo(),
                //{xtype: 'textfield', name: self.name, itemId:self.name+self.mode+'SelText', readOnly:true, margin:'0 5 0 0',allowBlank:((typeof(self.allowBlank)=="undefined")?true:false)},
                this.createSelBtn()
            ]
        });

        this.callParent(arguments);
    },

    /**
     * 创建下拉列表
     */
    createCombo: function(){
        var self = this;
        return Ext.widget({
            xtype: 'combo',
            name: self.name,
            itemId: self.name+self.mode+'SelCombo',
            displayField: self.displayField,
            valueField: self.valueField,
            editable: false,
            allowBlank: (typeof(self.allowBlank) == 'undefined'?true:self.allowBlank),
            readOnly:true,
            store: Ext.create('Ext.data.Store', {
                autoSync:true,
//                autoLoad:false,
//                autoLoad: (self.mode=='create'?false:true),
                fields: [self.displayField,self.valueField],
                pageSize:100000,
                proxy: {
                    type: 'ajax',
                    url: self.url,
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
                        Ext.apply(this.proxy.extraParams, {search:'[{"key":"'+self.valueField+'","value":"'+self.keyValue+'"}]'});
                    },
                    load: function(_self,records,success){
                        if(success){
                            try{
                                Ext.ComponentQuery.query('#'+self.name+self.mode+'SelCombo')[0].setValue(this.getAt(0).data[self.valueField]);
                            }catch (e){
                                //出错：关联对象不存在
                            }
                        }
                    }
                }
            }),
            listeners: {
                change: function(me, newVal, oldVal, opts){
                    if(newVal){
                        if(typeof(newVal)!='object') self.keyValue = newVal;
                        else self.keyValue = newVal.id;

                        this.getStore().reload();

                        if(typeof(onSelectChange) == 'function'){
                            onSelectChange(me, newVal, oldVal, opts);
                        }
                    }
                }
            }

            });
    },

    /**
     * 创建选择按钮
     */
     createSelBtn: function(){
         var self = this;
         return (self.mode=='detail'?null:Ext.widget({
            xtype: 'button',
            itemId: self.name+'SelBtn',
            text: '选择...',
             margin:'0 0 0 5',
             handler: function () {
                Ext.create('Ext.Window', {
                    title: '请选择',
                    width: 550,
                    modal: true,
                    resizable: false,
                    buttonAlign: 'center',
                    items: [
                        {
                            xtype: 'grid',
                            id:self.name+'SelGrid',
                            height:((_height=document.documentElement.clientHeight*0.7)<500?_height:500),
                            autoWidth: true,
                            border: false,
                            //selModel: Ext.create('Ext.selection.CheckboxModel',{mode:'SINGLE'}),
                            columns: self.columns,
                            store: Ext.create('Ext.data.Store', {
                                storeId: self.name+'SelStore',
                                autoLoad: true,
                                remoteSort: true,
                                pageSize:15,
                                fields: self.fields,
                                proxy:{
                                    type: 'ajax',
                                    url: self.url,
                                    reader: {
                                        type: 'json',
                                        root: 'data',
                                        successProperty: 'success',
                                        messageProperty: 'message',
                                        totalProperty: 'totalCount' //数据总条数
                                    },
                                    listeners: {
                                        exception: function (proxy, response, operation) {
                                            Ext.wintip.error('错误!',operation.getError());
                                        }
                                    }
                                },
                                listeners: {
                                    beforeload: function (proxy, response, operation) {
                                        var searchValue = Ext.getCmp(self.name+'SelSearch').getValue();
                                        var query_params={
                                            search:  '[{"key":"'+self.fields[0]+'","value":"'+searchValue+'"}]'
                                        };
                                        Ext.apply(this.proxy.extraParams, query_params);

                                        if(typeof(beforeLoad1) == 'function'){
                                            beforeLoad1(proxy, response, operation);
                                        }
                                    }
                                }
                            }),
                            dockedItems: [{
                                xtype: 'toolbar',
                                dock: 'top',
                                items:[
                                    {
                                        id:self.name+'SelSearch',
                                        //itemId:'multiUnselSearchVal',
                                        width:450,
                                        xtype: 'textfield',
                                        emptyText:'请输入'+self.columns[0].header+'进行搜索',
                                        listeners: {
                                             change: function(){
                                                Ext.getCmp(self.name+'SelGrid').getStore().loadPage(1);
                                             }
                                        }
                                    },{
                                        xtype: 'button',
                                        //hideLabel: true,
                                        margin:'0 0 0 5',
                                        text: '查询',
                                        iconCls: 'icon-search',
                                        handler: function(){
                                            Ext.getCmp(self.name+'SelGrid').getStore().loadPage(1);
                                        }
                                    }
                                ]
                            }],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                prevText:'',
                                nextText:'',
                                firstText:'',
                                lastText:'',
                                refreshText:'',
                                store: self.name+'SelStore',
                                displayInfo: true,
                                displayMsg: '显示 {0} - {1} 条，共 {2} 条',
                                emptyMsg: "没有数据"
                            }
                        }
                    ],
                    buttons: [
                      {
                        text: '确定',
                        handler: function(){
                            var selData = Ext.getCmp(self.name+'SelGrid').getSelectionModel().getSelection();
                            if(selData.length==0){
                                Ext.MessageBox.show({
                                    title: '提示',
                                    msg: '请选择数据再点确定！',
                                    buttons: Ext.MessageBox.OK
                                });
                                return;
                            }

                            self.keyValue = selData[0]['data'][self.valueField];
                            Ext.ComponentQuery.query('#'+self.name+self.mode+'SelCombo')[0].getStore().reload();

                            this.up('window').close();
                        }
                      },{
                        text: '取消',handler: function(){ this.up('window').close();}
                      }
                    ]
                }).show();
            }
         }));
     }
});
