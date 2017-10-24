<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>${grailsApplication.config.viewobjects.viewobjects.title}</title>
    <m:extResource/>
    <script type="text/javascript">
        var viewobjectsDetailWnd;
        var required = '<span style="color:red;">*</span>';

        <m:extDetailWnd model="viewobjects" excluded="[]" controller="viewobjects" action="detailAction" />

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker"/>', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.multiUpload':'<g:resource plugin="scaffolding" dir="js/multiUpload"/>',
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload"/>' //上传控件路径，不使用上传控件时，可删除该代码
            }
        });

        Ext.require(['Go.form.field.DateTime']); //带时间选择的日期控件
        Ext.require(['Ext.upload.ImgUpload']);   //图片上传
        Ext.require(['Ext.upload.FileUpload']);  //单文件上传
        Ext.require(['Ext.multiUpload.Panel']);  //多文件上传

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                minWidth: 800,
                items: [
                    <m:extCRUDButtons model="viewobjects" view="viewobject" excluded="['create','update','delete']" />
                    '->',
                    <m:extSearchButtons model="viewobjects" mode="standard" excluded="[]" config="[autoQuery:true]" />
                ]
            });

            <m:extGridStore model="viewobjects" excluded="[]" />
            <m:extGrid model="viewobjects" excluded="[]" config="[column:8,rowHeight:29]" />

            <m:extLayout model="viewobjects" />

            viewobjectsStore.loadPage(1);
        });
    </script>
</head>
<body style="overflow: auto;"></body>
</html>
