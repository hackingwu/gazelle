Ext.define('Esm.portlet.PortletPanel', {

    extend: 'Ext.panel.Panel',
    alias: 'widget.portletPanel',

    requires: [
       // 'Ext.data.JsonStore'
    ],

    config : {
        border:false,

        title:'',

        autoLoad : false,

        dataUrl : '',

        params : {},

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
                   var title = op.title;
                   var height = op.height;

                   if(title){
                       me.ownerCt.setTitle(title);
                       delete op.title;
                   }
                   if(height){
                       me.ownerCt.setHeight(height);
                       delete op.height;
                   }
                   me.responseData(me,op.option || {},op.data || [],op);
                }else {
                    me.responseData(me,{},op.data);
                }
            }
        });

        //
    },
    refresh : function(params){
      this.loadData(params);
    },
    //ajax取回数据
    responseData : function(me,option,data){

    },

    //portlet框架加载完毕后，初始化body内容
    boxready : function(){

    },

    listeners :{
        boxready : {
            fn:function(me,wt,ht,e){
                me.boxready(me);

                //params

                me.loadData();
               // alert(option)

            }
        }
    }

});
