/**
 * 这是一个扩展类，实现了编辑框多选操作
 * 用途：一对多的多项选择
 * 调用方式:
 {
     xtype:'multiselectfield', //扩展类
     fieldLabel: '多个外部对象', //必选
     mode: 'create',  //必选
     url:'/cube_demo/student/list' //Ajax去后台获取数据的URL
     displayName:'objects' //前台展示的值 name1,name2,name3
     valueName:'objectIds' //后台存储的值 id1,id2,id3
     displayField:'name'  //关联对象展示的值 name
     valueField:'value'  //关联对象后台存储的值 id
 }
 */

Ext.define('Ext.selectField.MultiSelect', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.multiselectfield',
    keyValue: '',
    layout: 'hbox',
    combineErrors: true,

    initComponent: function () {
        var self = this;
        Ext.apply(this, this.option, {
            items: [
                this.createDisplayTextField(),
                this.createValueTextField(),
                this.createSelBtn(self.selMode)
            ]
        });

        this.callParent(arguments);
    },

    createDisplayTextField:function(){
        var self = this;
        return Ext.widget({
            xtype:'textfield',
            id:self.mode+'multiSelectDisplayTextField',
            readOnly:true,
//            emptyText:'请选择',
            name:self.displayName
        });
    },

    createValueTextField: function () {
        var self = this;
        return Ext.widget({
            xtype:'hidden',
            id:self.mode+'multiSelectValueTextField',
            name:self.valueName
        });
    },

    createSelBtn:function(flag){
        var self = this;
        if(flag=="checkBoxMultiSelect"){
            return self.mode=='detail'?null:Ext.widget(
                {
                    xtype:'button',
                    itemId:self.mode+'MultiSelBtn',
                    text:'选择...',
                    margin:'0 0 0 5',
                    handler:function(){
                        Ext.create('Ext.Window', {
                            title: '请选择',
                            width: 550,
                            modal: true,
                            resizable: false,
                            buttonAlign: 'center',
                            items: [
                                {
                                    xtype: 'grid',
                                    id: self.mode + 'Grid',
                                    height:((_height=document.documentElement.clientHeight*0.7)<500?_height:500),
                                    autoWidth: true,
                                    border: false,
                                    columns: self.columns,
                                    selModel:Ext.create('Ext.selection.CheckboxModel'),
                                    store: Ext.create('Ext.data.Store', {
                                        storeId: self.mode + 'Store',
                                        autoLoad: true,
                                        remoteSort: true,
                                        pageSize: 100,//self.maxSel,
                                        fields: [self.displayField, self.valueField],
                                        proxy: {
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
                                                    Ext.wintip.error('错误!', operation.getError());
                                                }
                                            }
                                        },
                                        listeners: {
                                            beforeload: function (proxy, response, operation) {

                                            },
                                            load:function(_self,records,success){
                                                if(success){
                                                    var gridSelectionModel = Ext.getCmp(self.mode+'Grid').getSelectionModel();
                                                    var sel = Ext.getCmp(self.mode+'multiSelectValueTextField').value
                                                    var selList = sel.split(",")
                                                    Ext.each(records,function(record){
                                                        var i = selList.length
                                                        while(i--){
                                                            if(record.data.id==selList[i]){
                                                                gridSelectionModel.select(record.index,true)
                                                                break
                                                            }
                                                        }

                                                    })
                                                }
                                            }
                                        }
                                    })
                                }
                            ],
                            buttons: [
                                {
                                    text:'确定',
                                    handler:function(){
                                        var selData = Ext.getCmp(self.mode+'Grid').getSelectionModel().getSelection()
                                        if(selData.length==0){
                                            Ext.MessageBox.show({
                                                title: '提示',
                                                msg: '请至少选择1个项目！',
                                                buttons: Ext.MessageBox.OK
                                            });
                                            return;
                                        }
                                        var values = [];
                                        var ids = []
                                        Ext.Array.each(selData,function(record){
                                            values.push(record.data[self.displayField]);
                                            ids.push(record.data[self.valueField]);
                                        })
                                        Ext.getCmp(self.mode+'multiSelectDisplayTextField').setValue(values)
                                        Ext.getCmp(self.mode+'multiSelectValueTextField').setValue(ids)

                                        this.up('window').close();
                                    }

                                },
                                {
                                    text: '关闭',handler: function(){ this.up('window').close();}
                                }
                            ]
                        }).show();
                    }
                }
            )
        }else{
            return self.mode=='detail'?null:Ext.widget(
                {
                    xtype: 'button',
                    itemId: self.mode+'MultiSelBtn',
                    text: '选择...',
//                iconCls: 'icon-add',
                    margin:'0 0 0 5',
                    handler: function () {
                        Ext.create('Ext.Window', {
                            title: '请选择',
                            width: 750,
                            modal: true,
                            resizable: false,
                            buttonAlign: 'center',
                            layout: {
                                type: 'table',
                                columns: 3
                            },
                            items: [
                                {
                                    colspan:3,
                                    xtype: 'toolbar',
                                    border:false,
                                    items: [{
                                        xtype: 'textfield',
                                        id:self.mode+'UnSelSearch',
                                        width:640,
                                        emptyText:'请输入姓名进行搜索',
                                        listeners: {
                                            change: function(){
                                                Ext.getCmp(self.mode+'UnSelGrid').getStore().loadPage(1);
                                            }
                                        }
                                    },{
                                        xtype: 'button',
                                        margin:'0 0 0 5',
                                        text: '查询',
                                        iconCls: 'icon-search',
                                        handler: function(){
                                            Ext.getCmp(self.mode+'UnSelGrid').getStore().loadPage(1);
                                        }
                                    }]
                                },
                                {
                                    xtype:'panel',
                                    width:395,
                                    items: [
                                        { //左边表格
                                            xtype: 'grid',
                                            id:self.mode+'UnSelGrid',
                                            title:'可选项目',
                                            flex:4,
                                            height:((_height=document.documentElement.clientHeight*0.7)<450?_height:450),
                                            autoWidth: true,
                                            border: false,
                                            selModel: Ext.create('Ext.selection.CheckboxModel'),
                                            columns: self.columns,
                                            store: Ext.create('Ext.data.Store', {
                                                storeId: self.mode+'UnSelStore',
                                                autoLoad: true,
                                                remoteSort: true,
                                                pageSize:self.maxSel,
                                                fields: [self.displayField,self.valueField],
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
                                                        exception: function (proxy, response, operation) { //{"success":false,"message":"Failed to Destroy user","data":[]} @success为false时执行此回调函数
                                                            Ext.wintip.error('错误!',operation.getError());
                                                        }
                                                    }
                                                },
                                                listeners: {
                                                    beforeload: function (proxy, response, operation) { //向服务端传递额外参数
                                                        var searchValue = Ext.getCmp(self.mode+'UnSelSearch').getValue();
                                                        var query_params={
                                                            search:  '[{"key":"'+self.displayField+'","value":"'+searchValue+'"}]'
                                                        };
                                                        Ext.apply(this.proxy.extraParams, query_params);
                                                    },
                                                    load: function(_self,records,success){
                                                        if(success){



                                                            var unSelGridSelModel = Ext.getCmp(self.mode+'UnSelGrid').getSelectionModel();
                                                            var selGridStore = Ext.getCmp(self.mode+'SelGrid').getStore();
                                                            unSelGridSelModel.deselectAll();
                                                            Ext.each(records, function(unSelRecord) {
                                                                var row = unSelRecord.index-(_self.pageSize*(_self.currentPage-1));
                                                                selGridStore.each(function(selRecord){
                                                                    if(unSelRecord.data.id == selRecord.data.id){
                                                                        unSelGridSelModel.select(row, true);
                                                                        return false; //跳出第二个each循环
                                                                    }
                                                                });
                                                            });
                                                            Ext.getCmp(self.mode+'UnSelSearch').focus();
                                                        }
                                                    }
                                                }
                                            }),
                                            bbar: {
                                                xtype: 'pagingtoolbar',
                                                prevText:'',
                                                nextText:'',
                                                firstText:'',
                                                lastText:'',
                                                refreshText:'',
                                                store: self.mode+'UnSelStore',
                                                emptyMsg: "没有数据"
                                            }
                                        }
                                    ]
                                },{
                                    border:false,
                                    width:35,
                                    layout: {
                                        type: 'vbox',
                                        align: 'center'
                                    },
                                    items:[
                                        {
                                            xtype: 'button',
                                            iconCls: 'icon-right',
                                            margin:'0 5 10 5',
                                            handler: function(){
                                                var selData=[];
                                                var unSelStore = Ext.getCmp(self.mode+'UnSelGrid').getStore();
                                                if(unSelStore.data.length==0){
                                                    return;
                                                }
                                                unSelStore.each(function(record){
                                                    selData.push(record);
                                                    if(selData.length==self.maxSel){
                                                        return false;
                                                    }
                                                });
                                                Ext.getCmp(self.mode+'SelGrid').getStore().loadData(selData,true);
                                                unSelStore.reload();
                                            }
                                        },
                                        {
                                            xtype: 'button',
                                            iconCls: 'icon-right2',
                                            margin:'0 5 10 5',
                                            handler: function(){
                                                var selStore = Ext.getCmp(self.mode+'SelGrid').getStore();
                                                if(selStore.getCount() == self.maxSel){
                                                    Ext.MessageBox.show({
                                                        title: '提示',
                                                        msg: '最多只能选择13个项目！',
                                                        buttons: Ext.MessageBox.OK
                                                    });
                                                    return;
                                                }

                                                var selData = Ext.getCmp(self.mode+'UnSelGrid').getSelectionModel().getSelection();
                                                if(selData.length==0){
                                                    return;
                                                }
                                                selStore.loadData(selData,true);
                                                Ext.getCmp(self.mode+'UnSelGrid').getStore().reload();
                                            }
                                        },{
                                            xtype: 'button',
                                            iconCls: 'icon-left2',
                                            margin:'0 5 10 5',
                                            handler: function(){
                                                var multiSelGrid = Ext.getCmp(self.mode+'SelGrid');
                                                var selData = multiSelGrid.getSelectionModel().getSelection();
                                                if(selData.length==0){
                                                    return;
                                                }
                                                multiSelGrid.getStore().remove(selData);
                                                Ext.getCmp(self.mode+'UnSelGrid').getStore().reload();
                                            }
                                        },{
                                            xtype: 'button',
                                            iconCls: 'icon-left',
                                            margin:'0 5 0 5',
                                            handler: function(){
                                                var selStore = Ext.getCmp(self.mode+'SelGrid').getStore();
                                                if(selStore.data.length==0){
                                                    return;
                                                }

                                                selStore.removeAll();
                                                Ext.getCmp(self.mode+'UnSelGrid').getStore().reload();
                                            }
                                        }
                                    ]
                                },{
                                    xtype:'panel',
                                    width:310,
                                    items:[
                                        { //右边表格
                                            xtype: 'grid',
                                            id: self.mode+'SelGrid',
                                            title:'已选项目',
                                            height:((_height=document.documentElement.clientHeight*0.7)<450?_height:450),
                                            autoWidth: true,
                                            selModel: Ext.create('Ext.selection.CheckboxModel'),
                                            columns:self.columns,
                                            store: Ext.create('Ext.data.Store', {
                                                fields: [self.displayField,self.valueField]
                                            })
                                        }
                                    ]
                                }
                            ],
                            buttons: [
                                {
                                    text: '确定',
                                    handler: function(){
                                        var selStore = Ext.getCmp(self.mode+'SelGrid').getStore();
                                        var selData = selStore.data
                                        if(selData.length==0){
                                            Ext.MessageBox.show({
                                                title: '提示',
                                                msg: '请至少选择1个项目！',
                                                buttons: Ext.MessageBox.OK
                                            });
                                            return;

                                        }
                                        var values = [];
                                        var ids = []

                                        selData.each(function(record){
                                            values.push(record.data[self.displayField]);
                                            ids.push(record.data[self.valueField]);
                                        });
                                        Ext.getCmp(self.mode+'multiSelectDisplayTextField').setValue(values)
                                        Ext.getCmp(self.mode+'multiSelectValueTextField').setValue(ids)

                                        this.up('window').close();

                                    }
                                },{
                                    text: '取消',handler: function(){ this.up('window').close();}
                                }
                            ]
                        }).show();


                    }
                }
            );
        }



    }


});
