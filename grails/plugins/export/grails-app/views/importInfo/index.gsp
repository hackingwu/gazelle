<%@ page import="export.ImportInfo" %><% Map importInfoModel = framework.ModelService.GetModel("importInfo") %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>${importInfoModel.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var importInfoDetailWnd;
        var required = '<span style="color:red;">*</span>';

        //tab获得焦点时，执行该函数
        function onTabActive(){
            importInfoStore.reload();
        }

        <m:extDetailWnd model="importInfo" excluded="['filePath', 'md5']" controller="importInfo" action="detailAction" />

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
                    <m:extCRUDButtons model="importInfo" excluded="['create','update']" />
                    '->',
                    <m:extSearchButtons model="importInfo" mode="standard" excluded="['userName','md5','total','sucessConut','filePath','fileStatus','activityId']" />
                ]
            });

            <m:extGridStore model="importInfo" excluded="[]" />
            <m:extGrid model="importInfo" excluded="['userName','md5']" config="[column:8,rowHeight:25]" />


            <m:extLayout model="importInfo" />

            importInfoStore.loadPage(1); //extGridStore配置name属性时，需要手动修改此属性

            timedCount();
        });

        function timedCount()
        {
            setTimeout("timedCount()",10000);
            importInfoStore.reload();
        }
</script>
</head>
<body style="overflow: auto;"></body>
</html>
