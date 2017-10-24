Ext.define('Esm.echart.Bar', {

    extend: 'Esm.echart.AbstractEChart',
    alias: 'widget.echartbar',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
   config : {
       dataUrl : '',
       option :{
           // title : {
           //     text: '某地区蒸发量和降水量',
           //     subtext: '纯属虚构'
           //  },
           tooltip : {
               trigger: 'axis'
           },
            legend: {
                data:['滴滴打车','快递打车']
            },
           toolbox: {
               show : true,
               feature : {
                   mark : {show: false},
                   dataView : {show: false, readOnly: false},
                   magicType : {show: true, type: ['line', 'bar']},
                   restore : {show: false},
                   saveAsImage : {show: true}
               }
           },
           calculable : true,
//           grid : {
//               x:40,
//               y:5,
//               x2:20,
//               y2:30
//           },
           xAxis : [
               {
                   type : 'category',
                   data : ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
               }
           ],
           yAxis : [
               {
                   type : 'value',
                   splitArea : {show : true}
               }
           ],
           series : [
//                {
//                    name:'滴滴打车',
//                    type:'bar',
//                    data:[2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
//                    markPoint : {
//                        data : [
//                            {type : 'max', name: '最大值'},
//                            {type : 'min', name: '最小值'}
//                        ]
//                    },
//                    markLine : {
//                        data : [
//                            {type : 'average', name: '平均值'}
//                        ]
//                    }
//                },
//                {
//                    name:'快的打车',
//                    type:'bar',
//                    data:[2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3],
//                    markPoint : {
//                        data : [
//                            {name : '年最高', value : 182.2, xAxis: 7, yAxis: 183, symbolSize:18},
//                            {name : '年最低', value : 2.3, xAxis: 11, yAxis: 3}
//                        ]
//                    },
//                    markLine : {
//                        data : [
//                            {type : 'average', name : '平均值'}
//                        ]
//                    }
//                }
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
                    type:'bar',
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

        var bars = me.option.bars || [];
        var series = [];
        var legends = [];


        Ext.Array.each(bars, function(value, index){
            series.push({ type:'bar',
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
        delete me.option.bars;

    }

});
