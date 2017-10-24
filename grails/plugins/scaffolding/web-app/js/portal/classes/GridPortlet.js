//var store = Ext.create('Ext.data.ArrayStore', {
//    fields: [
//        {name: 'name'},
//        {name: 'class'}
//    ],
//    params:{
//        id : 10,
//        p : 'dddd'
//    },
//    listeners: {
//
//        beforeload: function (proxy, response, operation) { //向服务端传递额外参数
//            Ext.apply(this.proxy.extraParams, {actioddd:'ddd',pe:'ll'});
//            // alert(Ext.JSON.decode(this.proxy.extraParams))
//        }
//    },
//
//    autoLoad:true,
//    proxy : {
//        type: 'ajax',
//        method: 'post',
//        url: 'portal/gridPortlet',
//        params:{
//            id : 10,
//            p : 'dddd'
//        },
//
//
//        reader: Ext.create('Ext.data.ArrayReader')
//    }
//    //data: this.myData
//});

Ext.define('Ext.app.GridPortlet', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.gridportlet',
    uses: [
        'Ext.data.ArrayStore'
    ],

    config : {
       height: 300,

       params : {

       },
       stripeRows: true,
       columnLines: true,
       columns: [{
            id       :'name',
            text   : '会员',
            //width: 120,
            flex: 1,
            sortable : true,
            dataIndex: 'name'
        },{
            text   : '门店',
            width    : 75,
            sortable : true,
            //renderer : this.change,
            dataIndex: 'class'
        }],
       store : {
           autoLoad: true,
           remoteSort: true,
           pageSize:15,
           fields: ['name', 'class'],
           listeners: {
                 beforeload: function (store, response, operation) { //向服务端传递额外参数
                     Ext.apply(store.proxy.extraParams, {ac:'mmmm',pe:'m中文ll'});
                        // alert(Ext.JSON.decode(this.proxy.extraParams))
                  }
            },
           proxy : {
               type: 'ajax',
               url: 'portal/gridPortlet',
               reader: {
                    type: 'json',
                    root: 'data',
                    successProperty: 'success',
                    messageProperty: 'message',
                    totalProperty: 'totalCount' //数据总条数
                }
           }
       }

    }
});
