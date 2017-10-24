<% import grails.persistence.Event %><%=packageName%><%

    Map mDomainClass = framework.ModelService.GetModel("${domainClass.propertyName}")

    out << "<" << "%" <<""" Map ${domainClass.propertyName}Model = framework.ModelService.GetModel("${domainClass.propertyName}") """ << "%" << ">"
%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>\${${domainClass.propertyName}Model.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var ${domainClass.propertyName}CreateWnd;
        var ${domainClass.propertyName}UpdateWnd;
        var ${domainClass.propertyName}DetailWnd;
        %{--var ${domainClass.propertyName}Store;--}%
        %{--var ${domainClass.propertyName}Grid;--}%
        var required = '<span style="color:red;">*</span>';

        <m:extCreateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="createAction" />
        <m:extUpdateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="updateAction" />
        <m:extDetailWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="detailAction" />

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
                    <m:extCRUDButtons model="${domainClass.propertyName}" excluded="[]" />
                    %{--<m:extSingleSelectButton model="${domainClass.propertyName}" excluded="[]" />
                    <m:extMultiSelectButton model="${domainClass.propertyName}" excluded="[]" />
                    <m:extImportButton model="${domainClass.propertyName}" />
                    <m:extExportButton model="${domainClass.propertyName}" />--}%
                    '->',
                    %{--<m:extGangCombo model="${domainClass.propertyName}" combo="[[label:'项目',name:'select_app_code',controller:'${domainClass.propertyName}',action:'combo1'],[label:'版本',name:'select_app_version',controller:'${domainClass.propertyName}',action:'combo2']]" />--}%
                    <m:extSearchButtons model="${domainClass.propertyName}" mode="standard" excluded="[]" />
                ]
            });

            <m:extGridStore model="${domainClass.propertyName}" excluded="[]" />
            <m:extGrid model="${domainClass.propertyName}" excluded="[]" config="[column:8,rowHeight:25]" />

            <m:extLayout model="${domainClass.propertyName}" />

            ${domainClass.propertyName}Store.loadPage(1); //extGridStore配置name属性时，需要手动修改此属性
        });
</script>
</head>
<body style="overflow: auto;"></body>
</html>
