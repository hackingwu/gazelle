Ext.define('Esm.portlet.Bar', {

    extend: 'Esm.portlet.EChartPanel',
    alias: 'widget.chartbarportlet',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
    config :{
        option :{
            // title : {
            //     text: '某地区蒸发量和降水量',
            //     subtext: '纯属虚构'
            //  },
            tooltip : {
                trigger: 'axis'
            },
//            legend: {
//                data:['滴滴打车','快递打车']
//            },
            toolbox: {
                show : false,
                feature : {
                    mark : {show: true},
                    dataView : {show: true, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar']},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            calculable : true,
            grid : {
                x:50,
                y:30,
                x2:50,
                y2:30
            },
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

        var bars = me.option.bars || [];
        var series = [];

        Ext.Array.each(bars, function(value, index){
            series.push({ type:'bar',
                name:value,
                itemStyle:
                {normal: {areaStyle: {type: 'default'}}}
            });
        });
        me.option.series = series;
        me.option.xAxis[0].data = me.option.x;

        delete me.option.x;
        delete me.option.bars;

    }

});
