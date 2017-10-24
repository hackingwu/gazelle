<%@ page import="relation.RoleAccessRelation" %><% Map roleAccessRelationModel = framework.ModelService.GetModel("roleAccessRelation") %>


<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>${roleAccessRelationModel.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var roleAccessRelationCreateWnd;
        var roleAccessRelationUpdateWnd;
        var roleAccessRelationDetailWnd;
        %{--var roleAccessRelationStore;--}%
        %{--var roleAccessRelationGrid;--}%
        var required = '<span style="color:red;">*</span>';

        <m:extCreateWnd model="roleAccessRelation" excluded="[]" controller="roleAccessRelation" action="createAction" />
        <m:extUpdateWnd model="roleAccessRelation" excluded="[]" controller="roleAccessRelation" action="updateAction" />
        <m:extDetailWnd model="roleAccessRelation" excluded="[]" controller="roleAccessRelation" action="detailAction" />

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
                    <m:extCRUDButtons model="roleAccessRelation" excluded="[]" />
                    %{--<m:extSingleSelectButton model="roleAccessRelation" excluded="[]" />
                    <m:extMultiSelectButton model="roleAccessRelation" excluded="[]" />
                    <m:extImportButton model="roleAccessRelation" />
                    <m:extExportButton model="roleAccessRelation" />--}%
                    '->',
                    %{--<m:extGangCombo model="roleAccessRelation" combo="[[label:'项目',name:'select_app_code',controller:'roleAccessRelation',action:'combo1'],[label:'版本',name:'select_app_version',controller:'roleAccessRelation',action:'combo2']]" />--}%
                    <m:extSearchButtons model="roleAccessRelation" mode="standard" excluded="[]" />
                ]
            });

            <m:extGridStore model="roleAccessRelation" excluded="[]" />
            <m:extGrid model="roleAccessRelation" excluded="[]" config="[column:8,rowHeight:25]" />

            <m:extLayout model="roleAccessRelation" />

            roleAccessRelationStore.loadPage(1); //extGridStore配置name属性时，需要手动修改此属性
        });
</script>
</head>
<body style="overflow: auto;"></body>
</html>
