Ext.define('Esm.echart.AbstractEChart', {

    alias: ['widget.echart'],

    requires: [
       // 'Ext.data.JsonStore'
    ],


    config : {
        border:false,

        title:'',

        autoLoad : false,

        dataUrl : '',

        params : {},

        renderTo : null,

        option:{

        },

        height: '100%'
    },

    beforeLoad : function(){

    },

    /**
     * 加载数据
     * @param params if(String)ajax 请求的url;if(Object)ajax请求发送的参数
     */
    loadData : function(params){
        var me = this;

        var dataUrl = me.dataUrl;

        if(Ext.isString(params)){
            dataUrl = params;
            params = {}
        }else if(Ext.isObject(params)) {
            params = Ext.apply({},params,me.params);
        }else {
            params = me.params;
        }

        me.beforeLoad(params);

        Ext.Ajax.request({
            url:dataUrl,
            params: params,
            success: function(response){
                var text = response.responseText;
                var op = Ext.JSON.decode(text);

                if(Ext.isObject(op)){
                    me.responseData(me,op.option || {},op.data || [],op);
                }else {
                    me.responseData(me,{},op);
                }
            }
        });

        //
    },
    refresh : function(params){
        this.loadData(params);
    },
    /**
     * ajax取回数据
     * @param me
     * @param option baidu echart原生配置
     * @param data 数据
     * @param responseData response取到所有数据包括title
     */
    responseData : function(me,option,data,responseData){
        me.echart.setOption(Ext.apply({},option,me.option));
    },

    renderTo : function(dom){
        var me = this;
        me.echart = echarts.init(dom);
        me._option = me.option;
        var op = Ext.apply({},me.config.option || {});
        op = me.applyEChartOption(op,me.option);
        me.option = op;

        this.initChart(me);

       // Ext.on('resize', me.echart.resize,dom);
       // window.resize = me.echart.resize;
        //me.loadData();

    },

    setOption : function(option){
        this.option = option;
        if(this.echart){
            this.echart.setOption(this.option,true);
        }
    },

    //初始化chart
    initChart : function(){

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
