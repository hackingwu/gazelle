<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>管理</title>
    <m:extResource/>
    <script type="text/javascript">
       // Ext.Loader.setPath('Ext.app', 'static/plugins/scaffolding-2.0.3/ext/portal/classes');
        Ext.Loader.setConfig({
            disableCaching :  false,
            paths:{

                'Ext.app' : '${g.resource([plugin: "scaffolding", dir: "/portal/classes"])}',
                'Esm.app' : '${g.resource([plugin: "scaffolding", dir: "/portal"])}'
            }
        });
    </script>
    <link href="${g.resource([plugin: "scaffolding", dir: "/portal", file: "/portal.css"])}" rel="stylesheet" type="text/css"/>
    <script src="${g.resource([plugin: "scaffolding", dir: "/portal", file: "/portal.js"])}" type="text/javascript"></script>


    <script type="text/javascript">
        Ext.require([
            'Ext.layout.container.*',
            'Ext.resizer.Splitter',
            'Ext.fx.target.Element',
            'Ext.fx.target.Component',
            'Ext.window.Window',
            'Ext.data.JsonStore',
            'Ext.app.Portlet',
            'Ext.app.PortalColumn',
            'Ext.app.PortalPanel',
            'Ext.app.Portlet',
            'Ext.app.PortalDropZone',
            'Ext.app.GridPortlet',
            'Ext.app.ChartPortlet',
            'Esm.app.Workbench'

        ]);


        Ext.onReady(function(){

            var option = {
        
                tooltip : {
                    trigger: 'axis'
                },
                legend: {
                    data:['最高气温','最低气温']
                },
                toolbox: {
                    show : true,
                    feature : {
                        mark : {show: true},
                        dataView : {show: true, readOnly: false},
                        magicType : {show: true, type: ['line', 'bar']},
                        restore : {show: true},
                        saveAsImage : {show: true}
                    }
                },
                calculable : true,
                xAxis : [
                    {
                        type : 'category',
                        boundaryGap : false,
                        data : ['周一','周二','周三','周四','周五','周六','周日']
                    }
                ],
                yAxis : [
                    {
                        type : 'value',
                        axisLabel : {
                            formatter: '{value} °C'
                        },
                        splitArea : {show : true}
                    }
                ],
                series : [
                    {
                        name:'最高气温',
                        type:'line',
                        itemStyle: {
                            normal: {
                                lineStyle: {
                                    shadowColor : 'rgba(0,0,0,0.4)',
                                    shadowBlur: 5,
                                    shadowOffsetX: 3,
                                    shadowOffsetY: 3
                                }
                            }
                        },
                        data:[11, 11, 15, 13, 12, 13, 10],
                        markPoint : {
                            data : [
                                {type : 'max', name: '最大值'},
                                {type : 'min', name: '最小值'}
                            ]
                        },
                        markLine : {
                            data : [
                                {type : 'average', name: '平均值'}
                            ]
                        }
                    },
                    {
                        name:'最低气温',
                        type:'line',
                        itemStyle: {
                            normal: {
                                lineStyle: {
                                    shadowColor : 'rgba(0,0,0,0.4)',
                                    shadowBlur: 5,
                                    shadowOffsetX: 3,
                                    shadowOffsetY: 3
                                }
                            }
                        },
                        data:[1, -2, 2, 5, 3, 2, 0],
                        markPoint : {
                            data : [
                                {name : '周最低', value : -2, xAxis: 1, yAxis: -1.5}
                            ]
                        },
                        markLine : {
                            data : [
                                {type : 'average', name : '平均值'}
                            ]
                        }
                    }
                ]
            };

            Ext.create('Esm.app.Workbench',{
                renderTo:document.getElementById('portal')
            });
           // var portal = Ext.get('app-portal');
           // alert(portal);

            //portal.remove(Ext.get('col-4'));


          //  var domMain =document.getElementById('chartlineportlet');
         //  var myChart = echarts.init(domMain);
         //  myChart.setOption(option);

           // var p7 = Ext.get('portlet-7');
           // p7.remove();
            //p7.getHeader().close();

            var store = Ext.create('Ext.data.JsonStore', {
                fields: ['value'],
                data: [
                    { 'value':80 }
                ]
            });

            Ext.create('Ext.chart.Chart', {
                renderTo: Ext.get('chartgauge'),
                store: store,
                width: 400,
                height: 250,
                animate: true,
                insetPadding: 30,
                axes: [{
                    type: 'gauge',
                    position: 'gauge',
                    minimum: 0,
                    maximum: 100,
                    steps: 10,
                    margin: 10
                }],
                series: [{
                    type: 'gauge',
                    field: 'value',
                    donut: 30,
                    colorSet: ['#F49D10', '#ddd']
                }]
            });
        });
    </script>
</head>

<body style="overflow: auto;">
<div id="domMain" style="width: 1000px;height: 600px"></div>
</body>
<script src="${g.resource([plugin: "scaffolding", dir: "/echarts", file: "/echarts-plain-original-map.js"])}" type="text/javascript"></script>

</html>
