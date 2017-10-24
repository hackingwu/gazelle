package framework

import org.springframework.beans.factory.NoSuchBeanDefinitionException

/**
 * @className ExtTagLib
 * @author lichb,lin yu,Su sunbin
 * @version 2014-08-21 单搜索框根据不同字段展示不同组件
 * @version 2014-09-18 新增extForm，extFormButtons，extFormUpdateWnd，extResetWnd，extFormTree五个为FormView和FormTreeView服务的控件
 * @version 2014-09-19 修改了extFormUpdateWnd窗口一个结果未判断的问题,以及extForm边界属性border设为false
 * @version 2014-09-23 extForm中新增根据属性所需页面高度动态分列的功能，修改extLayout,支持FormView和FormTreeView
 * @version 2014-09-28 将extFormButtons，extFormUpdateWnd，extResetWnd，extFormTree四个标签移到对应的拆分文件中
 * @since  Tue Aug 05 14:28:25 CST 2014
 * TODO://关于所有included和excluded的测试，代码中得类型好像用错了应为: List<String>
 */

/**
 * 动态生成界面的标签
 *
 *
 */
class ExtTagLib {
	//static defaultEncodeAs = 'html'
	//static encodeAsForTags = [tagName: 'raw']

	static namespace = "m"
	static rowNumber = 8 //表格默认显示8列

    def authService
	def grailsApplication
	def scaffoldingAssociationDefaultService
//    def auth
    def scaffoldingUtilService

	/**
	 * 导入extjs4.2的资源
	 */
	def extResource = { attrs, body ->

		out <<"""
            <link href="${g.resource([plugin: "scaffolding", dir: "/ext/resources/css", file: "ext-all-neptune.css"])}" rel="stylesheet" type="text/css"/>
            <link href="${g.resource([plugin: "scaffolding", dir: "/css", file: "data-view.css"])}" rel="stylesheet" type="text/css"/>
            <link href="${g.resource([plugin: "scaffolding", dir: "/css", file: "toolbar.css"])}" rel="stylesheet" type="text/css"/>
            <link href="${g.resource([plugin: "scaffolding", dir: "/css", file: "wintip.css"])}" rel="stylesheet" type="text/css"/>
            <link href="${g.resource([plugin: "scaffolding", dir: "/css", file: "calendar.css"])}" rel="stylesheet" type="text/css"/>
            <link href="${g.resource([plugin: "scaffolding", dir: "/css", file: "examples.css"])}" rel="stylesheet" type="text/css"/>
            <script src="${g.resource([plugin: "scaffolding", dir: "/ext", file: "ext-all.js"])}" type="text/javascript"></script>
            <script src="${g.resource([plugin: "scaffolding", dir: "/ext/locale", file: "ext-lang-zh_CN.js"])}" type="text/javascript"></script>
            <script src="${g.resource([plugin: "scaffolding", dir: "/js", file: "WinTip.js"])}" type="text/javascript"></script>
            <script src="${g.resource([plugin: "scaffolding", dir: "/js/core", file: "BackSpacePrevent.js"])}" type="text/javascript"></script>
            <script src="${g.resource([plugin: "scaffolding", dir: "/js/core", file: "EventCore.js"])}" type="text/javascript"></script>
            <script src="${g.resource([plugin: "scaffolding", dir: "/js/multiUpload", file: "swfobject.js"])}" type="text/javascript"></script>
            <script src="${g.resource([plugin: "scaffolding", dir: "/js", file: "/SessionCheck.js"])}" type="text/javascript"></script>
	        <script src="${g.resource([plugin: "scaffolding", dir: "/js", file: "/util.js"])}" type="text/javascript"></script>"""
    }

