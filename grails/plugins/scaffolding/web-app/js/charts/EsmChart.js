Ext.define('Esm.echart.EsmChart', {

    extend: 'Ext.Viewport',

    alias: 'widget.esmchart',

    requires: [
        // 'Ext.data.JsonStore'
    ],

    layout: 'border',

    config : {

        title:'',

        dataUrl : '',

        params : {},

        //line,pie,bar,map,mapType
        chartType:'line',

        option:{},

        conditions :[],

        height: '100%'
    },

    loadData : function(params){
        var me = this;

        var dataUrl = me.dataUrl;

        if(Ext.isString(params)){
            dataUrl = params;
        }else if(Ext.isObject(params)) {
            if(params.dataUrl){
                dataUrl = params.dataUrl
                delete params.dataUrl
            }
            params = Ext.apply({},params,me.params);
        }else {
            params = me.params;
        }

      //  me.beforeLoad(params);

        Ext.Ajax.request({
            url:dataUrl,
            params: params,
            success: function(response){
                var text = response.responseText;
                var op = Ext.JSON.decode(text);

                if(Ext.isObject(op)){
                    me.echart.responseData(me.echart,op.option || {},op.data || [],op);
                    me.gridLoadData(op.data || [],op.option);
                }else {
                    me.echart.responseData(me.echart,{},op);
                    me.gridLoadData(op || [],op.option);
                }
            }
        });
        //

    },
    refresh : function(params){
        this.loadData(params);
    },

    listeners :{
        boxready : {
            fn:function(me,wt,ht,e){
                var items = [];
                if(me.chartType == 'bar'){
                    me.echart = Ext.create('Esm.echart.Bar');
                }else if(me.chartType == 'pie'){
                    me.echart = Ext.create('Esm.echart.Pie');
                }else if(me.chartType == 'map'){

                }else{
                    me.echart = Ext.create('Esm.echart.Line');
                }
                me.echart.option = me.option;
               // alert(Ext.JSON.encode(me.echart.getOption()));
             //  alert(Ext.JSON.encode(me.echart.option));

                items.push({region: 'north',
                    border:false,
                    items:me.createToolbar()});

                items.push({
                    region: 'east',
                    collapsible: true,
                    collapsed : true,
                    title: '详细记录',
                    split: true,
                    width: '20%',
                    items: me.createGrid(),
                    listeners :{
                        boxready : function(comp){


                            me.gridPanel.setHeight(comp.body.getHeight());
                           // me.gridPanel.setWidth(comp.body.getWidth());

                        }
                    }
                });

                items.push({
                    region: 'center',
                    margins: '2 5 5 0',
                    activeItem: 0,
                    border: false,
                    html:'center',
                    listeners :{
                        boxready : function(comp){
                            me.echart.renderTo(comp.body.dom);
                            comp.on('resize',me.echart.echart.resize,comp);

                        }
                    }
                });

                me.add(items);

                me.loadData();

            }
        }
    },

    createToolbar : function(){

        var me = this;

        var refreshData = function(){
            var params = {};
            var name ,v;

            Ext.each(toolbar.query('textfield'),function(value,index){
                name = value.getName();
                v = value.getValue();
                if(Ext.isDate(v)){
                    v = Ext.Date.format(v,'Y-m-d H:i:s');
                }
                params[name] = v;
            });

            var gangCombo = toolbar.query("combo");
            for(var i=0;i<gangCombo.length;i++){
                var value = gangCombo[i].getValue();
                value = (value=='tbar_gang_all'?'':value);
                if(gangCombo[i].getItemId().indexOf("tbar_gang_")!=-1){
                    params[gangCombo[i].getName()] = (value?value:"")
                }
            }

            me.refresh(params);
        }
        var listeners = {
            change : {
                fn : function(){
                    refreshData();
                }
            }
        }



        var items = [
//            {
//                text: '导出',
//                iconCls: 'icon-add'
//
//            },

            '->'
        ];

        if(me.option.apptime){
        items.push(
            {
                xtype: 'combo',
                name: 'appCode',
                itemId: 'tbar_gang_select_app_code',
                fieldLabel: '请选择项目',
                labelAlign: 'right',
                displayField: 'name',
                valueField: 'value',
                emptyText: '所有项目',
                editable: false,
                store: Ext.create('Ext.data.Store', {
                    autoLoad: false,
                    fields: ['name', 'value'],
                    proxy: {
                        type: 'ajax',
                        url: '/appVersion/appCombo'
                    },
                    listeners: {
                        load: function(_self,records,success){
                            if(success){
                                _self.insert(0,{name:'所有项目',value:'tbar_gang_all'});
                                Ext.ComponentQuery.query("#tbar_gang_select_app_code")[0].setValue(this.getAt(0).data.value);
                            }
                        }
                    }
                }),
                listeners: {
                    change: function(me, newVal, oldVal, opts){
                        var nextCombo = Ext.ComponentQuery.query("#tbar_gang_select_app_version")[0];
                        nextCombo.getStore().reload();
                        if(nextCombo.getValue() == 'tbar_gang_all'){
                            refreshData();
                        }
                    }
                }
            }
        );




        items.push(
            {
                xtype: 'combo',
                name: 'appVersion',
                itemId: 'tbar_gang_select_app_version',
                fieldLabel: '请选择版本',
                labelAlign: 'right',
                displayField: 'name',
                valueField: 'value',
                emptyText: '所有版本',
                editable: false,
                store: Ext.create('Ext.data.Store', {
                    autoLoad: false,
                    fields: ['name', 'value'],
                    proxy: {
                        type: 'ajax',
                        url: '/crush-log-analyser/appVersion/appVersionCombo'
                    },
                    listeners: {
                        beforeload: function (proxy, response, operation) {
                            var preCombo = Ext.ComponentQuery.query("#tbar_gang_select_app_code")[0];
                            var value = preCombo.getValue();
                            Ext.apply(this.proxy.extraParams, {appCode:(value=='tbar_gang_all'?'':value)});
                        },

                        load: function(_self,records,success){
                            if(success){
                                _self.insert(0,{name:'所有版本',value:'tbar_gang_all'});
                                Ext.ComponentQuery.query("#tbar_gang_select_app_version")[0].setValue(this.getAt(0).data.value);
                            }
                        }
                    }
                }),
                listeners: {
                    change: function(me, newVal, oldVal, opts){
                        refreshData();
                    }
                }
            }
        );
        }
        items.push(
        {
            xtype: 'datefield',
                id: 'sdate',
            name : 'startTime',
            fieldLabel: '开始时间',
            labelAlign: 'right',
            emptyText: '请选择日期',
            format: 'Y-m-d',
            value:Ext.Date.add(new Date(), Ext.Date.DAY, -7),
            editable:false,
            listeners: {
                'select': function () {
                    var start = Ext.getCmp('sdate').getValue();
                    var endDate = Ext.getCmp('edate').getValue();
                    if (start > endDate) {
                        Ext.getCmp('sdate').setValue(endDate);
                    }
                }
            }
        },
        {
            xtype: 'datefield',
                id: 'edate',
            name:'endTime',
            fieldLabel: '结束时间',
            labelAlign: 'right',
            emptyText: '请选择日期',
            format: 'Y-m-d',
            value:Ext.Date.add(new Date(), Ext.Date.DAY, 0),
            editable:false,
            listeners: {
                select: function () {
                    var start = Ext.getCmp('sdate').getValue();
                    var endDate = Ext.getCmp('edate').getValue();
                    if (start > endDate) {
                        Ext.getCmp('sdate').setValue(endDate);
                    }
                }
            }
        }
        );

       Ext.each(me.conditions,function(value,index){
           var f = me.createFormField(value);
           f.listeners = listeners;
           items.push(f);
        });

        items.push({
            itemId: 'search',
            text: '查询',
            iconCls: 'icon-search',
            listeners : {
                click: {
                    fn : function(){
                        refreshData();
                    }
                }
            }

        });

        items.push( {
            itemId: 'resetBtn',
            text: '重置',
            handler: function () {
                var gangCombo = toolbar.query("combo");
                for(var i=0;i<gangCombo.length;i++){
                    if(gangCombo[i].getItemId().indexOf("tbar_gang_")!=-1){
                        gangCombo[i].setValue("");
                    }
                }

                var datefields = toolbar.query("datefield");
                datefields[0].setValue(Ext.Date.add(new Date(), Ext.Date.DAY, -7));
                datefields[1].setValue(Ext.Date.add(new Date(), Ext.Date.DAY, 0));
                refreshData();
            }
        });

        var toolbar = Ext.create('Ext.Toolbar', {
            minWidth: 800,
            items: items
        });



        return toolbar;

    },
    createLineChart : function(){

    },
    createGrid : function(){
        var columns,fields,store;
        if(this.chartType == 'bar' || this.chartType == 'line'){

            columns = [{text:'类目',flex:1,dataIndex:'name'},{text:'值',flex:1,dataIndex:'value'}];
            fields = [];

            Ext.each(columns,function(value,index){
                fields.push(value.dataIndex);
            });

            store = Ext.create('Ext.data.ArrayStore', {
                fields: fields,
                data: []
            });

        }else if(this.chartType == 'pie'){
            columns = [{text:'类目',flex:1,dataIndex:'name'},{text:'值',flex:1,dataIndex:'value'}];
            fields = ['name','value'];
            store = Ext.create('Ext.data.JsonStore', {
                fields: fields,
                data: []
            });
        }else if(this.chartType == 'map'){

        }else {
//            columns = [ { text: '类目',  dataIndex: 'name' }];
//            fields = [];
//
//            this.xAxis = this.option.x || [];
//
//            Ext.each(this.xAxis ,function(value,index){
//                columns.push({text:value,dataIndex:'col' + index});
//            });
//
//            Ext.each(columns,function(value,index){
//                fields.push(value.dataIndex);
//            });
//
//            store = Ext.create('Ext.data.ArrayStore', {
//                fields: fields,
//                data: []
//            });
            columns = [ { text: '日期',  dataIndex: 'name' },{text: '日志数',flex: 1, dataIndex: 'count'}];
            fields = [];

            Ext.each(columns,function(value,index){
                fields.push(value.dataIndex);
            });

            store = Ext.create('Ext.data.ArrayStore', {
                fields: fields,
                data: []
            });
        }
        var grid = Ext.create('Ext.grid.GridPanel',{

            columns: columns,
            store : store,
            height: 400
           // width: 400
        });

        this.gridPanel = grid;
        return grid;
    },

    gridLoadData : function(data,option){
        var store = this.gridPanel.getStore();

        store.removeAll();

        if(this.chartType == 'bar' || this.chartType == 'line'){
            var tmp =  [],item;
            var d = option.series[0].data
            Ext.each(option.xAxis[0].data ,function(value,index){
                item = [];
                item.push(value,d[index]);

//                Ext.each(data,function(v,i){
//                    if(Ext.isArray(v) && index < v.length ){
//                        item.push(data[index]);
//                    }
//                });

                tmp.push(item);
            });
            data = tmp;

        }else if(this.chartType == 'pie'){


        }else if(this.chartType == 'map'){

        }else {
//            Ext.each(this.option.lines ,function(value,index){
//                if(Ext.isArray(data[index])){
//                    data[index].unshift(value);
//                }
//            });
            var tmp =  [],item;
            var d = option.series[0].data
            Ext.each(option.xAxis[0].data ,function(value,index){
                item = [];
                item.push(value);
                if(Ext.isArray(d) && index < d.length ){
                    item.push(d[index]);
                }
                tmp.push(item);
            });
            data = tmp;


        }

        store.loadData(data)
    },

    createFormField : function(configs){
        //createByAlias( alias, args ) : Object
        configs = configs || {};

        var field ;

        if(configs.xtype){
            field = configs;
        }else {
            field = Ext.apply(configs,this.mappingXtype(configs.type));
        }
        return field;

    },
    mappingXtype : function(type){
        var f = {'int':{xtype: 'numberfield'},time:{xtype:'datefield',format:'Y-m-d'},string:{xtype:'textfield'}}[type];
        return f || {xtype:'textfield'};
    }

});
