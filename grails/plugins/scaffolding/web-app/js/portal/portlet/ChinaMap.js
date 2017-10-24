Ext.define('Esm.portlet.ChinaMap', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.chartchinamapportlet',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
    config : {
        option : {
            // title : {
            //    text: 'iphone销量',
            //    subtext: '纯属虚构',
            //    x:'center'
            // },
            tooltip : {
                trigger: 'item'
            },
//            legend: {
//                orient: 'vertical',
//                x:'left',
//                data:['iphone3','iphone4','iphone5']
//            },

            dataRange: {
                min: 0,
                max: 100000,
                color:['orangered','yellow','lightskyblue'],
                text:['高','低'],           // 文本，默认为数值文本
                calculable : true
            },
//            toolbox: {
//                show : true,
//                orient : 'vertical',
//                x: 'right',
//                y: 'center',
//                feature : {
//                    mark : {show: true},
//                    dataView : {show: true, readOnly: false},
//                    restore : {show: true},
//                    saveAsImage : {show: true}
//                }
//            },
            series : [
//                {
//                    name: 'iphone3',
//                    type: 'map',
//                    mapType: 'china',
//                    itemStyle:{
//                        normal:{label:{show:true}},
//                        emphasis:{label:{show:true}}
//                    },
//                    data:[
//                        {name: '北京',value: Math.round(Math.random()*1000)},
//                        {name: '天津',value: Math.round(Math.random()*1000)},
//                        {name: '上海',value: Math.round(Math.random()*1000)},
//                        {name: '重庆',value: Math.round(Math.random()*1000)},
//                        {name: '河北',value: Math.round(Math.random()*1000)},
//                        {name: '河南',value: Math.round(Math.random()*1000)},
//                        {name: '云南',value: Math.round(Math.random()*1000)},
//                        {name: '辽宁',value: Math.round(Math.random()*1000)},
//                        {name: '黑龙江',value: Math.round(Math.random()*1000)},
//                        {name: '湖南',value: Math.round(Math.random()*1000)},
//                        {name: '安徽',value: Math.round(Math.random()*1000)},
//                        {name: '山东',value: Math.round(Math.random()*1000)},
//                        {name: '新疆',value: Math.round(Math.random()*1000)},
//                        {name: '江苏',value: Math.round(Math.random()*1000)},
//                        {name: '浙江',value: Math.round(Math.random()*1000)},
//                        {name: '江西',value: Math.round(Math.random()*1000)},
//                        {name: '湖北',value: Math.round(Math.random()*1000)},
//                        {name: '广西',value: Math.round(Math.random()*1000)},
//                        {name: '甘肃',value: Math.round(Math.random()*1000)},
//                        {name: '山西',value: Math.round(Math.random()*1000)},
//                        {name: '内蒙古',value: Math.round(Math.random()*1000)},
//                        {name: '陕西',value: Math.round(Math.random()*1000)},
//                        {name: '吉林',value: Math.round(Math.random()*1000)},
//                        {name: '福建',value: Math.round(Math.random()*1000)},
//                        {name: '贵州',value: Math.round(Math.random()*1000)},
//                        {name: '广东',value: Math.round(Math.random()*1000)},
//                        {name: '青海',value: Math.round(Math.random()*1000)},
//                        {name: '西藏',value: Math.round(Math.random()*1000)},
//                        {name: '四川',value: Math.round(Math.random()*1000)},
//                        {name: '宁夏',value: Math.round(Math.random()*1000)},
//                        {name: '海南',value: Math.round(Math.random()*1000)},
//                        {name: '台湾',value: Math.round(Math.random()*1000)},
//                        {name: '香港',value: Math.round(Math.random()*1000)},
//                        {name: '澳门',value: Math.round(Math.random()*1000)}
//                    ]
//                }
            ]
        }
    },
    listeners :{
        boxready : {
            fn:function(me,wt,ht,e){
                var myChart = echarts.init(me.body.dom);
                var params = Ext.apply({},me.option.params);
                var mapType = '福建';

                if(me.option.mapType){
                    mapType = me.option.mapType;
                }

                var maps = me.option.maps || [];

                var series = [];
                Ext.Array.each(maps, function(value, index){
                    series.push({ type:'map',
                        mapType: mapType,
                        name:value,
                        itemStyle:{
                             normal:{label:{show:true}},
                             emphasis:{label:{show:true}}
                      }
                    });
                });

                me.option.series = series;



                //  alert( Ext.JSON.encode(me.option));
                Ext.Ajax.request({
                    url: me.dataUrl,
                    params: params,
                    success: function(response){


                        var option = Ext.apply({},me.option,me.config.option);

                        var text = response.responseText;
                        var op = Ext.JSON.decode(text);
                        if(Ext.isArray(op)){
                            var len =  option.series.length;
                            Ext.Array.each(op, function(value, index, countriesItSelf) {
                                if(index < len){
                                    option.series[index].data = value;
                                }
                            });
                        }else {
                            var title = op.title;
                            if(title){
                                me.ownerCt.setTitle(title);
                            }
                            var len =  option.series.length;
                            Ext.Array.each(op.data, function(value, index, countriesItSelf) {
                                if(index < len){
                                    option.series[index].data = value;
                                }
                            });
                            //值域最大值配置
                            if(op.max){
                                option.dataRange.max = op.max;
                            }
                           // Ext.apply(option,op);
                        }
                        myChart.setOption(option);

                    }
                });
            }
        }
    }
});