	/**
	 * 导入登陆界面资源
	 */
	def loginCssResource = { attrs, body ->

		out <<"""
            <link href="${g.resource([plugin: "scaffolding", dir: "/login/css", file: "bootstrap.css"])}" rel="stylesheet" type="text/css" />
            <link href="${g.resource([plugin: "scaffolding", dir: "/login/css", file: "font-awesome.css"])}" rel="stylesheet" type="text/css" />
            <link href="${g.resource([plugin: "scaffolding", dir: "/login/css", file: "style.css"])}" rel="stylesheet" type="text/css" />
            <link href="${g.resource([plugin: "scaffolding", dir: "/login/css", file: "style_responsive.css"])}" rel="stylesheet" type="text/css" />
            <link href="${g.resource([plugin: "scaffolding", dir: "/login/css", file: "style_default.css"])}" rel="stylesheet" type="text/css" />"""
	}


	/**
	 * 生成默认的'Ext.Viewport的border布局
	 * @param attrs.model 关联的domain class名称
	 * @param attrs.layout 布局类型，参加extjs
	 * @param attrs.associated 关联的domain class对象
	 * @param attrs.extra 附件变量标志，通常无需配置
	 * @param attrs.config.gridName 关联的Grid名称
	 * @return Ext.Viewport的border布局视图
	 */
    def extLayout = { attrs, body ->

        int dataBind=attrs.dataBind?:0
        String model=attrs.model
        String layout=attrs.layout
        String associated=attrs.associated
        String extra = attrs.extra?:""
        //centerWidth表示的是region center的宽度,即左边，eastWidth表示的是region east的宽度，即右边。默认都是50%
        String centerWidth = attrs.centerWidth?:"50%"
        String eastWidth = attrs.eastWidth?:"50%"
        Map domain=ModelService.GetModel(model)
        String propertyName=domain.propertyName
        String gridName=(attrs.config?.gridName)?:("${propertyName}${extra}Grid")
        String formName=(attrs.config?.gridName)?:"${propertyName}${extra}Form"
        String controller = attrs.controller ?: controllerName
        String storeName=(attrs.config?.storeName)?:"${propertyName}${extra}Store"
        String output=""
        if(layout=="oneToManyLeftRight")
        {
            output="""
        //使用border布局
        Ext.create('Ext.Viewport', {
            layout: 'border',
            items: [{
                region: 'north',
                border:false,
                items: toolbar,
                height:50
            },{
                region: 'center',
                width:'${centerWidth}',
                border:false,
                margins: '0 50 0 0',
                items: ${gridName}

            },{
                region: 'east', //west
                width: '${eastWidth}',
                border:false,
                items: ${associated}DownGrid

            }]
        });"""
        }
        else if(layout=="oneToManyUpDown")
        {
            output="""
        //使用border布局
        Ext.create('Ext.Viewport', {
            layout: 'border',
            items: [{
                region: 'north',
                border:false,
                items: toolbar,
                height:50
            },{
                region: 'center',
                border:false,
                items: ${gridName}
            },{
                region: 'south', //west
                width: '50%',
                //split: true,
                border:false,
                items: ${associated}DownGrid
            }]
        });"""
        }else if(layout=="dataBindingWithRightImg") {
            output="""
        Ext.create('Ext.Viewport', {
            layout: 'border',
            items: [{
                region: 'north',
                border: false,
                items : toolbar,
                height:50
            },{
                region: 'center',
                border: false,
                layout:{type:'vbox',
                        align : 'stretch',
                        pack  : 'start',},
                items : [
                        
                
                        
                
                        Ext.create('Ext.panel.Panel', {
                            flex:12,
                            items: ${gridName}
                        }),
                            Ext.create('Ext.panel.Panel', {
                            flex:7,
                            autoScroll:true,
                            items:${propertyName}${extra}DetailBindForm
                            }),
                            
                                Ext.create('Ext.PagingToolbar', {
                                flex:1,
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
                        })
                    ]
                
            }]
        });
        ${gridName}.getSelectionModel().on('selectionchange', function(sm, selection) {
        
            ${propertyName}DetailBindForm.getForm().reset();
    
    
            ${propertyName}DetailBindForm.getForm().load({
                params: { id: selection[0].data.id},
                url: '${g.createLink([controller: controller, action: "detailAction"])} ',
                autoLoad: true,
                success:function(form,action){
    
                },
                failure: function(form, action) { //失败返回：{ success: false, errorMessage:"指定的数据不存在" }
                    Ext.MessageBox.show({
                        title: '数据加载失败',
                        msg: action.result.errorMessage,
                        buttons: Ext.MessageBox.OK
                    });
                }
            });
        
        });
            """
        }else if(layout=="oneToManyTreeView") { //添加oneToManyTreeView组件by lichb 2014-6-4
            output = """
                //使用border布局
                Ext.create('Ext.Viewport', {
                    layout: 'border',
                    items: [{
                        region: 'north',
                        autoScroll: true,
                        border:false,
                        items: toolbar,
                        height:50
                    },{
                        region: 'center',
                        border:false,
                        items: ${gridName}
                    },{
                        region: 'west',
                        border:false,
                        items: ${associated}Tree
                    }]
                });
            """
        }else if(layout=="formTreeView"){ //添加formTreeView组件by linch 2014-9-16
            output="""
                Ext.create('Ext.Viewport', {
                    layout: 'border',
                    items: [{
                        region: 'north',
                        autoScroll: true,
                        border:false,
                        items: toolbar,
                        height:50
                    },{
                        region: 'center',
                        border:false,
                        items: ${formName}
                    },{
                        region: 'west',
                        border:false,
                        items: ${associated}Tree
                    }]
                });
            """
        }else if(layout=="formView"){ //添加formView组件by linchh 2014-9-17
            output="""
                Ext.create('Ext.Viewport', {
                    layout: 'border',
                    items: [{
                        region: 'north',
                        autoScroll: true,
                        border:false,
                        items: toolbar,
                        height:50
                    },{
                        region: 'center',
                        border:false,
                        items: ${formName}
                    }]
                });
            """
        }else{
            output = """
        //使用border布局
        Ext.create('Ext.Viewport', {
            layout: 'border',
            items: [{
                region: 'north',
                border:false,
                items: toolbar,
                height:50
            },{
                region: 'center',
                border:false,
                items: ${gridName}
            }]
        });
    """
        }
        out << output
    }

