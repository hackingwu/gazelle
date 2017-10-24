Ext.define('Esm.echart.Pie', {

    extend: 'Esm.echart.AbstractEChart',
    alias: 'widget.echartpie',

    requires: [
        'Ext.data.JsonStore'
    ],
    border:false,
   config : {

       dataUrl : '',

       option : {
//           title : {
//               text: '某站点用户访问来源',
//               subtext: '纯属虚构',
//               x:'center'
//           },
           tooltip : {
               trigger: 'item',
               formatter: "{a}<br/>{b} : {c} ({d}%)"
           },
           legend: {
               orient : 'vertical',
               x : 'left',
               data:['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
           },

           toolbox: {
               show : true,
               feature : {
                 //  mark : {show: true},
                   dataView : {show: false, readOnly: false},
                   restore : {show: true},
                   saveAsImage : {show: true}
               }
           },
           calculable : true,
           series : [
               {

                   type:'pie',
                   //         radius : '55%',
                   //          center: ['50%', 225],
                   data:[
                   ]
               }
           ]
       }
   },

    //ajax取回数据
    responseData : function(me,option,data){
        var op = Ext.apply({},option || {},me.option);

        var legends = [];

//        if(data.length == 0){
//            data = [{name:'',value:0}]
//        }

        op.series[0].data = data;

        Ext.each(data,function(v,i){
            if(Ext.isObject(v)){
                legends.push(v.name);
            }
        });
        op.legend.data = legends;

        me.echart.setOption(op,true);

    },

    //初始化chart
    initChart : function(me){

    }

});
