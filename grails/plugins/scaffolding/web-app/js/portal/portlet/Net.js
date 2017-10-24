Ext.define('Esm.portlet.Net', {

    extend: 'Esm.portlet.EChartPanel',
    alias: 'widget.chartnetportlet',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
    config :{
        option : {
//            title : {
//                text: '',
//                subtext: '',
//                x:'right',
//                y:'bottom'
//            },
            tooltip : {
                trigger: 'item',
                formatter: '{a} : {b}'
            },
//            legend: {
//                x: 'left',
//                data:['家人','朋友']
//            },
            series : [
                {
                    type:'force',
                   // name : "人物关系",
                    categories : [
//                        {
//                            name: '人物',
//                            itemStyle: {
//                                normal: {
//                                    color : '#ff7f50'
//                                }
//                            }
//                        },
//                        {
//                            name: '家人',
//                            itemStyle: {
//                                normal: {
//                                    color : '#87cdfa'
//                                }
//                            }
//                        },
//                        {
//                            name:'朋友',
//                            itemStyle: {
//                                normal: {
//                                    color : '#9acd32'
//                                }
//                            }
//                        }
                    ],
                    itemStyle: {
                        normal: {
                            label: {
                                show: true,
                                textStyle: {
                                    color: '#800080'
                                }
                            },
                            nodeStyle : {
                                brushType : 'both',
                                strokeColor : 'rgba(255,215,0,0.4)',
                                lineWidth : 8
                            }
                        },
                        emphasis: {
                            label: {
                                show: false
                                // textStyle: null      // 默认使用全局文本样式，详见TEXTSTYLE
                            },
                            nodeStyle : {
                                r: 30
                            },
                            linkStyle : {}
                        }
                    },
                    minRadius : 15,
                    maxRadius : 25,
                    density : 0.05,
                    attractiveness: 1.2,
                    nodes:[
//                        {category:0, name: '乔布斯', value : 10},
//                        {category:1, name: '丽萨-乔布斯',value : 2},
//                        {category:1, name: '保罗-乔布斯',value : 3},
//                        {category:1, name: '克拉拉-乔布斯',value : 3},
//                        {category:1, name: '劳伦-鲍威尔',value : 7},
//                        {category:2, name: '史蒂夫-沃兹尼艾克',value : 5},
//                        {category:2, name: '奥巴马',value : 8},
//                        {category:2, name: '比尔-盖茨',value : 9},
//                        {category:2, name: '乔纳森-艾夫',value : 4},
//                        {category:2, name: '蒂姆-库克',value : 4},
//                        {category:2, name: '龙-韦恩',value : 1}
                    ],
                    links : [
//                        {source : 1, target : 0, weight : 1},
//                        {source : 2, target : 0, weight : 2},
//                        {source : 3, target : 0, weight : 1},
//                        {source : 4, target : 0, weight : 2},
//                        {source : 5, target : 0, weight : 3},
//                        {source : 6, target : 0, weight : 6},
//                        {source : 7, target : 0, weight : 6},
//                        {source : 8, target : 0, weight : 1},
//                        {source : 9, target : 0, weight : 1},
//                        {source : 10, target : 0, weight : 1},
//                        {source : 3, target : 2, weight : 1},
//                        {source : 6, target : 2, weight : 1},
//                        {source : 6, target : 3, weight : 1},
//                        {source : 6, target : 4, weight : 1},
//                        {source : 6, target : 5, weight : 1},
//                        {source : 7, target : 6, weight : 6},
//                        {source : 7, target : 3, weight : 1},
//                        {source : 9, target : 6, weight : 1}
                    ]
                }
            ]
        }
    },
    //ajax取回数据
    responseData : function(me,option,data){
        var op = Ext.apply({},option || {},me.option);

        op.series[0].nodes = data.nodes;
        op.series[0].links = data.links;

        me.echart.setOption(op,true);

    },

    //初始化chart
    initChart : function(me){

       // var nodes = me.option.nodes || [];
       // var links = me.option.links || [];
        var category = me.option.category || [];

        var series =  me.option.series || [];

        series[0].categories = category;

        me.option.series = series;

        delete me.option.category;

    },
    html:''

});
