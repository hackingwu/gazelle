Ext.define('Esm.portlet.GridPortlet', {
    extend: 'Esm.portlet.PortletPanel',
    alias: 'widget.gridportlet',
    requires : [
        'Ext.data.ArrayStore'
    ],
    border:false,
    config : {
       height: 300,

       params : {

       },
       stripeRows: true,
       columnLines: true,
       columns: []
    },

    //ajax取回数据
    responseData : function(me,option,data){
        data = data || [];

       var store = me.gridPanel.getStore();

        store.removeAll();
        store.loadData(data)
    },

    //portlet框架加载完毕后，初始化body内容
    boxready : function(me){
        me._option = me.option;
        var op = Ext.apply({},me.option || {},me.config || {});

        me.option = op;
        delete me.option.params;

        var columns = this.option.columns || [];
        var fields = [];
        Ext.each(columns,function(value,index){
            fields.push(value.dataIndex);
        });

        var store = Ext.create('Ext.data.ArrayStore', {
            fields: fields,
            data: []
        });

        op.store = store;
        op.height = me.body.getHeight();

        //Ext.grid.Panel gridpanel
        me.gridPanel = Ext.create('Ext.grid.Panel',op);

        me.add(me.gridPanel);

    }
});
