Ext.define('Esm.portlet.DynLine', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.chartdynlineportlet',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
    generateData: function(){
        var data = [{
                name: 0,
                djia: 10000,
                sp500: 1100
            }],
            i;
        for (i = 1; i < 50; i++) {
            data.push({
                name: i,
                sp500: data[i - 1].sp500 + ((Math.floor(Math.random() * 2) % 2) ? -1 : 1) * Math.floor(Math.random() * 7),
                djia: data[i - 1].djia + ((Math.floor(Math.random() * 2) % 2) ? -1 : 1) * Math.floor(Math.random() * 7)
            });
        }
        return data;
    },

    option : {
//        title : {
//            text: '动态数据',
//            subtext: '纯属虚构'
//        },
        tooltip : {
            trigger: 'axis'
        },
        legend: {
            data:['最新成交价', '预购队列']
        },
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
        dataZoom : {
            show : false,
            realtime: true,
            start : 50,
            end : 100
        },
        xAxis : [
            {
                type : 'category',
                boundaryGap : true,
                data : (function(){
                    var now = new Date();
                    var res = [];
                    var len = 10;
                    while (len--) {
                        res.unshift(now.toLocaleTimeString().replace(/^\D*/,''));
                        now = new Date(now - 2000);
                    }
                    return res;
                })()
            },
            {
                type : 'category',
                boundaryGap : true,
                splitline : {show : false},
                data : (function(){
                    var res = [];
                    var len = 10;
                    while (len--) {
                        res.push(len + 1);
                    }
                    return res;
                })()
            }
        ],
        yAxis : [
            {
                type : 'value',
                scale: true,
                precision:1,
                power:1,
                name : '价格',
                boundaryGap: [0.2, 0.2],
                splitArea : {show : true}
            },
            {
                type : 'value',
                scale: true,
                name : '预购量',
                boundaryGap: [0.2, 0.2]
            }
        ],
        series : [
            {
                name:'预购队列',
                type:'bar',
                xAxisIndex: 1,
                yAxisIndex: 1,
                itemStyle: {
                    normal: {
                        color : 'rgba(135,206,205,0.4)'
                    }
                },
                data:(function(){
                    var res = [];
                    var len = 10;
                    while (len--) {
                        res.push(Math.round(Math.random() * 1000));
                    }
                    return res;
                })()
            },
            {
                name:'最新成交价',
                type:'line',
                itemStyle: {
                    normal: {
                        // areaStyle: {type: 'default'},
                        lineStyle: {
                            shadowColor : 'rgba(0,0,0,0.4)'
                        }
                    }
                },
                data:(function(){
                    var res = [];
                    var len = 10;
                    while (len--) {
                        res.push((Math.random()*10 + 5).toFixed(1) - 0);
                    }
                    return res;
                })()
            }
        ]
    },
    listeners :{
        boxready : {
            fn:function(me,wt,ht,e){
                var myChart = echarts.init(me.body.dom);
                myChart.setOption(me.option);

                var lastData = 11;
                var axisData,timeTicket;
                clearInterval(timeTicket);
                timeTicket = setInterval(function(){
                    lastData += Math.random() * ((Math.round(Math.random() * 10) % 2) == 0 ? 1 : -1);
                    lastData = lastData.toFixed(1) - 0;
                    axisData = (new Date()).toLocaleTimeString().replace(/^\D*/,'');

                    // 动态数据接口 addData
                    myChart.addData([
                        [
                            0,        // 系列索引
                            Math.round(Math.random() * 1000), // 新增数据
                            true,     // 新增数据是否从队列头部插入
                            false     // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
                        ],
                        [
                            1,        // 系列索引
                            lastData, // 新增数据
                            false,    // 新增数据是否从队列头部插入
                            false,    // 是否增加队列长度，false则自定删除原有数据，队头插入删队尾，队尾插入删队头
                            axisData  // 坐标轴标签
                        ]
                    ]);
                }, 2000);
            }
        }
    },
    html:'<div id="chartdynlineportlet" style="width: 100%;height: 100%"></div>'

});
