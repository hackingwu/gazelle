<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>饼图</title>
    <m:extResource/>
    <script type="text/javascript">
    var scope = "global";
    function checkScope() {
        document.write(scope);
        var scope = "local";
        document.write(scope);
    }
    checkScope();
    document.write(scope);

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
                %{--dataUrl="echart/lineChart"--}%
                %{--conditions="[[name:'name'],[name:'dateTime',type:'time']]"/>--}%

        %{--<m:extChart chartType="bar" option="[bars:['德国','葡萄牙'],x:['周一', '周二', '周三', '周四', '周五', '周六', '周日']]"--}%
                %{--dataUrl="echart/barChart"--}%
                %{--conditions="[[name:'name'],[name:'dateTime',type:'time']]"/>--}%

        <m:extChart chartType="pie"
                dataUrl="${g.resource([file: "chart/pieChart"])}"

                />


    });

    </script>
</head>
<body style="overflow: auto;height: 500px"></body>
<script src="${g.resource([plugin: "scaffolding", dir: "/echarts", file: "/echarts-plain-original-map.js"])}" type="text/javascript"></script>

</html>
