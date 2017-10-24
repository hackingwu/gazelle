<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>我的桌面</title>
    <m:extResource/>
    <script type="text/javascript">
        // Ext.Loader.setPath('Ext.app', 'static/plugins/scaffolding-2.0.3/js/portal/classes');
        Ext.Loader.setConfig({
            disableCaching :  false,
            paths:{

                'Ext.app' : '${g.resource([plugin: "scaffolding", dir: "/js/portal/classes"])}',
                'Esm.app' : '${g.resource([plugin: "scaffolding", dir: "/js/portal"])}',
                'Esm.portlet' : '${g.resource([plugin: "scaffolding", dir: "/js/portal/portlet"])}'
    }
    });
</script>
    <link href="${g.resource([plugin: "scaffolding", dir: "/js/portal", file: "/portal.css"])}" rel="stylesheet" type="text/css"/>
            <script src="${g.resource([plugin: "scaffolding", dir: "/js/portal", file: "/portal.js"])}" type="text/javascript"></script>


    <script src="${g.resource([plugin: "scaffolding", dir: "/rgraph", file: "/RGraph.common.core.js"])}" type="text/javascript"></script>
    <script src="${g.resource([plugin: "scaffolding", dir: "/rgraph", file: "/RGraph.common.dynamic.js"])}" type="text/javascript"></script>
    <script src="${g.resource([plugin: "scaffolding", dir: "/rgraph", file: "/RGraph.common.effects.js"])}" type="text/javascript"></script>
    <script src="${g.resource([plugin: "scaffolding", dir: "/rgraph", file: "/RGraph.gauge.js"])}" type="text/javascript"></script>


    <script type="text/javascript">
        Ext.require([
            'Ext.layout.container.*',
            'Ext.resizer.Splitter',
            'Ext.fx.target.Element',
            'Ext.fx.target.Component',
            'Ext.window.Window',
            'Ext.data.JsonStore',
            'Esm.app.Desktop'

        ]);
        //生成桌面的portlet，定义在ExtChartTagLib中
        <m:extPortal/>

    </script>
</head>

<body style="overflow: auto;">


</body>
<script src="${g.resource([plugin: "scaffolding", dir: "/echarts", file: "/echarts-plain-original-map.js"])}" type="text/javascript"></script>

</html>
