package framework
/**
 * 生成ToolBar上的最常用的button的方法
 * @className ExtToolBarTagLib
 * @author Su sunbin,Lch
 * @version 2014-09-16 ExtTagLib中的生成ToolBar上最常用的的button的方法拆分至此
 * @version 2014-09-28 extFormButtons，提供给fromView类型的页面，提供一个支持权限的更新buttons和一个不支持权限的重置buttons
 * @version 2014-10-10 新增一个extOtherButtons标签，用来放一些不常用的类似反馈页面确认处理的buttons
 */
class ExtToolBarTagLib extends ExtTagLib{

    static namespace = "m"
	//static defaultEncodeAs = 'html'
	//static encodeAsForTags = [tagName: 'raw']

	/**
	 * 在toolbar上生成搜索组件
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.included 需要加入搜索的domain class属性
	 * @param attrs.excluded 不需要加入搜索的domain属性
	 * @param attrs.extra 附件变量标志，通常无需配置
	 * @param attrs.config.storeName 关联的store名称
	 * @return 搜索组件视图
	 */
	def extSearchButtons = { attrs, body ->

		String model = attrs.model
		String included = attrs.included
		String excluded = attrs.excluded
		String extra = attrs.extra?:""

		Map domain=ModelService.GetModel(model)
		String storeName=(attrs.config?.storeName)?:"${domain.propertyName}${extra}Store"

		List<Map> fields=[]
		Map config=attrs.config?:[:]

		String queryFunc = ""
		if(config.autoQuery){
			queryFunc = """
                'change': function(el,e,opts){
                    if(searchValue == 'defaultSearchVal')
                        {
                            Ext.MessageBox.show({
                                title: '提示',
                                msg: '请先选择查询条件！',
                                buttons: Ext.MessageBox.OK
                            });
                                return;
                        }else{
                        ${storeName}.loadPage(1);
						}
                }
            """
		}else{
			queryFunc = """
                specialkey: function(field, e){
                    if (e.getKey() == e.ENTER) {
						if(searchValue == 'defaultSearchVal')
                        {
                            Ext.MessageBox.show({
                                title: '提示',
                                msg: '请先选择查询条件！',
                                buttons: Ext.MessageBox.OK
                            });
                                return;
                        }else{
                        ${storeName}.loadPage(1);
						}
                    }
                }
            """
		}

		//included与excluded是互斥的，定义了included就会忽略excluded
		if(included)
		{
			for(int i=0; i<domain.fields.size();i++)
			{
				if(included.contains(domain.fields[i].name)){
					fields << domain.fields[i]
				}
			}

		}else if(excluded)
		{
			for(int i=0; i<domain.fields.size();i++)
			{
				if(!excluded.contains(domain.fields[i].name)){
					fields << domain.fields[i]
				}
			}
		}else{
			fields=domain.fields
		}

		String columnsString = """{"name": "---请选择---", "value": "defaultSearchVal","type": "string","widget": "null","list":""},"""
		String searchList = "{"
		int countList = 0
		int itemID = -1;
		for(int i=0; i<fields.size();i++)
		{
			Map constraint = fields[i].constraint?:[:]
			if(constraint.relation && constraint.domain && fields[i].type!='string'){
				continue
			}else{
				if(itemID==-1){
					itemID = i
				}
				columnsString=columnsString+"""{"name": "${fields[i].cn}", "value": "${fields[i].name}","type": "${fields[i].type}","widget": "${fields[i].widget}","list":"${fields[i].constraint.inList?'list':''}"}${i<(fields.size()-1) ?",":""}"""
				if(constraint.inList){
					countList++
					searchList = searchList + """${(countList==1)?"":","} '${fields[i].name}': ["""
					for(int j=0;j < constraint.inList.size(); j++)
					{
						searchList =searchList + """{'display':'${constraint.inListLabel?constraint.inListLabel[j]:constraint.inList[j]}','value':'${constraint.inList[j]}'}${j<(constraint.inList.size()-1)?",":""}"""
					}
					searchList =searchList + """]"""
				}
			}
		}
		searchList =searchList + """}"""

		String output = """
                {
                    xtype: 'combo',
                    itemId: 'searchKey',
                    name: 'searchKey',
                    width:100,
                    displayField: 'name',
                    valueField: 'value',
                    store: Ext.create('Ext.data.Store', {
                        fields: ['name', 'value','type','widget','list'],
                        data: [
                            ${columnsString}
                        ]
                    }),
                    value:"defaultSearchVal",
                    editable: false,
                    typeAhead: true,
                    queryMode: 'local',
                    forceSelection: true,
                     listeners: {
                        change: function(me,newVal,oldVal, opts){
								var items = [${columnsString}];
								var input=toolbar.down('#searchStringValue');
								input.setValue('');
								var inputCombo=toolbar.down('#searchComboValue');
								inputCombo.setValue('');
								var inputDate=toolbar.down('#searchDateValue');
								inputDate.setValue('');
								searchValue = 'defaultSearchVal';
								for(var i=0; i<items.length;i++){
									if(newVal==items[i].value)
									{
				                        if(items[i].list == 'list')
				                        {
											var searchList= ${searchList};
											var data = searchList[newVal];
											inputCombo.getStore().loadData(data);
				                            inputCombo.show();
				                            inputDate.hide();
				                            input.hide();
				                            searchValue = 'searchComboValue';
				                        //调用“年-月-日”时间控件
				                        }else if(items[i].widget == 'datefield'||items[i].widget == 'datetimefield')
				                        {
				                            inputDate.show();
				                            input.hide();
				                            inputCombo.hide();
				                            searchValue = 'searchDateValue';
			                            }
			                            else if(newVal == 'defaultSearchVal')
			                            {
			                                input.show();
			                                inputDate.hide();
			                                inputCombo.hide();
			                            }else
			                            {
			                                input.show();
			                                inputDate.hide();
			                                inputCombo.hide();
			                                searchValue = 'searchStringValue';
			                            }
		                            }
							}
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    itemId: 'searchStringValue',
                    width:120,
                    name: 'search',
                    enableKeyEvents:true,
                    listeners:{
                        ${queryFunc}
                    }
                },
				{
					xtype:'combo',
					hidden:true,
					itemId: 'searchComboValue',
					width:120,
					name: 'searchComboValue',
					editable: false,
	                store: Ext.create('Ext.data.Store', {
                        fields: ['display','value'],
                        data: [
                        ]
                    }),
                    queryMode: 'local',
					valueField: 'value',
					displayField: 'display',
                    listeners:{
                        ${queryFunc}
                    }
				},
				{
					xtype:'datefield',
					hidden:true,
					editable: false,
					itemId: 'searchDateValue',
					width:120,
					name: 'searchDateValue',
					format: 'Y-m-d',
					allowBlank: true,
                    listeners:{
                        ${queryFunc}
                    }
				},
				{
					xtype:'datetimefield',
					hidden:true,
					editable: false,
					itemId: 'searchDateTimeValue',
					width:120,
					name: 'searchDateTimeValue',
					allowBlank: true,
					afterLabelTextTpl: required,
                    listeners:{
                        ${queryFunc}
                    }
				},
                {
                    itemId: 'search',
                    text: '${g.message(code:"toolbar.search.lang")}',
                    iconCls: 'icon-search',
                    handler: function () {
                        if(searchValue == 'defaultSearchVal')
                        {
                            Ext.MessageBox.show({
                                title: '提示',
                                msg: '请先选择查询条件！',
                                buttons: Ext.MessageBox.OK
                            });
                                return;
                        }else{
                        ${storeName}.loadPage(1);
                        }
                    }
                },
                {
                    itemId: 'resetSearch',
                    text: '${g.message(code:"toolbar.reset.lang")}',
                    iconCls: 'icon-reset',
                    handler: function () {
                        if(searchValue == 'defaultSearchVal'){
                            toolbar.down('#searchStringValue').setValue("");
                        }else{
                            toolbar.down('#'+searchValue).setValue("");
                        }

                        var gangCombo = toolbar.query("combo");
                        for(var i=0;i<gangCombo.length;i++){
                            if(gangCombo[i].getItemId().indexOf("tbar_gang_")!=-1){
                                gangCombo[i].setValue("all");
                            }
                        }
                        ${storeName}.loadPage(1);
                    }
                }
"""

		out << output
	}

