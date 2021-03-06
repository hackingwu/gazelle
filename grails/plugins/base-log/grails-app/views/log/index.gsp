<%@ page import="com.nd.grails.plugins.log.Log" %><% Map logModel = framework.ModelService.GetModel("log") %>


<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>${logModel.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var logCreateWnd;
        var logUpdateWnd;
        var logDetailWnd;
        %{--var logStore;--}%
        %{--var logGrid;--}%
        var required = '<span style="color:red;">*</span>';

        <m:extExportFileFunc controller="log" action="exportData" funcName="logExport" />
        <m:extDetailWnd model="log" excluded="['objectId','level','application','module']" controller="log" action="detailAction" />

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker" />' //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
            }
        });

        Ext.require(['Go.form.field.DateTime']); //带时间选择的日期控件

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extCRUDButtons model="log" excluded="['create','update','delete']" />
                    <m:extOtherButtons items="[[code:'log_export',text:'导出日志',icon:'icon-data-export',handler:'logExport()']]" />
                    '->',
                    <m:extSearchButtons model="log" mode="standard" included="['operator','birthday','catalog']" excluded="[]" />
                ]
            });

            <m:extGridStore model="log" excluded="[]" />
            <m:extGrid model="log" included="['operator','operatorName','birthday','logMsg','catalog','ip']" excluded="[]" config="[column:8,rowHeight:25]" dataBind="1"/>
            <m:extDetailBindForm model="log" excluded="[]" />
            <m:extLayout model="log" layout="dataBindingWithRightImg"/>

            logStore.loadPage(1); //extGridStore配置name属性时，需要手动修改此属性
        });
    </script>
</head>
<body style="overflow: auto;"></body>
</html>
