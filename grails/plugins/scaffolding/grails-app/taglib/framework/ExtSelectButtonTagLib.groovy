package framework

import java.util.regex.Pattern

/**
 * 生成ToolBar上的单选、多选按钮的方法
 * @className ExtSelectButtonTagLib
 * @author Su sunbin
 * @version 2014-09-16 ExtTagLib中的生成ToolBar上的单选、多选按钮的方法拆分至此
 */
class ExtSelectButtonTagLib extends ExtTagLib{
//	static defaultEncodeAs = 'html'
	//static encodeAsForTags = [tagName: 'raw']

	/**
	 * 在toolbar上生成复杂单选按钮
	 * @param attrs.included 显示在单选列表中的domain class属性
	 * @param attrs.excluded 不显示在单选列表中的domain class属性
	 * @param attrs.text 单选按钮标题
	 * @param attrs.model 关联的domain class名称
	 * @param attrs.controller commit的服务端控制器
	 * @param attrs.action commit的服务端action
	 * @return 单选按钮功能视图
	 */
	def extSingleSelectButton={ attrs, body ->
		List included = attrs.included
		List excluded = attrs.excluded
		String text = attrs.text?:"${g.message(code:"toolbar.singlesel.lang")}"
		String association = attrs.association?:""
		String model=attrs.model
		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName

		String controller = attrs.controller ?: propertyName
		String action= attrs.action ?: "singleSelCommitAction"  //数据提交到服务器的路径

		List<Map> fields=[]
		//included与excluded是互斥的，定义了included就会忽略excluded
		if(included){
			for(int i=0; i<domain.fields.size();i++)
			{
                for(int j=0;j<included.size();j++){
                    Pattern pattern = Pattern.compile(included[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        fields << domain.transFields[i]
                    }
                }
			}

		}else if(excluded){
			for(int i=0; i<domain.fields.size();i++)
			{
                boolean flag = false //是否匹配正则
                for(int j=0;j<excluded.size();j++){
                    Pattern pattern = Pattern.compile(excluded[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        flag = true
                    }
                }
                if(!flag){
                    fields << domain.transFields[i]
                }
			}
		}else{
			fields=domain.fields
		}

		if(fields.size() > 3){
			fields=fields[0..3]
		}

		String columnsString = ""
		for(int i=0; i<fields.size();i++)
		{
			Map constraint = fields[i].constraint

			String func = ""
			if(fields[i].type=='boolean'){ //控制boolean的grid显示内容
				func = ",renderer:function(value){\r\nreturn (value?'是':'否');\r\n}"
			}else if(constraint.inList && constraint.inListLabel){  //控制带缩写的列表在grid列里的显示内容
				String listVal = "{";
				for(int k=0;k<constraint.inList.size();k++){
					listVal += """ '${constraint.inList[k]}':'${constraint.inListLabel[k]}'${k<(constraint.inList.size()-1) ?",":"}"}"""
				}
				func = """,renderer:function(value){\r\nvar listArr=${listVal};\r\nreturn listArr[value];\r\n}"""
			}else if(constraint.relation && fields[i].name=='parentId'){
				func = ",renderer:function(value){\r\nreturn (value==''?'无':value);\r\n}"
			}

			if(constraint.relation && constraint.domain){
				columnsString=columnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, sortable: true, dataIndex: '${fields[i].name}Value'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
			}else{
				columnsString=columnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, sortable: true, dataIndex: '${fields[i].name}'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
			}
		}

		String fieldsString = ""
		for(int i=0; i<fields.size();i++)
		{
			Map constraint=fields[i].constraint
			if(constraint.relation && constraint.domain){
				fieldsString=fieldsString+"""'${fields[i].name}Value'${i<(fields.size()-1) ?",":""}"""
			}else{
				fieldsString=fieldsString+"""'${fields[i].name}'${i<(fields.size()-1) ?",":""}"""
			}
		}

		String output ="""
            {
                xtype: 'button',
                itemId: '${propertyName}SingleSelBtn',
                text: '${text}',
                iconCls: 'icon-single-select',
                handler: function () {
                    var selection = ${controllerName}Grid.getView().getSelectionModel().getSelection();
                    if(selection.length>1){
                        Ext.MessageBox.show({
                        title: '提示',
                        msg: '最多只能选择一条记录！',
                        buttons: Ext.MessageBox.OK
                        });
                        return;
                    }

                    Ext.create('Ext.Window', {
                        title: '请选择',
                        width: 550,
                        modal: true,
                        resizable: false,
                        buttonAlign: 'center',
                        items: [
                            {
                                xtype: 'grid',
                                id:'${propertyName}SingleSelGrid',
                                height:parseInt(document.documentElement.clientHeight*0.7),
                                autoWidth: true,
                                border: false,
                                selModel: Ext.create('Ext.selection.CheckboxModel',{mode:'SINGLE'}),
                                columns: [
                                    ${columnsString}
                                ],
                                store: Ext.create('Ext.data.Store', {
                                    storeId: '${propertyName}SingleSelStore',
                                    autoLoad: true,
                                    remoteSort: true,
                                    pageSize:15,
                                    fields: [${fieldsString}],
                                    proxy:{
                                        type: 'ajax',
                                        url: '${g.createLink([controller: propertyName, action: "list"])}',
                                        reader: {
                                            type: 'json',
                                            root: 'data',
                                            successProperty: 'success',
                                            messageProperty: 'message',
                                            totalProperty: 'totalCount' //数据总条数
                                        },
                                        listeners: {
                                            exception: function (proxy, response, operation) {
                                                Ext.wintip.error('错误!',operation.getError());
                                            }
                                        }
                                    },
                                    listeners: {
                                        beforeload: function (proxy, response, operation) {
                                            var searchValue = Ext.getCmp('${propertyName}SingleSelSearch').getValue();
                                            var query_params={
                                                search:  '[{"key":"${fields[0].name}","value":"'+searchValue+'"}]'
                                            };
                                            Ext.apply(this.proxy.extraParams, query_params);
                                        }
                                    }
                                }),
                                dockedItems: [{
                                    xtype: 'toolbar',
                                    dock: 'top',
                                    items:[
                                        {
                                            id:'${propertyName}SingleSelSearch',
                                            //itemId:'multiUnselSearchVal',
                                            width:450,
                                            xtype: 'textfield',
                                            emptyText:'请输入${fields[0].cn}进行搜索',
                                            listeners: {
                                                 change: function(){
                                                    Ext.getCmp('${propertyName}SingleSelGrid').getStore().loadPage(1);
                                                 }
                                            }
                                        },{
                                            xtype: 'button',
                                            //hideLabel: true,
                                            margin:'0 0 0 5',
                                            text: '查询',
                                            iconCls: 'icon-search',
                                            handler: function(){
                                                Ext.getCmp('${propertyName}SingleSelGrid').getStore().loadPage(1);
                                            }
                                        }
                                    ]
                                }],
                                bbar: {
                                    xtype: 'pagingtoolbar',
                                    prevText:'',
                                    nextText:'',
                                    firstText:'',
                                    lastText:'',
                                    refreshText:'',
                                    store: '${propertyName}SingleSelStore',
                                    displayInfo: true,
                                    displayMsg: '显示 {0} - {1} 条，共 {2} 条',
                                    emptyMsg: "没有数据"
                                },
                                listeners: {
                                    itemdblclick: function(dataview, record, item, index, e){
                                        var selData = Ext.getCmp('${propertyName}SingleSelGrid').getSelectionModel().getSelection();
                                        Ext.Ajax.request({
                                            async : false,
                                            method: 'GET',
                                            url: '${g.createLink([controller: controller, action: action])}',
                                            params: {
                                                id: selData[0].data.id
                                            },
                                            success: function(response){
                                                var response = Ext.JSON.decode(response.responseText);
                                                if(response.success){
                                                    Ext.wintip.msg('操作成功!', response.message);
                                                }else{
                                                    Ext.wintip.error('错误!', response.message);
                                                }
                                            }
                                        });

                                        this.up('window').close();
                                    }
                                }
                            }
                        ],
                        buttons: [
                          {
                            text: '确定',
                            handler: function(){
                                var selData = Ext.getCmp('${propertyName}SingleSelGrid').getSelectionModel().getSelection();
                                if(selData.length==0){
                                    Ext.MessageBox.show({
                                        title: '提示',
                                        msg: '请先选择1个项目！',
                                        buttons: Ext.MessageBox.OK
                                    });
                                    return;
                                }

                                Ext.Ajax.request({
                                    async : false,
                                    method: 'GET',
                                    url: '${g.createLink([controller: controller, action: action])}',
                                    params: {
                                        id: selData[0].data.id
                                    },
                                    success: function(response){
                                        var response = Ext.JSON.decode(response.responseText); //数据返回格式:{success: true, message: "数据已提交服务器!"}
                                        if(response.success){
                                            Ext.wintip.msg('操作成功!', response.message);
                                        }else{
                                            Ext.wintip.error('错误!', response.message);
                                        }
                                    }
                                });
                                this.up('window').close();
                            }
                          },{
                            text: '取消',handler: function(){ this.up('window').close();}
                          }
                        ]
                    }).show();
                }
            },
        """
		out << output
	}

	/**
	 * 在toolbar上生成复杂多选按钮
	 * @param attrs.included 显示在单选列表中的domain class属性
	 * @param attrs.excluded 不显示在单选列表中的domain class属性
	 * @param attrs.text 多选按钮标题
	 * @param attrs.model 关联的domain class名称
	 * @param attrs.controller commit的服务端控制器
	 * @param attrs.action commit的服务端action
	 * @param attrs.maxSel 可选择的最大记录数
	 * @param attrs.searchKeyIdx 作为搜索字段的索引值
	 * @return 多选按钮功能视图
	 */
	def extMultiSelectButton={ attrs, body ->
		List included = attrs.included
		List excluded = attrs.excluded
		String text = attrs.text?:"${g.message(code:"toolbar.multisel.lang")}"
		String extra = attrs.extra?:""

		String model=attrs.model
		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName

		String controller = attrs.controller ?: propertyName
		String action = attrs.action ?: "multiSelCommitAction" //数据提交到服务器的路径
		String maxSel = attrs.maxSel?:"13"
		int searchKeyIdx = (attrs.searchKeyIdx?:"0").toInteger()

		List<Map> fields=[]
		//included与excluded是互斥的，定义了included就会忽略excluded
		if(included){
			for(int i=0; i<domain.fields.size();i++)
			{
                for(int j=0;j<included.size();j++){
                    Pattern pattern = Pattern.compile(included[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        fields << domain.transFields[i]
                    }
                }
			}
		}else if(excluded){
			for(int i=0; i<domain.fields.size();i++)
			{
                boolean flag = false //是否匹配正则
                for(int j=0;j<excluded.size();j++){
                    Pattern pattern = Pattern.compile(excluded[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        flag = true
                    }
                }
                if(!flag){
                    fields << domain.transFields[i]
                }
			}
		}else{
			fields=domain.fields
		}

		if(fields.size() > 3){
			fields=fields[0..3]
		}

		String unSelGridColumnsString = ""
		String selGridColumnsString = ""
		for(int i=0; i<fields.size();i++)
		{
			Map constraint = fields[i].constraint
			String func = ""
			if(fields[i].type=='boolean'){ //控制boolean的grid显示内容
				func = ",renderer:function(value){\r\nreturn (value?'是':'否');\r\n}"
			}else if(constraint.inList && constraint.inListLabel){  //控制带缩写的列表在grid列里的显示内容
				String listVal = "{";
				for(int k=0;k<constraint.inList.size();k++){
					listVal += """ '${constraint.inList[k]}':'${constraint.inListLabel[k]}'${k<(constraint.inList.size()-1) ?",":"}"}"""
				}
				func = """,renderer:function(value){\r\nvar listArr=${listVal};\r\nreturn listArr[value];\r\n}"""
			}else if(constraint.relation && fields[i].name=='parentId'){
				func = ",renderer:function(value){\r\nreturn (value==''?'无':value);\r\n}"
			}

			if(constraint.relation && constraint.domain){
				unSelGridColumnsString=unSelGridColumnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, sortable: true, dataIndex: '${fields[i].name}Value'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
			}else{
				unSelGridColumnsString=unSelGridColumnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, sortable: true, dataIndex: '${fields[i].name}'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
			}

			if(i==1){ //右边表格显示前两个字段
				selGridColumnsString = unSelGridColumnsString.replaceAll(~/},$/,"}")
			}
		}

		String unSelGridFieldsString = ""
		String selGridFieldsString = ""
		for(int i=0; i<fields.size();i++)
		{
			Map constraint=fields[i].constraint
			if(constraint.relation && constraint.domain){
				unSelGridFieldsString=unSelGridFieldsString+"""'${fields[i].name}Value'${i<(fields.size()-1) ?",":""}"""
			}else{
				unSelGridFieldsString=unSelGridFieldsString+"""'${fields[i].name}'${i<(fields.size()-1) ?",":""}"""
			}

			if(i==1){ //右边表格显示前两个字段
				selGridFieldsString = unSelGridFieldsString.replaceAll(~/',$/,"'")
			}
		}

		String output ="""
            {
                xtype: 'button',
                itemId: '${propertyName}MultiSelBtn',
                text: '${text}',
                iconCls: 'icon-multi-select',
                handler: function () {


                    Ext.create('Ext.Window', {
                        title: '请选择',
                        width: 750,
                        modal: true,
                        resizable: false,
                        buttonAlign: 'center',
                        layout: {
                            type: 'table',
                            columns: 3
                        },
                        items: [
                            {
                                colspan:3,
                                xtype: 'toolbar',
                                border:false,
                                items: [{
                                    xtype: 'textfield',
                                    id:'${propertyName}UnSelSearch',
                                    width:640,
                                    emptyText:'请输入${fields[searchKeyIdx].cn}进行搜索',
                                    listeners: {
                                         change: function(){
                                            Ext.getCmp('${propertyName}UnSelGrid').getStore().loadPage(1);
                                         }
                                    }
                                },{
                                    xtype: 'button',
                                    margin:'0 0 0 5',
                                    text: '查询',
                                    iconCls: 'icon-search',
                                    handler: function(){
                                        Ext.getCmp('${propertyName}UnSelGrid').getStore().loadPage(1);
                                    }
                                }]
                            },
                            {
                                xtype:'panel',
                                width:395,
                                items: [
                                    { //左边表格
                                        xtype: 'grid',
                                        id:'${propertyName}UnSelGrid',
                                        title:'可选项目',
                                        flex:4,
                                        height:parseInt(document.documentElement.clientHeight*0.7),
                                        autoWidth: true,
                                        border: false,
                                        selModel: Ext.create('Ext.selection.CheckboxModel'),
                                        columns: [
                                            ${unSelGridColumnsString}
                                        ],
                                        store: Ext.create('Ext.data.Store', {
                                            storeId: '${propertyName}UnSelStore',
                                            autoLoad: true,
                                            remoteSort: true,
                                            pageSize:13,
                                            fields: [${unSelGridFieldsString}],
                                            proxy:{
                                                type: 'ajax',
                                                url: '${g.createLink([controller: propertyName, action: "list"])}',
                                                reader: {
                                                    type: 'json',
                                                    root: 'data',
                                                    successProperty: 'success',
                                                    messageProperty: 'message',
                                                    totalProperty: 'totalCount' //数据总条数
                                                },
                                                listeners: {
                                                    exception: function (proxy, response, operation) { //{"success":false,"message":"Failed to Destroy user","data":[]} @success为false时执行此回调函数
                                                        Ext.wintip.error('错误!',operation.getError());
                                                    }
                                                }
                                            },
                                            listeners: {
                                                beforeload: function (proxy, response, operation) { //向服务端传递额外参数
                                                    var searchValue = Ext.getCmp('${propertyName}UnSelSearch').getValue();
                                                    var query_params={
                                                        search:  '[{"key":"${fields[searchKeyIdx].name}","value":"'+searchValue+'"}]'
                                                    };
                                                    Ext.apply(this.proxy.extraParams, query_params);
                                                },
                                                load: function(_self,records,success){
                                                    if(success){
                                                        var unSelGridSelModel = Ext.getCmp('${propertyName}UnSelGrid').getSelectionModel();
                                                        var selGridStore = Ext.getCmp('${propertyName}SelGrid').getStore();
                                                        unSelGridSelModel.deselectAll();
                                                        Ext.each(records, function(unSelRecord) {
                                                            var row = unSelRecord.index-(_self.pageSize*(_self.currentPage-1));
                                                            selGridStore.each(function(selRecord){
                                                                if(unSelRecord.data.id == selRecord.data.id){
                                                                    unSelGridSelModel.select(row, true);
                                                                    return false; //跳出第二个each循环
                                                                }
                                                            });
                                                        });
                                                        Ext.getCmp('${propertyName}UnSelSearch').focus();
                                                    }
                                                }
                                            }
                                        }),
                                        bbar: {
                                            xtype: 'pagingtoolbar',
                                            prevText:'',
                                            nextText:'',
                                            firstText:'',
                                            lastText:'',
                                            refreshText:'',
                                            store: '${propertyName}UnSelStore',
                                            emptyMsg: "没有数据"
                                        }
                                    }
                                ]
                            },{
                                border:false,
                                width:35,
                                layout: {
                                    type: 'vbox',
                                    align: 'center'
                                },
                                items:[
                                    {
                                        xtype: 'button',
                                        iconCls: 'icon-right',
                                        margin:'0 5 10 5',
                                        handler: function(){
                                            var selData=[];
                                            var unSelStore = Ext.getCmp('${propertyName}UnSelGrid').getStore();
                                            if(unSelStore.data.length==0){
                                                return;
                                            }
                                            unSelStore.each(function(record){
                                                selData.push(record);
                                                if(selData.length==${maxSel}){
                                                    return false;
                                                }
                                            });
                                            Ext.getCmp('${propertyName}SelGrid').getStore().loadData(selData,true);
                                            unSelStore.reload();
                                        }
                                    },
                                    {
                                        xtype: 'button',
                                        iconCls: 'icon-right2',
                                        margin:'0 5 10 5',
                                        handler: function(){
                                            var selStore = Ext.getCmp('${propertyName}SelGrid').getStore();
                                            if(selStore.getCount() == ${maxSel}){
                                                Ext.MessageBox.show({
                                                    title: '提示',
                                                    msg: '最多只能选择${maxSel}个项目！',
                                                    buttons: Ext.MessageBox.OK
                                                });
                                                return;
                                            }

                                            var selData = Ext.getCmp('${propertyName}UnSelGrid').getSelectionModel().getSelection();
                                            if(selData.length==0){
                                                return;
                                            }
                                            selStore.loadData(selData,true);
                                            Ext.getCmp('${propertyName}UnSelGrid').getStore().reload();
                                        }
                                    },{
                                        xtype: 'button',
                                        iconCls: 'icon-left2',
                                        margin:'0 5 10 5',
                                        handler: function(){
                                            var multiSelGrid = Ext.getCmp('${propertyName}SelGrid');
                                            var selData = multiSelGrid.getSelectionModel().getSelection();
                                            if(selData.length==0){
                                                return;
                                            }
                                            multiSelGrid.getStore().remove(selData);
                                            Ext.getCmp('${propertyName}UnSelGrid').getStore().reload();
                                        }
                                    },{
                                        xtype: 'button',
                                        iconCls: 'icon-left',
                                        margin:'0 5 0 5',
                                        handler: function(){
                                            var selStore = Ext.getCmp('${propertyName}SelGrid').getStore();
                                            if(selStore.data.length==0){
                                                return;
                                            }

                                            selStore.removeAll();
                                            Ext.getCmp('${propertyName}UnSelGrid').getStore().reload();
                                        }
                                    }
                                ]
                            },{
                                xtype:'panel',
                                width:310,
                                items:[
                                    { //右边表格
                                        xtype: 'grid',
                                        id: '${propertyName}SelGrid',
                                        title:'已选项目',
                                        height:parseInt(document.documentElement.clientHeight*0.7),
                                        autoWidth: true,
                                        selModel: Ext.create('Ext.selection.CheckboxModel'),
                                        columns: [
                                            ${selGridColumnsString}
                                        ],
                                        store: Ext.create('Ext.data.Store', {
                                            fields: [${selGridFieldsString}]
                                        })
                                    }
                                ]
                            }
                        ],
                        buttons: [
                          {
                            text: '确定',
                            handler: function(){
                                var selStore = Ext.getCmp('${propertyName}SelGrid').getStore();
                                if(selStore.data.length==0){
                                    Ext.MessageBox.show({
                                        title: '提示',
                                        msg: '请至少选择1个项目！',
                                        buttons: Ext.MessageBox.OK
                                    });
                                    return;
                                }

                                var postValue=[];
                                selStore.each(function(record){
                                    postValue.push(record.data.id);
                                });

                                Ext.Ajax.request({
                                    async : false,
                                    method: 'GET',
                                    url: '${g.createLink([controller: controller, action: action])}',
                                    params: {
                                        id: "["+postValue.join()+"]"
                                    },
                                    success: function(response){
                                        var response = Ext.JSON.decode(response.responseText);
                                        if(response.success){
                                            ${controllerName}${extra}Store.loadPage(1);
                                            Ext.wintip.msg('操作成功!', response.message);
                                        }else{
                                            Ext.wintip.error('错误!', response.message);
                                        }
                                    }
                                });

                                this.up('window').close();
                            }
                          },{
                            text: '取消',handler: function(){ this.up('window').close();}
                          }
                        ]
                    }).show();

                    //获取已选数据
                    Ext.Ajax.request({
                        method: 'GET',
                        url: '${g.createLink([controller: "focusInfo", action: "list"])}',
                        success: function(response){
                            var selData = Ext.JSON.decode(response.responseText);
                            Ext.getCmp('${propertyName}SelGrid').getStore().loadData(selData,true);
                        }
                    });
                }
            },
        """
		out << output
	}

	/**
	 * 在toolbar上生成grid列的单/多选操作按钮
	 * @param attrs.extra 附件变量字段，通常无需配置
	 * @param attrs.config.gridName 关联的Grid名称
	 * @param attrs.model 关联的domain class名称
	 * @param attrs.controller commit的服务端控制器
	 * @param attrs.action commit的服务端action
	 * @param attrs.mode 值为multi表示多选，否则表示单选
	 * @return 单/多选操作功能按钮
	 */
	def extRowSelectButton={ attrs, body ->

		String extra = attrs.extra?:""
		String propertyName=ModelService.GetModel(attrs.model).propertyName
		String gridName=(attrs.config?.gridName)?:"${propertyName}${extra}Grid"
		String controller = attrs.controller ?: controllerName
		String action = attrs.action ?: "rowSelAction"
		String mode = attrs.mode ?: "multi"
        String text = attrs.text?:"选行"

		String output = """
            {
                itemId: '${mode}RowSel',
                text: '${text}',
                iconCls: '${mode=='multi'?'icon-multi-select':'icon-single-select'}',
                handler: function () {
                    var data = ${gridName}.getView().getSelectionModel().getSelection();
                    if(data.length==0){
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: '请选择项目！',
                            buttons: Ext.MessageBox.OK
                        });

                        return;
                    }

                    ${mode=='multi'?"":"" +
				"                    if(data.length>1){\n" +
				"                        Ext.MessageBox.show({\n" +
				"                            title: '提示',\n" +
				"                            msg: '只能选择一个项目！',\n" +
				"                            buttons: Ext.MessageBox.OK\n" +
				"                        });\n" +
				"\n" +
				"                        return;\n" +
				"                    }"}

                    var ids = [];
                    Ext.Array.each(data, function(record) {
                        ids.push(record.get('id'));
                    });

                    Ext.Ajax.request({
                        url: '${g.createLink([controller: controller, action: action])}',
                        method : 'post',
                        params: {data:ids.join()},
                        success: function(response) {
                            var result = Ext.decode(response.responseText);
                            if(result.success){
                                ${gridName}.getStore().reload();
                                Ext.wintip.msg('操作成功!',  result.message);
                            }else{
                                Ext.wintip.error('操作失败!',  result.message);
                            }
                        },
                        failure: function() {
                            Ext.wintip.error('请求超时!',  '网络超时!');
                        }
                    });
                }
            },"""

		out << output
	}

	/**
	 * 在toolbar上生成联动的combo列表
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.extra 附加变量标识
	 * @param attrs.config.gridName combo关联的Grid名称
	 * @param attrs.combo combo属性列表，可支持多级combo联动，详见示例代码
	 * @return 联动combo视图
	 */
	def extGangCombo = { attrs, body ->
		String model=attrs.model
		String extra = attrs.extra?:""

		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName
		String gridName=(attrs.config?.gridName)?:"${propertyName}${extra}Grid"

		List<Map> combo = attrs.combo
		String output = ""

		for(int k=0;k<combo.size();k++){
			String changeFun
			String beforeload = ""
			if(k<combo.size()-1){
				changeFun = """
                    var nextCombo = Ext.ComponentQuery.query("#tbar_gang_${combo[k+1].name}")[0];
//                    var value = this.getValue();
                    nextCombo.getStore().reload();
                    if(nextCombo.getValue() == 'tbar_gang_all'){
                        ${gridName}.getStore().reload();
                    }
                """
			}else{
				changeFun = """${gridName}.getStore().reload();"""
			}

			if(k!=0){
				beforeload = """
                        beforeload: function (proxy, response, operation) {
                            var preCombo = Ext.ComponentQuery.query("#tbar_gang_${combo[k-1].name}")[0];
                            var value = preCombo.getValue();
                            Ext.apply(this.proxy.extraParams, {${combo[k-1].name}:(value=='tbar_gang_all'?'':value)});
                        },
                    """
			}

			output += """
                {
                    xtype: 'combo',
                    name: '${combo[k].name}',
                    itemId: 'tbar_gang_${combo[k].name}',
                    fieldLabel: '请选择${combo[k].label}',
                    labelAlign: 'right',
                    displayField: 'name',
                    valueField: 'value',
                    emptyText: '所有${combo[k].label}',
                    editable: false,
                    store: Ext.create('Ext.data.Store', {
                        autoLoad: false,
                        fields: ['name', 'value'],
                        proxy: {
                            type: 'ajax',
                            url: '${g.createLink([controller: combo[k].controller, action: combo[k].action])}'
                        },
                        listeners: {
                            ${beforeload}
                            load: function(_self,records,success){
                                if(success){
                                    _self.insert(0,{name:'所有${combo[k].label}',value:'tbar_gang_all'});
                                    Ext.ComponentQuery.query("#tbar_gang_${combo[k].name}")[0].setValue(this.getAt(0).data.value);
                                }
                            }
                        }
                    }),
                    listeners: {
                        change: function(me, newVal, oldVal, opts){
                            ${changeFun}
                        }
                    }
                },
            """
		}

		out << output
	}

    def extToolbarSelectButton={ attrs, body ->
        List included = attrs.included
        List excluded = attrs.excluded
        String text = attrs.text?:"${g.message(code:"toolbar.singlesel.lang")}"
        String model=attrs.model
        Map domain=ModelService.GetModel(model)
        String propertyName=domain.propertyName
        List<Map> items = attrs.toolbar

        String controller = attrs.controller ?: propertyName
        String action= attrs.action ?: "singleSelCommitAction"  //数据提交到服务器的路径

        List<Map> fields=[]

        String toolBarBtn = ""
        for(int k=0;k<items?.size();k++){
            toolBarBtn += """
                ${k==0?"":","}
                {
                    text: '${items[k].text}',
                    disabled: false,
                    ${items[k].icon?"iconCls: '${items[k].icon}',":"iconCls: 'icon-grid',"}
                    handler: function (){${items[k].handler};}
                }
            """
        }

        //included与excluded是互斥的，定义了included就会忽略excluded
        if(included){
            for(int i=0; i<domain.fields.size();i++)
            {
                for(int j=0;j<included.size();j++){
                    Pattern pattern = Pattern.compile(included[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        fields << domain.transFields[i]
                    }
                }
            }

        }else if(excluded){
            for(int i=0; i<domain.fields.size();i++)
            {
                boolean flag = false //是否匹配正则
                for(int j=0;j<excluded.size();j++){
                    Pattern pattern = Pattern.compile(excluded[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        flag = true
                    }
                }
                if(!flag){
                    fields << domain.transFields[i]
                }
            }
        }else{
            fields=domain.fields
        }

        if(fields.size() > 3){
            fields=fields[0..3]
        }

        String columnsString = ""
        for(int i=0; i<fields.size();i++)
        {
            Map constraint = fields[i].constraint

            String func = ""
            if(fields[i].type=='boolean'){ //控制boolean的grid显示内容
                func = ",renderer:function(value){\r\nreturn (value?'是':'否');\r\n}"
            }else if(constraint.inList && constraint.inListLabel){  //控制带缩写的列表在grid列里的显示内容
                String listVal = "{";
                for(int k=0;k<constraint.inList.size();k++){
                    listVal += """ '${constraint.inList[k]}':'${constraint.inListLabel[k]}'${k<(constraint.inList.size()-1) ?",":"}"}"""
                }
                func = """,renderer:function(value){\r\nvar listArr=${listVal};\r\nreturn listArr[value];\r\n}"""
            }else if(constraint.relation && fields[i].name=='parentId'){
                func = ",renderer:function(value){\r\nreturn (value==''?'无':value);\r\n}"
            }

            if(constraint.relation && constraint.domain){
                columnsString=columnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, sortable: true, dataIndex: '${fields[i].name}Value'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
            }else{
                columnsString=columnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, sortable: true, dataIndex: '${fields[i].name}'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
            }
        }

        String fieldsString = ""
        for(int i=0; i<fields.size();i++)
        {
            Map constraint=fields[i].constraint
            if(constraint.relation && constraint.domain){
                fieldsString=fieldsString+"""'${fields[i].name}Value'${i<(fields.size()-1) ?",":""}"""
            }else{
                fieldsString=fieldsString+"""'${fields[i].name}'${i<(fields.size()-1) ?",":""}"""
            }
        }

        String output ="""
            {
                xtype: 'button',
                itemId: '${propertyName}SingleSelBtn',
                text: '${text}',
                iconCls: 'icon-single-select',
                handler: function () {
                    var selection = ${controllerName}Grid.getView().getSelectionModel().getSelection();

                    if(selection.length==0){
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: '请先选中一条记录！',
                            buttons: Ext.MessageBox.OK
                        });
                        return;
                    }
                    if(selection.length>1){
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: '最多只能选择一条记录！',
                            buttons: Ext.MessageBox.OK
                        });
                        return;
                    }

                    Ext.create('Ext.Window', {
                        title: '请选择',
                        width: 550,
                        modal: true,
                        resizable: false,
                        buttonAlign: 'center',
                        items: [
                            {
                                xtype: 'grid',
                                id:'${propertyName}SingleSelGrid',
                                height:500,
                                autoWidth: true,
                                border: false,
                                selModel: Ext.create('Ext.selection.CheckboxModel'),
                                columns: [
                                    ${columnsString}
                                ],
                                store: Ext.create('Ext.data.Store', {
                                    storeId: '${propertyName}SingleSelStore',
                                    autoLoad: true,
                                    remoteSort: true,
                                    pageSize:15,
                                    fields: [${fieldsString}],
                                    proxy:{
                                        type: 'ajax',
                                        url: '${g.createLink([controller: propertyName, action: "${action}"])}',
                                        reader: {
                                            type: 'json',
                                            root: 'data',
                                            successProperty: 'success',
                                            messageProperty: 'message',
                                            totalProperty: 'totalCount' //数据总条数
                                        },
                                        listeners: {
                                            exception: function (proxy, response, operation) {
                                                Ext.wintip.error('错误!',operation.getError());
                                            }
                                        }
                                    },
                                    listeners: {
                                        beforeload: function (proxy, response, operation) {
                                            Ext.apply(this.proxy.extraParams, {id: selection[0].data.id});
                                        }
                                    }
                                }),
                                dockedItems: [{
                                    xtype: 'toolbar',
                                    dock: 'top',
                                    items:[${toolBarBtn}]
                                }],
                                bbar: {
                                    xtype: 'pagingtoolbar',
                                    prevText:'',
                                    nextText:'',
                                    firstText:'',
                                    lastText:'',
                                    refreshText:'',
                                    store: '${propertyName}SingleSelStore',
                                    displayInfo: true,
                                    displayMsg: '显示 {0} - {1} 条，共 {2} 条',
                                    emptyMsg: "没有数据"
                                },
                                listeners: {
                                    itemdblclick: function(dataview, record, item, index, e){
                                        var selData = Ext.getCmp('${propertyName}SingleSelGrid').getSelectionModel().getSelection();
                                        Ext.Ajax.request({
                                            async : false,
                                            method: 'GET',
                                            url: '${g.createLink([controller: controller, action: action])}',
                                            params: {
                                                id: selData[0].data.id
                                            },
                                            success: function(response){
                                                var response = Ext.JSON.decode(response.responseText);
                                                if(response.success){
                                                    Ext.wintip.msg('操作成功!', response.message);
                                                }else{
                                                    Ext.wintip.error('错误!', response.message);
                                                }
                                            }
                                        });

                                        this.up('window').close();
                                    }
                                }
                            }
                        ],
                        buttons: [
                          {
                            text: '确定',
                            handler: function(){
                                var selData = Ext.getCmp('${propertyName}SingleSelGrid').getSelectionModel().getSelection();
                                if(selData.length==0){
                                    Ext.MessageBox.show({
                                        title: '提示',
                                        msg: '请先选择1个项目！',
                                        buttons: Ext.MessageBox.OK
                                    });
                                    return;
                                }

                                Ext.Ajax.request({
                                    async : false,
                                    method: 'GET',
                                    url: '${g.createLink([controller: controller, action: action])}',
                                    params: {
                                        id: selData[0].data.id
                                    },
                                    success: function(response){
                                        var response = Ext.JSON.decode(response.responseText); //数据返回格式:{success: true, message: "数据已提交服务器!"}
                                        if(response.success){
                                            Ext.wintip.msg('操作成功!', response.message);
                                        }else{
                                            Ext.wintip.error('错误!', response.message);
                                        }
                                    }
                                });
                                this.up('window').close();
                            }
                          },{
                            text: '取消',handler: function(){ this.up('window').close();}
                          }
                        ]
                    }).show();
                }
            },
        """
        out << output
    }
}