	/**
	 * 在toolbar上生成CRUD按钮
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.excluded 不需要进行CRUD操作的domain属性
	 * @param attrs.extra 附件变量标志，通常无需配置
	 * @param attrs.controller commit的服务端控制器
	 * @param attrs.action commit 的服务端action
	 * @param attrs.config.gridName 关联的Grid名称
	 * @return CRUD功能视图
	 */
	def extCRUDButtons={ attrs, body ->

		String model = attrs.model
		List<String> excluded = attrs.excluded
		String extra = attrs.extra?:""
		String controller = attrs.controller ?: controllerName
		String action= attrs.action ?: "deleteAction"
		Map domain=ModelService.GetModel(model)
        String codePrefix = attrs.authCodePrefix?attrs.authCodePrefix:controller

		String propertyName=domain.propertyName
		String gridName=(attrs.config?.gridName)?:"${propertyName}${extra}Grid"

		String output =""

//		boolean createDisable=(!authService.Button(session["roles"], "${controller}Index","create"))
//		boolean detailDisable=(!authService.Button(session["roles"], "${controller}Index","detail"))
//		boolean updateDisable=(!authService.Button(session["roles"], "${controller}Index","update"))
//		boolean deleteDisable=(!authService.Button(session["roles"], "${controller}Index","delete"))
        boolean createDisable=(!authService.Button(session["sysRoleId"], "${codePrefix}_create"))
        boolean detailDisable=(!authService.Button(session["sysRoleId"], "${codePrefix}_detail"))
        boolean updateDisable=(!authService.Button(session["sysRoleId"], "${codePrefix}_update"))
        boolean deleteDisable=(!authService.Button(session["sysRoleId"], "${codePrefix}_delete"))

		if(!excluded || !excluded.contains("create"))
		{
			output = output + """
                {
                    itemId:'create',
                    text: '新增',
                    disabled: ${createDisable},
                    iconCls: 'icon-add',
                    handler: function () {
                        ${propertyName}Create();
                    }
                },"""
		}

		if(!excluded || !excluded.contains("detail"))
		{
			output = output + """
                {
                    itemId: 'detail',
                    text: '详细',
                    disabled: ${detailDisable},
                    iconCls: 'icon-grid',
                    handler: function () {
                        ${propertyName}Detail();
                    }
                },"""
		}

		if(!excluded || !excluded.contains("update"))
		{
			output = output + """
                {
                    itemId: 'update',
                    text: '修改',
                    disabled: ${updateDisable},
                    iconCls: 'icon-edit',
                    handler: function () {
                        ${propertyName}Update();
                    }
                },"""
		}

		if(!excluded || !excluded.contains("delete"))
		{
            String submitFunc = ""
            if(domain.m.layout.type == "oneToManyTreeView"){
                submitFunc = """
                    ${domain.m.layout.domain}Tree.getStore().reload();
                """
            }

            String deleteFunc = """
                var data = ${gridName}.getView().getSelectionModel().getSelection();
                if(data.length==0){
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: '请选择需要删除的${domain.m?.domain?.cn}！',
                        buttons: Ext.MessageBox.OK
                    });

                    return;
                }

                if(typeof(beforeDelete)=='function'){
                    if(!beforeDelete(data)) return
                }

                Ext.MessageBox.confirm('确认', '确定删除选中的${domain.m?.domain?.cn}记录?',function(sel){
                    if(sel=='no') return;
                    var ids = [];
                    Ext.Array.each(data, function(record) {
                        ids.push(record.get('id'));
                    });

                    Ext.Ajax.request({
                        url: '${g.createLink([controller: controller, action: action])}',
                        method : 'post',
                        params: {data:ids.join()},
                        success: function(response) {
                            if(!response.responseText || response.responseText=="") return; //超时处理
                            var result = Ext.decode(response.responseText);
                            if(result.success){
                                Ext.wintip.msg('操作成功!',  result.message);
                                ${gridName}.getStore().reload(); //loadPage(1);
                                ${submitFunc}
                            }else{
                                Ext.wintip.error('操作失败!',  result.message);
                            }
                        },
                        failure: function() {
                            Ext.wintip.error('请求超时!',  '网络超时!');
                        }
                    });
                });
            """

            if(attrs.deleteFunc){
                deleteFunc = attrs.deleteFunc+"();"
            }
			output = output + """
                {
                    itemId: 'delete',
                    text: '删除',
                    disabled: ${deleteDisable},
                    iconCls: 'icon-delete',
                    handler: function () {
                        ${deleteFunc}
                    }
                },"""
		}

		out << output
	}

