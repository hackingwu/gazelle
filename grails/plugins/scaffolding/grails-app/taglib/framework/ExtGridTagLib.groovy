package framework



import java.util.regex.Pattern

/**
 * 生成默认的Extjs Grid 和 GridStore的方法
 * @className ExtGridTagLib
 * @author Su sunbin
 * @version 2014-09-16 ExtTagLib中的extGrid和extGridStore方法拆分至此
 */
class ExtGridTagLib extends ExtTagLib{
	//static defaultEncodeAs = 'html'
	//static encodeAsForTags = [tagName: 'raw']

	def scaffoldingUtilService

	/**
	 * 生成默认的extjs Store
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.included 需要显示的domain 属性，included与excluded是互斥的，就是定义了included就忽略excluded
	 * @param attrs.excluded 忽略的domain 属性
	 * @param attrs.controller 数据源的服务端控制器
	 * @param attrs.action 数据源的服务器action
	 * @param attrs.extra 附件参数
	 * @param attrs.name store的名称
	 * @return 客户端store代码
	 */
	def extGridStore = { attrs, body ->

		String model=attrs.model
		String extra = attrs.extra?:""
		List included = attrs.included
		List excluded = attrs.excluded
		String associated = attrs.associated?:""
		String action = attrs.action ?: "list"
		String query = attrs.query?"+',${attrs.query}'":""
		Map domain=ModelService.GetModel(model)

		String propertyName=domain.propertyName
		String storeName = attrs.name?:"${propertyName}${extra}Store"

		List<Map> fields=[]

		//included与excluded是互斥的，就是定义了included就忽略excluded
		if(included)
		{

			for(int i=0; i<domain.transFields.size();i++)
			{
                for(int j=0;j<included.size();j++){
                    Pattern pattern = Pattern.compile(included[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        fields << domain.transFields[i]
                    }
                }
			}

		}else if(excluded)
		{

			for(int i=0; i<domain.transFields.size();i++)
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
			fields=domain.transFields
		}

		String fieldsString = ""
		for(int i=0; i<fields.size();i++)
		{
			Map constraint=fields[i].constraint

			if((fields[i].type == "long") && (fields[i].widget != "checkbox") && constraint && constraint.relation && constraint.domain){ //关联的数据为domain class的ID时，需要特殊处理
				fieldsString=fieldsString+"""'${fields[i].name}Value'${i<(fields.size()-1) ?",":""}"""
			}else{
				fieldsString=fieldsString+"""'${fields[i].name}'${i<(fields.size()-1) ?",":""}"""
			}
		}

		String listeners=""

		if(extra==""){
			String query_params;

			if(associated!=""){ //oneToManyTreeView
				query_params=""" '[{"key":"'+(toolbar.down('#searchKey')?toolbar.down('#searchKey').getValue():"")+'","value":"'+(toolbar.down('#'+searchValue)?((searchValue=='searchDateValue'||searchValue=='searchDateTimeValue')?toolbar.down('#'+searchValue).getRawValue().replace(/(^\\s*)|(\\s*\$)/g, ""):toolbar.down('#'+searchValue).getValue().replace(/(^\\s*)|(\\s*\\\$)/g, "")):"")+'"},{"key":"${domain.m.layout.key}","value":"'+${associated}TreeNode+'"}'"""
			}else{
				query_params="""'[{"key":"'+(toolbar.down('#searchKey')?toolbar.down('#searchKey').getValue():"")+'","value":"'+(toolbar.down('#'+searchValue)?((searchValue=='searchDateValue'||searchValue=='searchDateTimeValue')?toolbar.down('#'+searchValue).getRawValue().replace(/(^\\s*)|(\\s*\\\$)/g, ""):toolbar.down('#'+searchValue).getValue().replace(/(^\\s*)|(\\s*\\\$)/g, "")):"")+'"}'"""
			}

			listeners=""",
                    listeners: {
                        beforeload: function (proxy, response, operation) {

                            var gangCombo = toolbar.query("combo");
                            var exParams = "";
                            for(var i=0;i<gangCombo.length;i++){
                                var value = gangCombo[i].getValue();
                                    value = (value=='tbar_gang_all'?'':value);
                                if(gangCombo[i].getItemId().indexOf("tbar_gang_")!=-1){
                                    exParams += ',{"key":"'+gangCombo[i].getName()+'","value":"'+(value?value:"")+'"}';
                                }
                            }

                            try{
                                var startDate = Ext.getCmp('sdate').getValue();
                                var endDate = Ext.getCmp('edate').getValue();
                                var startDateFmt = startDate?(Ext.Date.format(startDate,'Y-m-d H:i:s')):"";
                                var endDateFmt = endDate?(Ext.Date.format(endDate,'Y-m-d H:i:s')):"";

                                exParams += ',{"key":"startDate","value":"'+startDateFmt+'"}';
                                exParams += ',{"key":"endDate","value":"'+endDateFmt+'"}';
                            }catch(e){}

                            var solveCheck = toolbar.query("#tbar_hide_resolved");
                            if(solveCheck[0]){
                                exParams += ',{"key":"'+solveCheck[0].getName()+'","value":"'+(solveCheck[0].getValue()?0:"")+'"}';
                            }
                            if(typeof(searchValue) == 'undefined'){
                                searchValue = 'defaultSearchVal';
                            }
                            var queryParams={
                                search: ${query_params}${query}+exParams+']'
                            };
                            Ext.apply(this.proxy.extraParams, queryParams);
                        }
					}

                """

		}

		String output ="""
        ${storeName} = Ext.create('Ext.data.Store', {
            autoSync: true,
            remoteSort: true,
            fields: [${fieldsString}],
            proxy: {
                type: 'ajax',
                api: {
                    read: '${g.createLink([controller: propertyName, action: action])}'     //列表
                },
                reader: {
                    type: 'json',
                    root: 'data',
                    successProperty: 'success',
                    messageProperty: 'message',
                    totalProperty: 'totalCount' //总条数
                }/*,
                listeners: {
                    exception: function (proxy, response, operation) {
                        Ext.wintip.error('提示','网络异常');
                    }
                }*/
            }${listeners}
        });
    """
		out << output
	}



	/**
	 * 生成默认的extjs Grid
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.extra 附件参数
	 * @param attrs.associatedStore 本grid关联的store对象，一对多视图中需要配置，其他情况不需要
	 * @param attrs.height Grid的高度
	 * @param attrs.included 需要显示的domain 属性，included与excluded是互斥的，就是定义了included就忽略excluded
	 * @param attrs.excluded 忽略的domain 属性
	 * @param attrs.config.column 限定Grid显示的最大列数
	 * @param attrs.config.rowHeight 定义Grid的行高
	 * @param attrs.config.storeName 本grid的数据源关联的store的名称
	 * @param attrs.name Grid对象的变量名称，可选
	 * @return Grid视图代码
	 */
	def extGrid = { attrs, body ->

		int dataBind = attrs.dataBind?:0
		String model=attrs.model
		String extra = attrs.extra?:""
		String associatedStore=attrs.associatedStore?:""
		String height = attrs.height?:""
		List included = attrs.included
		List excluded = attrs.excluded
		int column = ((attrs.config?.column)?:rowNumber).toInteger()
		String rowHeight = (attrs.config?.rowHeight)?:"25" //默认行高25

		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName
		String storeName=(attrs.config?.storeName)?:"${propertyName}${extra}Store"
		String gridName=attrs.name?:"${propertyName}${extra}Grid"
		String gridPanel=attrs.name?:"${propertyName}${extra}GridPanel"
		String controller = attrs.controller ?: controllerName
		String action= attrs.action ?: "detailAction"
        String gridRowDbClickFunc = attrs.rowDbClickFunc?attrs.rowDbClickFunc:"${propertyName}${extra}Detail"
        String listener = attrs.listener?",${attrs.listener.collect{k,v->k+":"+v}.join(",")}":""
		String viewConfig = attrs.viewConfig?"${attrs.viewConfig.collect{k,v->k+":"+v}.join(",")}":"";
        List<Map> fields=[]
        String selModel = ((attrs.selModel&&attrs.selModel=="SINGLE")?"":"selModel: Ext.create('Ext.selection.CheckboxModel'),")

        boolean detailDisable=(!authService.Button(session["sysRoleId"], "${propertyName}_detail"))

        //included与excluded是互斥的，定义了included就会忽略excluded
		if(included)
		{

			for(int i=0; i<domain.transFields.size();i++)
			{
                for(int j=0;j<included.size();j++){
                    Pattern pattern = Pattern.compile(included[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        fields << domain.transFields[i]
                    }
                }
			}

		}else if(excluded)
		{
            for(int i=0; i<domain.transFields.size();i++)
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
			fields=domain.transFields
		}

		if(fields.size() > column){
			fields=fields[0..column-1]
		}

		String columnsString = ""
		for(int i=0; i<fields.size();i++)
		{
			Map constraint = fields[i].constraint
			String func = ""

			if(fields[i].renderer){ //自定义渲染函数,约定：必须放在gridRenderService中实现
				func =  ","+grailsApplication.mainContext.getBean("gridRenderService")."${fields[i].renderer}"()
			}else if(constraint && constraint.gridview){
                if(constraint.gridview.type == "map"){
                    func = """
                        ,renderer:function(value){
                            var map = ${constraint.gridview.params};
                            return map[value];
                        }
                    """
                }else if(constraint.gridview.type == "icon"){
                    func = """
                        ,renderer:function(value){
                            var map = ${constraint.gridview.params};
                            var ret = '';
                            for(var k=0;k<map.length;k++){
                                if(map[k].value == value){
                                    ret='<img src="${g.resource(dir: "images/renderer", file: "")}/'+map[k].url+'" title="'+map[k].title+'" />'+map[k].title;
                                }
                            }
                            return ret;
                        }
                    """
                }
            }else if(fields[i].type=='boolean'){ //控制boolean的grid显示内容
				if(constraint.isCheckbox){
					func = """,renderer:function(value){
                        return (eval(value)?'已选':'未选');
                        }"""
				}else if(constraint.inListLabel){
					func = """,renderer:function(value){
                            return (eval(value)?'${constraint.inListLabel[0]}':'${constraint.inListLabel[1]}');
                            }"""
				}else{
					func = """,renderer:function(value){
                        return (eval(value)?'是':'否');
                        }"""
				}
			}else if(fields[i].widget == "checkbox"){
				String listVal = "{";
				if(constraint.items) { //items已在domain class中定义
					for(int k=0;k<constraint.items.size();k++){
						listVal += """ '${constraint.items[k].value}':'${constraint.items[k].label}'${k<(constraint.items.size()-1) ?",":""}"""
					}
				}else{//数据来自ajax
					List modelDatas = ModelService.GetModelDataByDomainName(constraint.domain)
					for(int j=0;j < modelDatas.size(); j++){
						listVal += """ '${modelDatas[j][constraint.valueField]}':'${modelDatas[j][constraint.displayField]}'${j<(modelDatas.size()-1) ?",":""}"""
					}
				}

				listVal +="}"

				func = """,
                        renderer:function(value){
                        if(''==value) {return '';}
                        var listArr=${listVal};
                        var label="";
                        for(var m=0;m<value.length;m++){
                            label+=listArr[value[m]]+(value.length-1==m?"":",");
                        }
                        return label;}
                    """
			}else if(constraint && constraint.inList && constraint.inListLabel){  //控制带缩写的列表在grid列里的显示内容
				String listVal = "{";
				for(int k=0;k<constraint.inList.size();k++){
					listVal += """ '${constraint.inList[k]}':'${constraint.inListLabel[k]}'${k<(constraint.inList.size()-1) ?",":"}"}"""
				}
				func = """,renderer:function(value){\r\nvar listArr=${listVal};\r\nreturn listArr[value];\r\n}"""
			}else if(constraint && constraint.relation && fields[i].name=='parentId'){
				func = ",renderer:function(value){\r\nreturn (value=='null'?'无':value);\r\n}"
			}else if(scaffoldingUtilService.isOne2OneFieldType(fields[i].type)){
				func = """,renderer:function(value){
                      return value.name;
                }"""
			}

			if((fields[i].type == "long") && (fields[i].widget != "checkbox") && constraint && constraint.relation && constraint.domain){ //关联的数据为domain class的ID时，需要特殊处理
				columnsString=columnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, renderer: 'htmlEncode', sortable: false, dataIndex: '${fields[i].name}Value'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
			}else if(constraint && constraint.gridview){
				if(constraint.gridview.type == "checkbox"){
					String tpl = """
                        new Ext.XTemplate(
                            '<input type="checkbox" onclick="${fields[i].name}RowClick(\\'{id}\\')"',
                            '<tpl if="${fields[i].name} == ${constraint.gridview.params}">',
                            'checked',
                            '</tpl>',
                            '/>'
                        )
                    """
					columnsString=columnsString+"""{header: '${fields[i].cn}',xtype: 'templatecolumn',renderer: 'htmlEncode', tpl:${tpl},flex: ${fields[i].flex},dataIndex: '${fields[i].name}'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
				}else{
					columnsString=columnsString+"""{header: '${fields[i].cn}',flex: ${fields[i].flex}, renderer: 'htmlEncode', dataIndex: '${fields[i].name}'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
				}
			}else{ //关联的外部属性在Grid中显示时，不支持排序
				columnsString=columnsString+"""{${(model=='slittingMember')?'editor:{allowBlank:false},':''}header: '${fields[i].cn}',${((constraint?.fromDomain)||(fields[i].type=='list')|| (fields[i].sort!=null&&fields[i].sort==false))?"sortable: false,":""}flex: ${fields[i].flex}, ${fields[i].widget == "htmleditor"?"renderer:function(value){return Ext.cubeUtil.noHtml(value);},":"renderer: 'htmlEncode',"} dataIndex: '${fields[i].name}'"""+func+"""}${i<(fields.size()-1) ?",":""}\r\n"""
			}
		}

		String heightString

		if(height)
		{
			heightString="""parseInt(document.documentElement.clientHeight-50)*${height};"""
		}else{
			heightString="""document.documentElement.clientHeight-50;"""
		}

		String itemClick= ""
		if(associatedStore!="")
		{
			itemClick="""
                itemclick: function(dataview, record, item, index, e){
                    var query_params = {
                        search: '[{"key":"${domain.m.layout.key}","value":"'+record.data.id+'"}];'
                    };
                    Ext.apply(${associatedStore}.getProxy().extraParams, query_params);
                    ${associatedStore}.load();
                },"""
		}

		String output ="""
        //计算grid高度
        if(document.documentElement && document.documentElement.clientHeight){
            ${propertyName}${extra}GridHeight = ${heightString}
            var pageSize = parseInt((${propertyName}${extra}GridHeight-5-30-50)/${rowHeight});
            ${storeName}.pageSize=pageSize;
        }

        ${gridName} = Ext.create('Ext.grid.Panel', {
            height: ${propertyName}${extra}GridHeight,
            autoWidth: true,
            border:false,
            store: ${storeName},
            stripeRows:true,
            loadMask: true,
            viewConfig:{
                ${viewConfig}
            },
            plugins:[
                 Ext.create('Ext.grid.plugin.CellEditing',{
                     clicksToEdit:2 //设置单击单元格编辑
                 })
            ],
            ${selModel}
            columns: [
                ${columnsString}
            ],
			"""
			if (!dataBind) {
				output += """
//底部翻页
            bbar: Ext.create('Ext.PagingToolbar', {
                prevText:'',
                nextText:'',
                firstText:'',
                lastText:'',
                refreshText:'',
                store: ${storeName},
                displayInfo: true,
                displayMsg: '显示 {0} - {1} 条，共 {2} 条',
                emptyMsg: "${g.message(code: "grid.bbr.empty1.lang")}${domain.m?.domain?.cn}${
					g.message(code: "grid.bbr.empty2.lang")
				}"
            }),
            """
			}
				output += """
            listeners:{
                ${itemClick}
                itemdblclick: function(dataview, record, item, index, e){
                    ${
					if(!detailDisable){
						"""
                            ${gridRowDbClickFunc}(dataview, record, item, index, e);
                            """
					}
				}
                }
                ${listener}
            }
        });
    """
//		if (dataBind) {
//			output += """
//
//			${gridPanel}=Ext.create('Ext.Panel', {
//
//				frame: true,
//				title: 'Book List',
//				layout: 'border',
//				items: [
//					${gridName},
//					{
//					xtype: 'form',
//					id: '${propertyName}${extra}DetailPanelForm',
//					layout: 'form',
//					autoScroll:true,
//					bodyPadding: '5 5 5 5',
//					defaultType: 'textfield',
//					items: ["""
//
//			List layout = grailsApplication.getArtefactByLogicalPropertyName("Domain", model).getPropertyValue("layout")
//
//			if (layout){
//				output+= createComplexWnd(model,fields,layout,'Detail')
//			}else{
//				for(int i=0;i<fields.size();i++)
//				{
//					output = output +"\r\n"+Scaffolding(fields[i], "detail", model) + "${i<(fields.size()-1)?",":""}"
//				}
//			}
//			output=output+"""
//                    ]
//				}]
//			})
//			// update panel body on selection change
//			${gridName}.getSelectionModel().on('selectionchange', function(sm, selection) {
//				${gridPanel}.down('form').getForm().load({
//				params: { id: selection[0].data.id},
//				url: '${g.createLink([controller: controller, action: action])} ',
//				autoLoad: true,
//				success:function(form,action){
//					${scaffoldingUtilService.getAllOne2OneFieldsScript('detail', fields)}
//				},
//				failure: function(form, action) { //失败返回：{ success: false, errorMessage:"指定的数据不存在" }
//					if(!action.result) return; //超时处理
//					Ext.MessageBox.show({
//						title: '数据加载失败',
//						msg: action.result.errorMessage,
//						buttons: Ext.MessageBox.OK
//					});
//				}
//        	});
//			"""
//		}
		out << output
	}

}
