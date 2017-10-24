
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html" charset="utf-8">
<title>Redis管理</title>
<m:extResource/>
<script type="text/javascript">
    var redisCreateWnd;
    var redisDeleteWnd;
    var redisIsMemberWnd;
    var redisCountMemberWnd;

    var required = '<span style="color:red;">*</span>';


    function redisCreate() {
        if (!redisCreateWnd) {

            redisCreateForm = Ext.widget({
                xtype: 'form',
                id: 'redisCreateForm',
                layout: 'form',
                autoScroll:true,
                width:750,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                items: [
                    {fieldLabel: 'key(键)',  name: 'key', allowBlank: false, afterLabelTextTpl: required},
                    {fieldLabel: 'member(值)',  name: 'member', allowBlank: false, afterLabelTextTpl: required}
                ],
                buttons: [
                    {
                        text: '确定',
                        handler: function () {
                            var cmpForm = this.up('form').getForm();

                            if (cmpForm.isValid()) {
                                cmpForm.submit({
                                    url: '${g.createLink([controller: 'redis', action:"addMember"])}', //params: {},
                                    success: function(form, response) {
                                        if(response.result.success){
                                            Ext.wintip.msg('操作成功!', response.result.message);
                                            redisCreateWnd.hide();
//                                            redisGrid.getStore().reload();
                                        }else{
                                            Ext.wintip.error('操作失败!',  response.result.message);
                                        }
                                    },
                                    failure: function(form, action) {
                                        Ext.wintip.error('操作失败!',  action.result.message);
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: '关闭',
                        handler: function () {
                            redisCreateWnd.hide();
                        }
                    }
                ]
            });

            redisCreateWnd = Ext.create('Ext.Window', {
                title: '新增Redis记录',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [redisCreateForm]
            });
        }
        redisCreateWnd.down('form').form.reset();

        redisCreateWnd.show();

        var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
        if(document.getElementById('redisCreateForm').scrollHeight>autoHeight){
            redisCreateWnd.down('form').setHeight(autoHeight);
            redisCreateWnd.down('form').setWidth(750);
            redisCreateWnd.center();
        }
    }

    //删除管理
    function redisDelete() {
        if (!redisDeleteWnd) {

            redisDeleteForm = Ext.widget({
                xtype: 'form',
                id: 'redisDeleteForm',
                layout: 'form',
                autoScroll:true,
                width:750,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                items: [
                    {fieldLabel: 'key(键)',  name: 'key', allowBlank: false, afterLabelTextTpl: required},
                    {fieldLabel: 'member(值)',  name: 'member', allowBlank: false, afterLabelTextTpl: required}
                ],
                buttons: [
                    {
                        text: '确定',
                        handler: function () {
                            var cmpForm = this.up('form').getForm();

                            if (cmpForm.isValid()) {
                                cmpForm.submit({
                                    url: '${g.createLink([controller: 'redis', action:"deleteMember"])}', //params: {},
                                    success: function(form, response) {
                                        if(response.result.success){
                                            Ext.wintip.msg('操作成功!', response.result.message);
                                            redisDeleteWnd.hide();
//                                            redisGrid.getStore().reload();
                                        }else{
                                            Ext.wintip.error('操作失败!',  response.result.message);
                                        }
                                    },
                                    failure: function(form, action) {
                                        Ext.wintip.error('操作失败!',  action.result.message);
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: '关闭',
                        handler: function () {
                            redisDeleteWnd.hide();
                        }
                    }
                ]
            });

            redisDeleteWnd = Ext.create('Ext.Window', {
                title: '删除Redis记录',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [redisDeleteForm]
            });
        }
        redisDeleteWnd.down('form').form.reset();

        redisDeleteWnd.show();

        var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
        if(document.getElementById('redisDeleteForm').scrollHeight>autoHeight){
            redisDeleteWnd.down('form').setHeight(autoHeight);
            redisDeleteWnd.down('form').setWidth(750);
            redisDeleteWnd.center();
        }
    }

    //查询记录是否存在
    function redisIsMember() {
        if (!redisIsMemberWnd) {

            redisIsMemberForm = Ext.widget({
                xtype: 'form',
                id: 'redisIsMemberForm',
                layout: 'form',
                autoScroll:true,
                width:750,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                items: [
                    {fieldLabel: 'key(键)',  name: 'key', allowBlank: false, afterLabelTextTpl: required},
                    {fieldLabel: 'member(值)',  name: 'member', allowBlank: false, afterLabelTextTpl: required}
                ],
                buttons: [
                    {
                        text: '确定',
                        handler: function () {
                            var cmpForm = this.up('form').getForm();

                            if (cmpForm.isValid()) {
                                cmpForm.submit({
                                    url: '${g.createLink([controller: 'redis', action:"isMember"])}', //params: {},
                                    success: function(form, response) {
                                        if(response.result.success){
                                            Ext.wintip.msg('记录存在!', response.result.message);
//                                            redisIsMemberWnd.hide();
//                                            redisGrid.getStore().reload();
                                        }else{
                                            Ext.wintip.error('记录不存在!',  response.result.message);
                                        }
                                    },
                                    failure: function(form, action) {
                                        Ext.wintip.error('记录不存在!',  action.result.message);
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: '关闭',
                        handler: function () {
                            redisIsMemberWnd.hide();
                        }
                    }
                ]
            });

            redisIsMemberWnd = Ext.create('Ext.Window', {
                title: '查询Redis记录',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [redisIsMemberForm]
            });
        }
        redisIsMemberWnd.down('form').form.reset();

        redisIsMemberWnd.show();

        var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
        if(document.getElementById('redisIsMemberForm').scrollHeight>autoHeight){
            redisIsMemberWnd.down('form').setHeight(autoHeight);
            redisIsMemberWnd.down('form').setWidth(750);
            redisIsMemberWnd.center();
        }
    }

    //查询记录数量
    function redisCountMember() {
        if (!redisCountMemberWnd) {

            redisCountMemberForm = Ext.widget({
                xtype: 'form',
                id: 'redisCountMemberForm',
                layout: 'form',
                autoScroll:true,
                width:750,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                items: [
                    {fieldLabel: 'key(键)',  name: 'key', allowBlank: false, afterLabelTextTpl: required}
                ],
                buttons: [
                    {
                        text: '确定',
                        handler: function () {
                            var cmpForm = this.up('form').getForm();

                            if (cmpForm.isValid()) {
                                cmpForm.submit({
                                    url: '${g.createLink([controller: 'redis', action:"countMember"])}', //params: {},
                                    success: function(form, response) {
                                        if(response.result.success){
                                            Ext.wintip.msg('记录存在!', response.result.message);
//                                            redisCountMemberWnd.hide();
//                                            redisGrid.getStore().reload();
                                        }else{
                                            Ext.wintip.error('记录不存在!',  response.result.message);
                                        }
                                    },
                                    failure: function(form, action) {
                                        Ext.wintip.error('记录不存在!',  action.result.message);
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: '关闭',
                        handler: function () {
                            redisCountMemberWnd.hide();
                        }
                    }
                ]
            });

            redisCountMemberWnd = Ext.create('Ext.Window', {
                title: '查询Redis记录',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [redisCountMemberForm]
            });
        }
        redisCountMemberWnd.down('form').form.reset();

        redisCountMemberWnd.show();

        var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
        if(document.getElementById('redisCountMemberForm').scrollHeight>autoHeight){
            redisCountMemberWnd.down('form').setHeight(autoHeight);
            redisCountMemberWnd.down('form').setWidth(750);
            redisCountMemberWnd.center();
        }
    }

    Ext.onReady(function () {

        Ext.QuickTips.init();
        Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息

        //创建toolbar
        var toolbar = Ext.create('Ext.Toolbar', {
            minWidth: 800,
            items: [

                {
                    text: '新增',
                    disabled: false,
                    iconCls: 'icon-add',
                    handler: function () {
                        redisCreate();
                    }
                },
                {
                    text: '删除',
                    disabled: false,
                    iconCls: 'icon-delete',
                    handler: function () {
                        redisDelete();
                    }
                },
                {
                    text: '查询记录',
                    disabled: false,
                    iconCls: 'icon-grid',
                    handler: function () {
                        redisIsMember();
                    }
                },
                {
                    text: '查询数量',
                    disabled: false,
                    iconCls: 'icon-grid',
                    handler: function () {
                        redisCountMember();
                    }
                }

            ]
        });


        redisStore = Ext.create('Ext.data.Store', {
            autoSync: true,
            remoteSort: true,
            fields: ['key','member']
        });


        //计算grid高度
        if(document.documentElement && document.documentElement.clientHeight){
            redisGridHeight = document.documentElement.clientHeight-50;
            var pageSize = parseInt((redisGridHeight-5-30-50)/29);
            redisStore.pageSize=pageSize;
        }

        redisGrid = Ext.create('Ext.grid.Panel', {
            height: redisGridHeight,
            autoWidth: true,
            border:false,
            store: redisStore,
            stripeRows:true,
            loadMask: true,
            selModel: Ext.create('Ext.selection.CheckboxModel'),
            columns: [
                {header: 'key(键)',flex: 1, dataIndex: 'key'},
                {header: 'member(值)',flex: 1, dataIndex: 'member'}

            ]
        });


        //使用border布局
        Ext.create('Ext.Viewport', {
            layout: 'border',
            items: [{
                region: 'north',
                border:false,
                items: toolbar,
                height:50
            },{
                region: 'center',
                border:false,
                items: redisGrid
            }]
        });


//            redisStore.loadPage(1);
    });
</script>
</head>
<body style="overflow: auto;"></body>
</html>