    /**
     * @author lch
     * @version 2014/9/18
     * 生成默认的extjs form数据重置
     * @param attrs.model 模型名称，类似: student, company
     * @param attrs.controller 数据提交的服务端控制器
     * @param attrs.associated 如果为Formtree类型，form数据重置时需要传入对应的树节点名字
     * @param attrs.excluded 不显示在单选列表中的domain class属性
     * @return 窗口实体web代码
     */
    def extFormButtons={ attrs, body ->

        String model = attrs.model
        List<String> excluded = attrs.excluded
        String extra = attrs.extra?:""
        String controller = attrs.controller ?: controllerName
        Map domain=ModelService.GetModel(model)


        String propertyName=domain.propertyName

        String output =""

        //这里重置按钮默认对所有角色都开启
        boolean resetDisable= false
        boolean updateDisable=(!authService.Button(session["sysRoleId"], "${controller}_update"))
//        boolean updateDisable=(!authService.Button(session["roles"], "${controller}Index","update"))

        if(!excluded || !excluded.contains("update"))
        {
            output = output + """
                {
                    itemId: 'update',
                    text: '${g.message(code:"toolbar.modify.lang")}',
                    disabled: ${updateDisable},
                    iconCls: 'icon-edit',
                    handler: function () {
                        ${propertyName}FormUpdate();
                    }
                },"""
        }

        if(!excluded || !excluded.contains("reset"))
        {
            output = output + """
                {
                    itemId: 'reset',
                    text: '${g.message(code:"toolbar.reset.lang")}',
                    disabled: ${resetDisable},
                    iconCls: 'icon-edit',
                    handler: function () {
                        ${propertyName}reset();
                    }
                },"""
        }
        out << output
    }

