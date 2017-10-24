Ext.define('Esm.echart.Line', {

    extend: 'Esm.echart.AbstractEChart',
    alias: 'widget.echartline',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
   config : {
       dataUrl : '',

       option :{
//        title : {
//            text: '某楼盘销售情况',
//            subtext: '纯属虚构'
//        },
           tooltip : {
               trigger: 'axis'
           },
        legend: {
            top : 10,
            data:['意向','预购','成交']
        },
           toolbox: {
               show : true,
               feature : {
                   mark : {show: false},
                   dataView : {show: false, readOnly: false},
                   magicType : {show: true, type: ['line', 'bar', 'stack', 'tiled']},
                   restore : {show: false},
                   saveAsImage : {show: true}
               }
           },
//           dataZoom : {
//               show : true,
//               realtime : true,
//               start : 20,
//               end : 80
//           },
           calculable : true,

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
    responseData : function(me,option,data,responseData){
        var op = Ext.apply({},option || {},me.option);
        responseData = responseData || {}

        if(responseData.xAxis){
            op.xAxis[0].data = responseData.xAxis
        }

        if(data && Ext.isArray(data) && data.length > 0){
            if(Ext.isArray(data[0])){
                var len =  op.series.length;
                Ext.Array.each(data, function(value, index, countriesItSelf) {
                    if(index < len){
                        op.series[index].data = value;
                    }
                });
            }else {
                var series = [];
                var legends = [];
                var line = {
                    type:'line',
                    itemStyle:
                    {normal: {areaStyle: {type: 'default'}}}
                }

                Ext.Array.each(data, function(value, index, countriesItSelf) {
                    series.push(Ext.apply({name:value.name || '', data:value.value || []},line));
                    legends.push(value.name || '');
                });

                op.series = series;
                op.legend.data = legends;
            }
        }
        me.echart.setOption(op,true);
    },

    //初始化chart
    initChart : function(me){

        var lines = me.option.lines || [];
        var series = [];
        var legends = [];

        Ext.Array.each(lines, function(value, index){
            series.push({ type:'line',
                name:value,
                itemStyle:
                {normal: {areaStyle: {type: 'default'}}},
                data:[]
            });
            legends.push(value);
        });
        me.option.series = series;
        me.option.xAxis[0].data = me.option.x;

        me.option.legend.data = legends;

        delete me.option.x;
        delete me.option.lines;

    }

});