	/**
	 * 左侧屏幕导航菜单
	 */
	def extNavigation = { attrs, body ->

		//系统预定义的所有菜单组

        List<Map> groupsFull = authService.getRootMenu()
        Map<String, List> itemsFull = [:]
        groupsFull.each {
            itemsFull.put(it.code,authService.getItems(it.code))
        }


        //用户可以展现的菜单组
		List<Map> groups = []
		Map<String, List> items = [:]

		//对预定义所有菜单组进行循环，找出允许显示的菜单项
		itemsFull.each{String key, List<Map<String, String>> value->
			List menuItems=[]
			for(int i=0;i<value.size();i++){
                if(authService.Menu(session["sysRoleId"], "${value[i].controller}_${value[i].action}")){
                    menuItems << value[i]
                }
			}

			//对于包含多余1项的菜单组予以显示
			if(menuItems){
				items[key]=menuItems
			}
		}


		Set<String> allowedGroupCodes = items.keySet()
		//生成用户菜单组
		for(int i=0;i<groupsFull.size();i++)
		{
			if(allowedGroupCodes.contains(groupsFull[i].code)){
				groups << groupsFull[i]
			}
		}

		//构造Ext Accordian菜单
		String output = ""

		output = output + """
                    {
                        region: 'west',
                        stateId: 'navigation_panel',
                        id: 'nd_menu',
                        title: '<center>导航菜单</center>',
                        split: true,
                        width: 180,
                        minWidth: 180,
                        maxWidth: 340,
                        collapsible: true,
                        margins: '0 0 0 5',
                        layout: 'accordion',
                        items: ["""


		for(int i=0;i<groups.size();i++){

			List<Map> groupItems = items[groups[i].code]

			output = output +"""
                        {
                            id: '${groups[i].code}',
                            cls: 'images-view',
                            title: '<center>${groups[i].label}</center>',
                            autoScroll: true,
                            items: Ext.create('Ext.view.View', {
                                store: Ext.create('Ext.data.Store', {
                                    fields: ['index','caption','icon','url'],
                                    data: ["""
			for(int j=0; j< groupItems.size(); j++){
				output = output +"""
                    {'caption':'${groupItems[j].label}', 'index':'${groupItems[j].id}', 'icon': '${g.resource(dir: "${groupItems[j].dir}", file: "${groupItems[j].img}")}', 'url':'${g.createLink(controller: groupItems[j].controller, action: groupItems[j].action)}'}${j< (groupItems.size()-1)?",":""}"""
			}

			output= output + """
                                    ]
                                }),
                                tpl: menuTemplate,
                                multiSelect: false,
                                height: 310,
                                trackOver: true,
                                overItemCls: 'x-item-over',
                                itemSelector: 'div.thumb-wrap',
                                listeners: {
                                    itemclick: function (self, record, opts) {
                                        var id = record.id;
                                        if(record.data.index && record.data.index!='null'){
                                            id=record.data.index;
                                        }
                                        menuItemClick(id,record.data.caption,record.data.url);
                                    }
                                }
                            })
                        }${i<(groups.size()-1)?",":""}"""
		}

		output = output + """
                    ]
                },
"""
		out << output
	}