	/**
	 * 在toolbar上生成更多按钮
	 * @param attrs.model 关联的domain class名称
	 * @param attrs.url 前端单/多行选择提交处理函数url
	 * @return 更多按钮
	 */
	def extMoreButtons= { attrs, body ->
		String output = "{text: '更多操作',iconCls: 'icon-multi-select',menu: ["
		List<Map> items = attrs.items
		String controller = attrs.controller ?: controllerName
//		boolean abortCountDisable=(!authService.Button(session["roles"], "${controller}Index","abortCount"))
        boolean abortCountDisable=(!authService.Button(session["sysRoleId"], "${controller}_abortCount"))


        for(int k=0;k<items?.size();k++){
//            String code = items[k].code
            boolean disabled=(!authService.Button(session["sysRoleId"], items[k].code))

            output += """
                new Ext.Action({
                    text: '${items[k].text}',
                    disabled: ${disabled},
                    ${items[k].icon?"iconCls: '${items[k].icon}',":""}
                    handler: function (){${items[k].handler};}
                })${(k==items.size()-1)?'':','}
            """
		}

		output +="]},"

		out << output
	}

    /**
     * 在toolbar上定制一些特殊的buttons
     */
    def extOtherButtons={ attrs, body ->

        List<Map> items = attrs.items
        String output =""


        for(int k=0;k<items?.size();k++){
            boolean otherOperDisable=(!authService.Button(session["sysRoleId"], items[k].code))
            output = output + """
                {
                    text: '${items[k].text}',
                    disabled: ${otherOperDisable},
                    ${items[k].icon?"iconCls: '${items[k].icon}',":""}
                    handler: function () {
                        ${items[k].handler};

                    }
                },
                """
        }
        out << output
    }


