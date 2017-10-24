Ext.define('Esm.portlet.Line', {

    extend: 'Esm.portlet.EChartPanel',
    alias: 'widget.chartlineportlet',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
   config : {
       option :{
//        title : {
//            text: '某楼盘销售情况',
//            subtext: '纯属虚构'
//        },
           tooltip : {
               trigger: 'axis'
           },
//        legend: {
//            data:['意向','预购','成交']
//        },
           toolbox: {
               show : false,
               feature : {
                   mark : {show: true},
                   dataView : {show: true, readOnly: false},
                   magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                   restore : {show: true},
                   saveAsImage : {show: true}
               }
           },
           calculable : true,
           grid : {
               x:40,
               y:5,
               x2:20,
               y2:30
           },
           xAxis : [
               {
                   type : 'category',
                   boundaryGap : false
                   // data : ['周一','周二','周三','周四','周五','周六','周日']
               }
           ],
           yAxis : [
               {
                   type : 'value'
               }
           ],
           series : [
//            {
//                name:'成交',
//                type:'line',
//                smooth:true,
//                itemStyle: {normal: {areaStyle: {type: 'default'}}},
//                data:[10, 12, 21, 54, 260, 830, 710]
//            },
//            {
//                name:'意向',
//                type:'line',
//                smooth:true,
//                itemStyle: {normal: {areaStyle: {type: 'default'}}},
//                data:[1320, 1132, 601, 234, 120, 90, 20]
//            }
           ]
       }
   },

    //ajax取回数据
    responseData : function(me,option,data){
        var op = Ext.apply({},option || {},me.option);
        var len =  op.series.length;

        Ext.Array.each(data, function(value, index, countriesItSelf) {
            if(index < len){
                op.series[index].data = value;
            }
        });

        me.echart.setOption(op,true);

    },

    //初始化chart
    initChart : function(me){

        var lines = me.option.lines || [];
        var series = [];

        Ext.Array.each(lines, function(value, index){
            series.push({ type:'line',
                name:value,
                itemStyle:
                {normal: {areaStyle: {type: 'default'}}}
            });
        });
        me.option.series = series;
        me.option.xAxis[0].data = me.option.x;

        delete me.option.x;
        delete me.option.lines;

    }

});
