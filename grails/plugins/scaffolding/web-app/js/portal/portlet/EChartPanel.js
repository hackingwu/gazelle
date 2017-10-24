Ext.define('Esm.portlet.EChartPanel', {

    extend: 'Esm.portlet.PortletPanel',
    alias: ['widget.echartportlet','widget.echartportletpanel'],

    requires: [
       // 'Ext.data.JsonStore'
    ],


    beforeLoad : function(){

    },

    //ajax取回数据
    responseData : function(me,option,data){

        me.echart.setOption(Ext.apply({},option,me.option));

    },

    //portlet框架加载完毕后，初始化body内容
    boxready : function(me){
         me.echart = echarts.init(me.body.dom);
         me._option = me.option;
         var op = Ext.apply({},me.config.option || {});
         op = me.applyEChartOption(op,me.option);
         me.option = op;
         this.initChart(me);

        delete me.option.params;
    },
    //初始化chart
    initChart : function(me){

    },
    applyEChartOption: function(target,source){
        var t,s;
        for(var p in source){
            s = source[p];
            t = target[p];
            if(Ext.isArray(s)){
                if(t && Ext.isArray(t)){
                    for(var i = 0,j = s.length ;i < j; i++){
                        t[i] = Ext.apply(t[i] || {},s[i]);
                    }
                    target[p] = t;
                }else {
                    target[p] = s;
                }
            }else if(Ext.isObject(s)){
                if(t){
                    target[p] =Ext.apply(t,s);
                }else {
                    target[p] = source[p];
                }
            }else {
                target[p] = source[p];
            }
        }

        return target;
    }

});