    String createComplexWnd(String model,List fields,List layout,String mode){

        Map fieldsMap = new HashMap()
        fields.each{
            fieldsMap.put(it.name,it)
        }
        String output=""
        int i = 0;
        layout.each {
            if (it instanceof Map){
                String title = it.title
                boolean collapsible = it.collapsible
                boolean collapsed = it.collapsed?:false
                boolean  border = it.border!=null?it.border:true
                output+="""
                    {
                        xtype:'fieldset',
                       title:'${title}',
                       checkboxToggle:${border},
                       defaultType:'textfield',
                       width:740,
                       border:${border},
                       collapsible:${collapsible},
                       collapsed:${collapsed},
                       defaults:{
                            anchor:'100%',
                            defaults: {msgTarget: 'side'},
                            flex:1
                       },
                       items:["""
                int j = 0
                it.layout.each{List row->
                    output+=createRowString(row,model,fieldsMap,mode)
                    if (j++ < it.layout.size()-1){
                        output += ","
                    }
                }
                output+="""
                    ],
                    listeners:{
                        collapse:function(self, opts){
                            ${model}${mode}Wnd.center();
                        },
                        expand:function(self, opts){
                            ${model}${mode}Wnd.center();
                        }
                    }
                }
                """

            }else if(it instanceof List){
                output+=createRowString(it,model,fieldsMap,mode)
            }

            if (i++ < layout.size()-1){
                output += ","
            }
        }
        return output

    }



    String  createRowString(List row,String model,Map fieldsMap,String mode){

        String output=""
        if (row.size()>1){
            output+="""
                        {
                            xtype:'fieldcontainer',
                            combineErrors: true,
                            defaultType:'textfield',
                            layout:{
                                type:'hbox',
                                defaultMargins: {top: 0, right: 15, bottom: 0, left: 0}
                            },
                            defaults:{
                                msgTarget: 'side',
                                flex:1
                            },
                            items:[

                    """
            int i = 0
            int size = row.size()
            row.each {String field->
                if (fieldsMap.containsKey(field)){
                    output+="\r\n"+Scaffolding(fieldsMap.get(field), mode, model) + "${i++<(size-1)?",":""}"
                }else{
                    size--
                }

            }
            output+="""]}"""
        }else{
            if (fieldsMap.containsKey(row[0])){
                output = output +"\r\n"+Scaffolding(fieldsMap.get(row[0]), mode, model)
            }
        }
        return output
    }


    def gridColumnStr(List<Map> fields,String model){
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
                        listVal += """ '${constraint.items[k].value}':'${constraint.items[k].label}'${k<(constraint.items.size()-1) ?",":"}"}"""
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
                        if(value != null){
                            return value.name;
                        }
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
        return columnsString;
    }


