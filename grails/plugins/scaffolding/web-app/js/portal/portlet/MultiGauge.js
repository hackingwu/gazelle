Ext.define('Esm.portlet.MultiGauge', {

    extend: 'Esm.portlet.EChartPanel',
    alias: 'widget.chartmultigaugeportlet',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,

    config :{
        option :{
            tooltip : {
                formatter: "{a} <br/>{c} {b}"
            },
            toolbox: {
                show : false,
                feature : {
                    mark : {show: true},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            series : [
                {
                    name:'招募比例',
                    type:'gauge',
                    min:0,
                    max:100,
                    splitNumber:4,
                    axisLine: {            // 坐标轴线
                        lineStyle: {       // 属性lineStyle控制线条样式
                            width: 10
                        }
                    },
                    axisTick: {            // 坐标轴小标记
                        length :15,        // 属性length控制线长
                        lineStyle: {       // 属性lineStyle控制线条样式
                            color: 'auto'
                        }
                    },
                    splitLine: {           // 分隔线
                        length :20,         // 属性length控制线长
                        lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
                            color: 'auto'
                        }
                    },
                    title : {
                        textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                            fontWeight: 'bolder',
                            fontSize: 13
//                            fontStyle: 'italic'
                        }
                    },
                    detail : {
                        textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                            fontWeight: 'bolder'
                        }
                    },
                    data:[{value: 40, name: '招募比例'}]
                },
                {
                    name:'按时授时比例',
                    type:'gauge',
                    center : ['25%', '55%'],    // 默认全局居中
                    radius : '50%',
                    min:0,
                    max:100,
                    endAngle:45,
                    splitNumber:4,
                    axisLine: {            // 坐标轴线
                        lineStyle: {       // 属性lineStyle控制线条样式
                            width: 8
                        }
                    },
                    axisTick: {            // 坐标轴小标记
                        length :12,        // 属性length控制线长
                        lineStyle: {       // 属性lineStyle控制线条样式
                            color: 'auto'
                        }
                    },
                    splitLine: {           // 分隔线
                        length :20,         // 属性length控制线长
                        lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
                            color: 'auto'
                        }
                    },
                    pointer: {
                        width:5
                    },
                    title : {
                        offsetCenter: [0, '-30%']       // x, y，单位px
                    },
                    detail : {
                        textStyle: {       // 其余属性默认使用全局文本样式，详见TEXTSTYLE
                            fontWeight: 'bolder',
                            fontSize: 23
                        }
                    },
                    data:[{value: 88.88, name: ''}]
                },
                {
                    name:'男生比例',
                    type:'gauge',
                    center : ['75%', '50%'],    // 默认全局居中
                    radius : '50%',
                    min:0,
                    max:100,
                    startAngle:135,
                    endAngle:45,
                    splitNumber:2,
                    axisLine: {            // 坐标轴线
                        lineStyle: {       // 属性lineStyle控制线条样式
                            color: [[0.2, '#ff4500'],[0.8, '#48b'],[1, '#228b22']],
                            width: 8
                        }
                    },
                    axisTick: {            // 坐标轴小标记
                        splitNumber:5,
                        length :10,        // 属性length控制线长
                        lineStyle: {       // 属性lineStyle控制线条样式
                            color: 'auto'
                        }
                    },
//                    axisLabel: {
//                        formatter:function(v){
//                            switch (v + '') {
//                                case '0' : return '';
//                                case '1' : return '';
//                                case '2' : return '';
//                            }
//                        }
//                    },
                    splitLine: {           // 分隔线
                        length :15,         // 属性length控制线长
                        lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
                            color: 'auto'
                        }
                    },
                    pointer: {
                        width:2
                    },
                    title : {
                        show: false
                    },
                    detail : {
                        show: false
                    },
                    data:[{value: 30.35, name: '%'}]
                },
                {
                    name:'女生比例',
                    type:'gauge',
                    center : ['75%', '50%'],    // 默认全局居中
                    radius : '50%',
                    min:0,
                    max:100,
                    startAngle:315,
                    endAngle:225,
                    splitNumber:2,
                    axisLine: {            // 坐标轴线
                        lineStyle: {       // 属性lineStyle控制线条样式
                            color: [[0.2, '#ff4500'],[0.8, '#48b'],[1, '#228b22']],
                            width: 8
                        }
                    },
                    axisTick: {            // 坐标轴小标记
                        show: false
                    },
//                    axisLabel: {
//                        formatter:function(v){
//                            switch (v + '') {
//                                case '0' : return '';
//                                case '1' : return '';
//                                case '2' : return '';
//                            }
//                        }
//                    },
                    splitLine: {           // 分隔线
                        length :15,         // 属性length控制线长
                        lineStyle: {       // 属性lineStyle（详见lineStyle）控制线条样式
                            color: 'auto'
                        }
                    },
                    pointer: {
                        width:2
                    },
                    title : {
                        show: false
                    },
                    detail : {
                        show: false
                    },
                    data:[{value: 69.47, name: '%'}]
                }
            ]
        }

    },

    //ajax取回数据
    responseData : function(me,option,data){
        var op = Ext.apply({},option || {},me.option);

        op.series[0].data = data;

        me.echart.setOption(op,true);

    },

    //初始化chart
    initChart : function(me){

    }

    });