    def extEnableButtons={attrs,body->
        String action = attrs.action?:"enable"
        String buttonText = attrs.buttonText?:(action=="enable"?"启用":"禁用")
        String codePrefix = attrs.authCodePrefix?:controllerName
        String callback = attrs.callback
        String extra = attrs.extra?:""
        String model = attrs.model
        Map domain = ModelService.GetModel(model)
        String propertyName = domain.propertyName
        String gridName = (attrs.config?.gridName)?:"${propertyName}${extra}Grid"
        boolean onDisable = (!authService.Button(session["sysRoleId"],"${codePrefix}_"+action))
        String output = """
            {
                itemId:"${action}",
                text:"${buttonText}",
                disabled:${onDisable},
                iconCls:'icon-edit',
                handler:function(){
                    ${
                        if (callback != null) {
                            callback+"();"
                        }else {
                            checkWithoutSel(gridName);

                        }
                    }
            """
        output+="""

                Ext.MessageBox.confirm('确认', '确定${action=="enable"?"启用":"禁用"}选中的${domain.m?.domain?.cn}记录?',function(sel){
                    if(sel=='no') return;
                    var ids = [];
                    Ext.Array.each(data, function(record) {
                        ids.push(record.get('id'));
                    });

                    Ext.Ajax.request({
                        url: '${g.createLink([controller: model, action: "enable"])}',
                        method : 'post',
                        params: {ids:ids.join(','),enabled:${action=="enable"?1:0}},
                        success: function(response) {
                            if(!response.responseText || response.responseText=="") return; //超时处理
                            var result = Ext.decode(response.responseText);
                            if(result.success){
                                Ext.wintip.msg('操作成功!',  result.message);
                                ${gridName}.getStore().reload(); //loadPage(1);
                            }else{
                                Ext.wintip.error('操作失败!',  result.message);
                            }
                        },
                        failure: function() {
                            Ext.wintip.error('请求超时!',  '网络超时!');
                        }
                    });
                });

                }
            }

        """
        out<<output
    }

    private String extEnableButtonHandler(){
        return """

        """
    }

    private String checkWithoutSel(String gridName){
        String output = """
            var data = ${gridName}.getView().getSelectionModel().getSelection();
            if(data.length == 0){
                Ext.MessageBox.show({
                    title:'提示',
                    msg:'请先选择一条记录',
                    buttons:Ext.MessageBox.OK
                });
                return;
            }
        """
    }

