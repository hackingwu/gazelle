<%@ page import="framework.ModelService; role.SysRole;authority.AuthorityService" %><% Map sysRoleModel = ModelService.GetModel("sysRole");
boolean isDisable= (!AuthorityService.ButtonStatic((Long)session["sysRoleId"],"showResourceTree")) %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>${sysRoleModel.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var sysRoleCreateWnd;
        var sysRoleUpdateWnd;
        var sysRoleDetailWnd;
        %{--var sysRoleStore;--}%
        %{--var sysRoleGrid;--}%

        var resourceTree;
        var resourceTreeNode=1;
        var resourceTreeText="";
        var resourceIds = [];

        var required = '<span style="color:red;">*</span>';

        <m:extCreateWnd model="sysRole" excluded="[]" controller="sysRole" action="createAction" />
        <m:extUpdateWnd model="sysRole" excluded="[]" controller="sysRole" action="updateAction" />
        <m:extDetailWnd model="sysRole" excluded="[]" controller="sysRole" action="detailAction" />

        function showResourceTree(){
            var selection = sysRoleGrid.getView().getSelectionModel().getSelection();
            if(selection.length==0){
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '请选择需要查看权限的角色！',
                    buttons: Ext.MessageBox.OK
                });
                return;
            }

            if(selection.length>1){
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '请选择一条角色记录进行查看！',
                    buttons: Ext.MessageBox.OK
                });
                return;
            }

            resourceTree.expandAll();

            resourceTreeStore.load({params:{roleId:selection[0].data.id}});

            resourceTreeWnd = Ext.create('Ext.Window', {
                title: '角色权限树',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [resourceTree],
                listeners:{

                }
            });

        resourceTreeWnd.show();
        resourceTreeWnd.center();
    }

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker" />', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.multiUpload':'<g:resource plugin="scaffolding" dir="js/multiUpload" />',
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload" />', //上传控件路径，不使用上传控件时，可删除该代码
                'Ext.selectField':'<g:resource plugin="scaffolding" dir="js/selectField" />'
            }
        });

        Ext.require(['Go.form.field.DateTime']); //带时间选择的日期控件
        Ext.require(['Ext.upload.ImgUpload']); //图片上传
        Ext.require(['Ext.upload.FileUpload']); //单文件上传
        Ext.require(['Ext.multiUpload.Panel']); //多文件上传
        Ext.require(['Ext.selectField.Select']); //单选控件
        Ext.require(['Ext.selectField.MultiSelect']); //多选控件

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extCRUDButtons model="sysRole" excluded="['detail','update']" />
                    {
                        itemId: 'showResourceTree',
                        text: '配置权限',
                        disabled: false,
                        iconCls: 'icon-edit',
                        handler: function () {
                            showResourceTree();
                        }
                    },
                    %{--<m:extMoreButtons items="[--}%
%{--//                            [text:'查看角色权限', handler:'loadResourceTree()', code:'loadResourceTree'],--}%
                            %{--[text:'查看角色对应权限', handler:'showResourceTree()', code:'showResourceTree']--}%
                     %{--]" />--}%

                    %{--<m:extSingleSelectButton model="sysRole" excluded="[]" />
                    <m:extMultiSelectButton model="sysRole" excluded="[]" />
                    <m:extImportButton model="sysRole" />
                    <m:extExportButton model="sysRole" />--}%
                    '->',
                    %{--<m:extGangCombo model="sysRole" combo="[[label:'项目',name:'select_app_code',controller:'sysRole',action:'combo1'],[label:'版本',name:'select_app_version',controller:'sysRole',action:'combo2']]" />--}%
                    <m:extSearchButtons model="sysRole" mode="standard" excluded="[]" />
                ]
            });

            <m:extGridStore model="sysRole" excluded="[]" />
            %{--<m:extGrid model="sysRole" excluded="[]" config="[column:8,rowHeight:25]" />--}%
            //计算grid高度
            if(document.documentElement && document.documentElement.clientHeight){
                sysRoleGridHeight = document.documentElement.clientHeight-50;
                var pageSize = parseInt((sysRoleGridHeight-5-30-50)/25);
                sysRoleStore.pageSize=pageSize;
            }

            sysRoleGrid = Ext.create('Ext.grid.Panel', {
                height: sysRoleGridHeight,
                autoWidth: true,
                border:false,
                store: sysRoleStore,
                stripeRows:true,
                loadMask: true,
                selModel: Ext.create('Ext.selection.CheckboxModel'),
                columns: [
                    {header: '角色名',flex: 1, dataIndex: 'name'}

                ],
                //底部翻页
                bbar: Ext.create('Ext.PagingToolbar', {
                    prevText:'',
                    nextText:'',
                    firstText:'',
                    lastText:'',
                    refreshText:'',
                    store: sysRoleStore,
                    displayInfo: true,
                    displayMsg: '显示 {0} - {1} 条，共 {2} 条',
                    emptyMsg: "没有系统角色数据"
                }),
                listeners:{

//                    itemdblclick: function(dataview, record, item, index, e){
//                        //点击角色刷新资源树
//                        loadResourceTree();
//
//                    }
                }
            });
            resourceTreeStore = Ext.create('Ext.data.TreeStore', {
                fields: ['id', 'text'],
                expanded: true,
                proxy: {
                    type: 'ajax',
                    autoLoad: true,
                    url: '${g.createLink([controller: 'btnAndMenu', action: 'treeList'])}' //params: {},

//                url: '/fj_volunteers_mgr/resource/treeList'
                },
                listeners : {
                    beforeload: function (store, operation) {
                        var selection = sysRoleGrid.getView().getSelectionModel().getSelection();
                        if(selection.length == 1) {
                            var new_params = {//参数
                                roleId : selection[0].data.id
                            };
                            Ext.apply(store.proxy.extraParams, new_params);
                        }
                    }
                }
            });


            var resourceRootData;
            Ext.Ajax.request({
                async : false,
                url: '${g.createLink([controller: 'btnAndMenu', action: 'getRootNode'])}', //params: {},

                success: function(response){
                    resourceRootData = Ext.JSON.decode(response.responseText);
                }
            });

            resourceTreeNode = resourceRootData.id;

            resourceTree = Ext.create('Ext.tree.Panel', {
                id:"resourceTree",
                store: resourceTreeStore,
                width: 400,
                height: sysRoleGridHeight-1,
                useArrows: true,
                rootVisible:false,


                root: {
                    id: resourceRootData.id,
                    text: resourceRootData.text,
                    expanded: true
                },
                bbar: [ {text: "保存",
                    disabled: ${isDisable},
                    handler:function(){
                        btnSave();
                    }
                },
                    {text: "全部授权",
                        disabled: ${isDisable},
                        handler:function(){
                            checkedAll();
                        }
                    },
                    {text: "取消全部",
                        disabled: ${isDisable},
                        handler:function(){
                            canelAll();
                        }
                    }
                ],
                listeners:{
                    itemclick: function (view, record, item, index, e, eOpts) {
                        resourceTreeNode =  record.data.id;
                        resourceTreeText =  record.data.text;
                    },
                    afterrender: function (node) {
                        resourceTree.expandAll();
                    },checkchange : function(node, checked) {
                        if (checked == true) {
                            node.checked = checked;
                            // console.dir(node.parentNode);
                            //alert(node.get("leaf"));
                            //获得父节点
                            pNode = node.parentNode;
                            //当checked == true通过循环将所有父节点选中
                            for (; pNode != null; pNode = pNode.parentNode) {
                                pNode.set("checked", true);
                            }
                        }
                        //当该节点有子节点时，将所有子节点选中删除
                        if (!node.get("leaf") && !checked)
                            node.cascade(function (node) {
                                node.set('checked', false);
                            });
                    }
                }
            });
            resourceTree.on('checkchange', function(node, checked) {//监听勾选事件
                //node.expand();
                if (checked == true) {
                    node.checked = checked;
                    var rootnodes = node.childNodes;//获取主节点
                    updateNodes(rootnodes,"selected");
                }
            }, resourceTree);

            %{--<m:extLayout model="sysRole" />--}%
            Ext.create('Ext.Viewport', {
                layout: 'border',
                items: [{
                    region: 'north',
                    autoScroll: true,
                    border:false,
                    items: toolbar,
                    height:50
                },{
                    region: 'center',
                    border:false,
                    items: sysRoleGrid
                }]
            });

            sysRoleStore.loadPage(1); //extGridStore配置name属性时，需要手动修改此属性
        });

    function loadResourceTree(){
        var selection = sysRoleGrid.getView().getSelectionModel().getSelection();
        if(selection.length==0){
            Ext.MessageBox.show({
                title: '提示',
                msg: '请先选择需要配置权限的角色！',
                buttons: Ext.MessageBox.OK
            });
            return;
        }

        if(selection.length>1){
            Ext.MessageBox.show({
                title: '提示',
                msg: '请选择一条角色记录进行查看！',
                buttons: Ext.MessageBox.OK
            });
            return;
        }

        resourceTree.expandAll();

        resourceTreeStore.load({params:{roleId:selection[0].data.id}});
    }
        function btnSave() {
            var rootnodes = resourceTree.getRootNode().childNodes;//获取主节点
            var selection = sysRoleGrid.getView().getSelectionModel().getSelection();
            if(selection.length==0){
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '请选择需要保存权限的角色！',
                    buttons: Ext.MessageBox.OK
                });
                return;
            }

            if(selection.length>1){
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '请选择一条角色记录进行保存！',
                    buttons: Ext.MessageBox.OK
                });
                return;
            }

            var roleId = selection[0].data.id;
            resourceIds = [];
            iterTree(rootnodes);

            Ext.Ajax.request({
                async : false,
                url: '${g.createLink([controller: 'roleAccessRelation', action: 'save'])}', //params: {},
                params: {
                    roleId: roleId,
                    resourceIds: resourceIds.join()
                },
                success: function(response){
                        var result = Ext.decode(response.responseText);
                        if(result.success){
                            Ext.wintip.msg('操作成功!',  result.message);
                        }else{
                            Ext.wintip.error('操作失败!',  result.message);
                        }
                    },
                    failure: function(form, action) {
                        Ext.wintip.error('请求超时!',  '网络超时!');
                    }

            });

        }
        //取消全部
        function canelAll(){
//            resourceTree.expandAll();
            var rootnodes = resourceTree.getRootNode().childNodes;//获取主节点
            updateNodes(rootnodes,"canel");
        }
        //选中全部
        function checkedAll(){
//            resourceTree.expandAll();
            var rootnodes = resourceTree.getRootNode().childNodes;//获取主节点
            updateNodes(rootnodes,"selected");

        }
        function updateNodes(rootnodes,mode){

            for(var i=0;i<rootnodes.length;i++){
                if(mode == "selected"){
                    rootnodes[i].set('checked', true);
                }else if(mode == "canel"){
                    rootnodes[i].set('checked', false);
                }
//                console.info(rootnodes[i]);
//
//                console.info(rootnodes[i].childNodes);

                if(rootnodes[i].childNodes.length != 0){
                    updateNodes(rootnodes[i].childNodes,mode); //如果有子节点就递归。
                }
            }
        }

        function iterTree(rootnodes){
            for(var i=0;i<rootnodes.length;i++){
                if(rootnodes[i].get('checked') == true){
                    resourceIds.push(rootnodes[i].data.id)
                }
                if(rootnodes[i].childNodes.length != 0){
                    iterTree(rootnodes[i].childNodes); //如果有子节点就递归。
                }
            }

        }

</script>
</head>
<body style="overflow: auto;"></body>
</html>
