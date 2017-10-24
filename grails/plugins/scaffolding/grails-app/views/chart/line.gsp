<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>折线图</title>
    <m:extResource/>
    <script type="text/javascript">

    Ext.Loader.setConfig({
        disableCaching :  false,
        paths:{
            'Esm.echart' : '${g.resource([plugin: "scaffolding", dir: "/js/charts"])}'
        }
    });

    Ext.require([
        'Esm.echart.AbstractEChart',
        'Esm.echart.Line',
        'Esm.echart.Bar',
        'Esm.echart.Pie',
        'Esm.echart.EsmChart'
    ]);

    Ext.onReady(function(){
//        Ext.create('Esm.echart.EsmChart',{dataUrl:'echart/lineChart',  option : {
//            lines :[
//                "成交1", "y轴"
//            ],
//            x : ['1一', '二', '周三', '周四', '周五', '周六', '周日']
//        }});

//        Ext.create('Esm.echart.EsmChart',{dataUrl:'echart/lineChart',chartType:'line',
//            option : {"lines":["德国","葡萄牙"],"x":["周一","周二","周三","周四","周五","周六","周日"]},
//            params:{},
//            conditions:[{"name":"name"},{"name":"dateTime",fieldLabel:"日期","type":"time"}]
//        });

        %{--<m:extChart chartType="line" option="[lines:['德国','葡萄牙'],x:['周一', '周二', '周三', '周四', '周五', '周六', '周日']]"--}%
        %{--dataUrl="${g.resource([file: "chart/lineChart"])}"--}%
        %{--conditions="[[name:'name'],[name:'dateTime',type:'time']]"/>--}%

//        var panel = Ext.create('Ext.Panel',{
//            layout : 'border',
//            region: 'center',
//            items :[{
//                region: 'east',
//                collapsible: true,
//                collapsed : true,
//                title: '详细记录',
//                split: true,
//                width: '50%',
//                html:'ddddd',
//                items:{xtype:'panel',html:'grid'}
//            },{
//                region: 'center',
//                margins: '2 5 5 0',
//                activeItem: 0,
//                border: false,
//                html:'center'
//            }]
//        });

        <m:extChart chartType="line"
                dataUrl="${g.resource([file: "chart/lineChart"])}"
                />

        //创建toolbar
//        var toolbar = Ext.create('Ext.Toolbar', {
//            // minWidth: document.documentElement.clientHeight,
//            items: [
//                {
//                    itemId: 'search',
//                    text: '查询',
//                    iconCls: 'icon-search',
//                    listeners : {
//                        click: {
//                            fn : function(){
//
//                            }
//                        }
//                    }
//
//                }
//            ]
//        });
//
//        Ext.create('Ext.Viewport', {
//            layout: 'border',
//            items: [{
//                region: 'north',
//                autoScroll: true,
//                border:false,
//                items: toolbar
//            },chart]
//        });

    });

    </script>
</head>
<body style="overflow: auto;"></body>
<script src="${g.resource([plugin: "scaffolding", dir: "/echarts", file: "/echarts-plain-original-map.js"])}" type="text/javascript"></script>

</html>