    /**
     * 生成导入处理函数
     * @return 导入处理函数
     */
    def extImportFileFunc= { attrs, body ->
        String funcName=attrs.funcName?attrs.funcName:"importFile"
        String controller = attrs.controller ?attrs.controller:controllerName
        String action= attrs.action ?attrs.action: "importData"
        String id= attrs.id ?attrs.id: "0"
        String tplName = attrs.tplName?attrs.tplName:""
        String tplDownLoad = ""

        if(tplName!=""){
            tplDownLoad = """
                ,{
                   xtype:"text",
                   height: 30,
                   html:'<a href="#" onclick="window.open(\\'${g.createLink([controller: 'importInfo', action: "exportFile"])}?filename=${tplName}\\');" style="text-decoration:none;">下载导入模板</a>',
                   style:{
                        padding:'10px 0px 0px 0px'
                   }
                }
            """
        }

        String output = """
                function ${funcName}() {
                    //建form表单
                    var required = '<span style="color:red;font-weight:bold">*</span>';

                    var fileUploadForm = Ext.widget({
                        xtype: 'form',
                        layout: 'form',
                        //height:100,
                        //frame: true,
                        bodyPadding: '5 5 5 5',
                        buttonAlign: 'center',
                        defaults: {
                            msgTarget: 'side',
                            allowBlank: false,
                            labelWidth: 50
                        },
                        items: [{
                            xtype: 'filefield',
                            name: 'upload-file',
                            emptyText: '请选择文件',
                            fieldLabel: '文件名',
                            afterLabelTextTpl: required,
                            buttonText: '',
                            buttonConfig: {
                                iconCls: 'icon-upload'
                            },
                            listeners: {
                                change: function (btn, v) {
                                    var fileName = v.replace(/^\\w+:\\\\.+\\\\(.+\\.xls|.+\\.xlsx)\$/gi,"\$1");
                                    //支持.xls,.xlsx结尾的文件类型
                                    var file_reg = /\\.xls{1}|\\.xlsx{1}\$/;
                                    if (!file_reg.test(fileName)) {
                                        Ext.Msg.alert('提示', '请选择以.xls或.xlsx结尾的文件！');
                                        this.setRawValue('');
                                        return;
                                    }

                                    if(fileName.indexOf("_")!=-1){
                                        Ext.Msg.alert('提示', '文件名中不能含有下划线！');
                                        this.setRawValue('');
                                        return;
                                    }
                                }
                            }
                        }${tplDownLoad}
                        ],
                        buttons: [{
                            text: '导入',
                            handler: function(){
                                var _self = this;
                                var form = _self.up('form').getForm();
                                if(form.isValid()){
                                    form.submit({
                                        url: '${g.createLink([controller: controller, action: action])}',
                                        params: {'id':${id}},
                                        waitMsg: '数据导入中，请稍后......',
                                        success: function(fp, o) {
                                            Ext.MessageBox.confirm('确认', '数据处理中，跳转到信息导入页面查看处理结果?',function(sel){
                                                if(sel=='no') return;
                                                _self.up('window').close();
                                                top.menuItemClick('importFileMng','导入导出管理','${g.createLink([controller: 'importInfo', action:"index"])}'); //id在配置文件菜单定制中定义
                                            });
                                        },
                                        failure: function(form, action) {
                                            var result = Ext.JSON.decode(action.response.responseText);
                                            var errorInfo = '<div class="x-component x-box-item x-component-default x-dlg-icon x-message-box-error" style="margin: 0px; left: 0px; top: 0px; width: 50px; height: 35px; right: auto;"></div><div style="padding: 10px 0px 0px 33px;">'+result.message+'</div>';
                                            errorInfo +='<div style="padding:5px 0px 0px 5px;">'
                                            if(result.errorMessages){
                                                for(var k=0;k<result.errorMessages.length;k++){
                                                    errorInfo +="<p>"+result.errorMessages[k]+"</p>";
                                                }
                                            }
                                            errorInfo +='</div>';
                                            Ext.create('Ext.Window', {
                                                title: '导入失败',
                                                maxHeight : 500,
                                                autoScroll:true,
                                                plain: true,
                                                modal: true,
                                                resizable: false,
                                                html: errorInfo,
                                                buttonAlign:'center',
                                                buttons: [{
                                                    text: '关闭',
                                                    handler:function(){
                                                        this.up('window').close();
                                                    }
                                                }]
                                            }).show();
                                        }
                                    });
                                }
                            }
                        },{
                            text: '取消',
                            handler: function() {
                                this.up('window').close();
                            }
                        }]
                    });

                    //贴到window上
                    var fileUploadWin = Ext.create('Ext.Window', {
                        title: '导入',
                        width: 350,
                        plain: true,
                        modal: true,
                        resizable: false,
                        items: [fileUploadForm]
                    });

                    fileUploadWin.show();
                }
        """
        out << output
    }

    /**
     * 生成导出处理函数
     * @return 导出处理函数
     */
    def extExportFileFunc= { attrs, body ->
        String funcName=attrs.funcName?attrs.funcName:"exportFile"
        String controller = attrs.controller ?attrs.controller: ""
        String action= attrs.action ?attrs.action: "exportData"
        String id= attrs.id ?attrs.id: "0"
        String output = """
            function ${funcName}() {
                Ext.Ajax.request({
                    url: '${g.createLink([controller: controller, action: action])}',
                    method : 'post',
                    success: function(response) {
                        if(!response.responseText || response.responseText=="") return; //超时处理
                        var result = Ext.decode(response.responseText);
                        if(result.success){
                            Ext.MessageBox.confirm('确认', '导出处理中，跳转到导入导出管理页面查看处理结果?',function(sel){
                                if(sel=='no') return;
                                top.menuItemClick('importFileMng','导入导出管理','${g.createLink([controller: 'importInfo', action:"index"])}'); //id在配置文件菜单定制中定义
                            });
                        }else{
                            Ext.wintip.msg('操作失败!',  result.message);
                        }
                    },
                    failure: function() {
                        Ext.wintip.msg('请求超时!',  '网络超时!');
                    }
                });
            }
        """

        out << output
    }
}
