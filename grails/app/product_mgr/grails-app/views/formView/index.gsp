<%@ page import="demo.FormView" %><% Map formViewModel = framework.ModelService.GetModel("formView") %>
%{--formView--}%
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>${formViewModel.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">


        var required = '<span style="color:red;">*</span>';


        <m:extFormUpdateWnd model="formView" controller="formView"  />
        <m:extResetWnd model="formView" controller="formView"/>

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker" />', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload" />' //上传控件路径，不使用上传控件时，可删除该代码
            }
        });

        Ext.require(['Go.form.field.DateTime']); //加载带时间选择的日期控件类,不使用时可删除该代码
        Ext.require(['Ext.upload.ImgUpload']); //加载图片上传类，不使用时可删除该代码
        Ext.require(['Ext.upload.FileUpload']); //加载文件上传类，不使用时可删除该代码

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extFormButtons model="formView" excluded="[]" />
                ]
            });
            <m:extForm model="formView" excluded="[]" />


            <m:extLayout model="formView" layout="formView"/>

        });
    </script>
</head>
<body style="overflow: auto;"></body>
</html>