	/**
	 *
	 * [name:strWithWidgetTextarea, cn:文本框字符串, type:string, widget:textarea, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:6, minSize:2, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:strWithWidgetHtmleditor, cn:富文本字符串, type:string, widget:htmleditor, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:6, minSize:2, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:strWithSize, cn:长度约束字符串, type:string, widget:null, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:6, minSize:2, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:strWithNullable, cn:非空字符串, type:string, widget:null, constraint:[nullable:true, blank:true, inList:null, inListLabel:null, maxSize:null, minSize:null, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:strWithDefault, cn:字符串默认值, type:string, widget:null, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:6, minSize:2, matches:null, mobile:null, email:null, default:默认, unique:null]],
	 * [name:strWithBlank, cn:空约束字符串, type:string, widget:null, constraint:[nullable:false, blank:false, inList:null, inListLabel:null, maxSize:null, minSize:null, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:strUnique, cn:唯一字符串, type:string, widget:null, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:6, minSize:2, matches:null, mobile:null, email:null, default:null, unique:true]],
	 * [name:strRegex, cn:正则字符串, type:string, widget:null, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:null, minSize:null, matches:^[abc]$, mobile:null, email:null, default:null, unique:null]],
	 * [name:strMobile, cn:手机号, type:string, widget:null, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:null, minSize:null, matches:null, mobile:true, email:null, default:null, unique:null]],
	 * [name:strInListWithAbbr, cn:带缩写字符串, type:string, widget:null, constraint:[nullable:false, blank:true, inList:[1, 2, 3], inListLabel:[列表1, 列表2, 列表3], maxSize:null, minSize:null, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:strInList, cn:列表字符串, type:string, widget:null, constraint:[nullable:false, blank:true, inList:[列表1, 列表2, 列表3], inListLabel:null, maxSize:null, minSize:null, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:strEmail, cn:邮件, type:string, widget:null, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:null, minSize:null, matches:null, mobile:null, email:true, default:null, unique:null]],
	 * [name:str, cn:字符串, type:string, widget:null, constraint:[nullable:false, blank:true, inList:null, inListLabel:null, maxSize:6, minSize:2, matches:null, mobile:null, email:null, default:null, unique:null]],
	 * [name:numberLongWithMinMax, cn:区间长整数, type:long, widget:null, constraint:[max:256, min:0, default:null]], [name:numberLongWithDefault, cn:长整数默认值, type:long, widget:null, constraint:[max:null, min:null, default:0]],
	 * [name:numberLong, cn:长整数, type:long, widget:null, constraint:[max:null, min:null, default:null]], [name:numberIntWithMinMax, cn:区间整数, type:int, widget:null, constraint:[max:256, min:0, default:null]],
	 * [name:numberIntWithDefault, cn:整数默认值, type:int, widget:null, constraint:[max:null, min:null, default:0]], [name:numberInt, cn:整数, type:int, widget:null, constraint:[max:null, min:null, default:null]],
	 * [name:numberFloatWithMinMax, cn:区间浮点数, type:float, widget:null, constraint:[max:256.0, min:0.0, default:null, factor:null]],
	 * [name:numberFloatWidgetProgress, cn:进度条浮点数, type:float, widget:progress, constraint:[max:null, min:null, default:null, factor:1000.0]],
	 * [name:numberFloat, cn:浮点数, type:float, widget:null, constraint:[max:null, min:null, default:null, factor:null]],
	 * [name:numberDoubleWithMinMax, cn:区间双精度数, type:double, widget:null, constraint:[max:256.0, min:0.0, default:null]],
	 * [name:numberDoubleWithDefault, cn:双精度数默认值, type:double, widget:null, constraint:[max:null, min:null, default:0.0]],
	 * [name:numberDouble, cn:双精度数, type:double, widget:null, constraint:[max:null, min:null, default:null]],
	 * [name:nubmerFloatWithDefault, cn:浮点数默认值, type:float, widget:null, constraint:[max:null, min:null, default:0.0, factor:null]],
	 * [name:dateWithSecond, cn:日期(精确到秒), type:date, widget:null, constraint:[nullable:false]],
	 * [name:dateWithRange, cn:日期范围, type:date, widget:null, constraint:[nullable:false]],
	 * [name:dateWithHour, cn:日期(精确到小时), type:date, widget:null, constraint:[nullable:false]],
	 * [name:dateWithDefault, cn:日期默认值, type:date, widget:null, constraint:[nullable:false]],
	 * [name:dateLThan, cn:日期小于, type:date, widget:null, constraint:[nullable:false]],
	 * [name:dateGTThan, cn:日期大于, type:date, widget:null, constraint:[nullable:true]],
	 * [name:date, cn:日期, type:date, widget:null, constraint:[nullable:false]],
	 * [name:boolWithDefault, cn:布尔型默认值, type:boolean, widget:null, constraint:[default:true]],
	 * [name:bool, cn:布尔型, type:boolean, widget:null, constraint:[default:null]]]]
	 * @param field
	 * @param mode
	 * @return
	 */
	String Scaffolding(Map field, String mode, String domain){

		String output = ""
		if(field.scaffolding){ //用户自定义组件
			output = grailsApplication.mainContext.getBean("${field.scaffolding}").Scaffolding(field, mode, domain)
		}else if(field.widget == "area"){ //区域组件
			output = grailsApplication.mainContext.getBean("scaffoldingAreaService").Scaffolding(field, mode, domain)
		}else{ //其他类型组件
			try{
				output = grailsApplication.mainContext.getBean("scaffolding${field.type.capitalize()}Service").Scaffolding(field, mode, domain)
			}catch(NoSuchBeanDefinitionException noBean){ // bean未定义
				output = scaffoldingAssociationDefaultService.Scaffolding(field, mode, domain)
			}catch(Exception e){
                log.error(e.getMessage())
			}
		}

		return output
	}

    /**
     * @author lch
     * @version 2014/9/23
     * 生成默认的extjs form表单,表单的结构为单列或者双列由表单的属性所需要占用的页面高度决定。
     * @param attrs.model 模型名称，类似: student, company
     * @param attrs.controller 数据提交的服务端控制器
     * @param attrs.associated 如果为Formtree类型，form数据重置时需要传入对应的树节点名字
     * @param attrs.excluded 不显示在单选列表中的domain class属性
     * @return 窗口实体web代码
     */

    def extForm = { attrs, body ->

        String model=attrs.model
        String extra = attrs.extra?:""
        Long height = attrs.height?:26
        //作为向后端请求数据时查询的id，如果为FormTree形式，则传对应的树节点id。如果为Form形式则默认传1,因为配置类型在数据库中是唯一的
        String id = attrs.associated?(attrs.associated+"TreeNode"):1


        Map domain=ModelService.GetModel(model)
        String propertyName=domain.propertyName



        List<String> excluded = attrs.excluded
        String controller = attrs.controller ?: controllerName





        String heightString

        if(height)
        {
            heightString="""parseInt(document.documentElement.clientHeight-50)*${height};"""
        }else{
            heightString="""document.documentElement.clientHeight-50;"""
        }
        List<Map> fields = []
        //计算所有需要显示的属性需要使用的高度，默认一行高度26px
        Long totalHeight = 50
        if (excluded) {
            for (int i = 0; i < domain.transFields.size(); i++) {
                if (!excluded.contains(domain.transFields[i].name)) {
                    fields << domain.transFields[i]
                    //先将所有需要展现的属性高度都默认设为26，即一个输入框的高度
                    totalHeight += height
                }
            }
        } else {
            fields = domain.transFields
            //先将所有需要展现的属性高度都默认设为26，即一个输入框的高度
            totalHeight = fields.size() * height

        }
        List<String> specialFields = []
        for (int i = 0; i < fields.size(); i++) {
            if(fields[i].widget == "checkbox" && fields[i].constraint.domain != null){
                specialFields << i
                //特殊处理类似特长，意向服务这类的高度无法确定的属性，在页面上的高度，先暂时设为144，减去26是扣掉上面默认统一设为26的高度
                totalHeight += (144-height)
            }
            if(fields[i].widget == "htmleditor" ){
                specialFields << i
                //特殊处理富文本编辑框
                totalHeight += (191-height)
            }
            if(fields[i].widget == "textareafield" ){
                specialFields << i
                //特殊处理文本框字符串这样的
                totalHeight += (77-height)
            }

        }


        String output ="""
        //计算Form高度
        if(document.documentElement && document.documentElement.clientHeight){
            ${propertyName}${extra}FormHeight = ${heightString}
        }
        var totalHeight = ${totalHeight}
        //动态决定form显示的类型为一列还是两列
        var items
        //这里去设置defaultType是为了处理textfield类型下无法显示多列表单的问题，只有container可以显示
        var defaultType
        if(totalHeight <= document.documentElement.clientHeight){
            //如果属性高度未超过一个页面将默认xtype类型定义为textfield
            defaultType = "textfield"
       items = [
          {xtype: 'hiddenfield', name: 'id'},"""

        List checkBoxFields = []
        for (int i = 0; i < fields.size(); i++) {
            if (fields[i].type == "boolean" && fields[i].constraint.isCheckbox) { //对boolean 类型的checkbox复选框，commit时要特殊处理
                checkBoxFields.push('"' + fields[i].name + '"')
            }
            output = output + "\r\n" + Scaffolding(fields[i], "update", model) + "${i < (fields.size() - 1) ? "," : ""}"
        }
        output = output + """
                    ]
        }else{ //如果超过一页的高度则分为两行来显示
            //如果属性高度超过一个页面将默认xtype类型定义为container方便下面分列

            defaultType = "container"
            items = [
            {
            layout:'column',
            items: [//共有2列，第一列
            { columnWidth: .5, layout: 'form', border: false, defaultType:"textfield", padding: '5 10 5 0',items: [
                    {xtype: 'hiddenfield', name: 'id'},"""

        for (int i = 0; i < fields.size(); i+=2) {
            if(specialFields.contains(i)){
                continue;
            }
            if (fields[i].type == "boolean" && fields[i].constraint.isCheckbox) { //对boolean 类型的checkbox复选框，commit时要特殊处理
                checkBoxFields.push('"' + fields[i].name + '"')
            }
            output = output + "\r\n" + Scaffolding(fields[i], "update", model) + "${i < (fields.size() - 1) ? "," : ""}"
        }
        output = output + """
                    ]
        },//共有2列，第二列
            { columnWidth: .5, layout: 'form', border: false, defaultType:"textfield", padding: '5 5 5 10',items: ["""
        for (int i = 1; i < fields.size(); i+=2) {
            if(specialFields.contains(i)){
                continue;
            }
            if (fields[i].type == "boolean" && fields[i].constraint.isCheckbox) { //对boolean 类型的checkbox复选框，commit时要特殊处理
                checkBoxFields.push('"' + fields[i].name + '"')
            }
            output = output + "\r\n" + Scaffolding(fields[i], "update", model) + "${i < (fields.size() - 1) ? "," : ""}"
        }
        output = output + """
                               ]
                        }
                ]
            }"""
        if(specialFields.size() != 0){   //特殊高度属性不参与分列，放到最下面一行
            output = output + """
            ,{layout: 'form', padding: '5 5 5 0',items:[
            """
            for (int i = 0; i < specialFields.size(); i++) {

                output = output + "\r\n" + Scaffolding(fields[specialFields[i]], "update", model) + "${i < (fields.size() - 1) ? "," : ""}"
            }
            output = output + """
            ]}
            """
        }
        output = output + """
        ]};
        ${propertyName}Form = Ext.widget({
                xtype: 'form',
                id: '${propertyName}Form',
                layout: 'form',
                autoScroll:true,
                height: ${propertyName}${extra}FormHeight,
                autoWidth: true,
                bodyPadding: '5 5 5 15',
                buttonAlign: 'center',
                defaultType: defaultType,
                border: false,
                items: items


        });




        Ext.getCmp('${propertyName}Form').getForm().reset();


        Ext.getCmp('${propertyName}Form').getForm().load({
            params: { id: ${id}},
            url: '${g.createLink([controller: controller, action: "detailAction"])} ',
            autoLoad: true,
            success:function(form,action){

            },
            failure: function(form, action) { //失败返回：{ success: false, errorMessage:"指定的数据不存在" }
                Ext.MessageBox.show({
                    title: '数据加载失败',
                    msg: action.result.errorMessage,
                    buttons: Ext.MessageBox.OK
                });
            }
        });
    """
        out << output
    }






}
