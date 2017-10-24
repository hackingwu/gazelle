package framework




import java.util.regex.Pattern

/**
 * 生成Create,Detail,Update的对话框
 * @className ExtDialogTagLib
 * @author Su sunbin, Lch
 * @version 2014-09-16 ExtTagLib生成对话框的方法拆分至此
 * @version 2014-09-28 新增extFormUpdateWnd，extResetWnd提供给fromView类型的页面的窗口，提供一个支持权限的更新buttons和一个不支持权限的重置buttons
 */

class ExtDialogTagLib extends ExtTagLib{

	/**
	 * 生成默认的extjs Create对话框
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.excluded 不需要创建组件的domain 属性
	 * @param attrs.controller 数据提交的服务端控制器
	 * @param attrs.action 数据提交的服务器action
	 * @param attrs.extra 附件参数
	 * @param attrs.config 附件属性
	 * @return 窗口实体web代码
	 */
	def extCreateWnd = { attrs, body ->

		String model=attrs.model
		List<String> excluded = attrs.excluded
		String controller = attrs.controller ?: controllerName
		String action= attrs.action ?: "createAction"
		String extra = attrs.extra?:""
		Map domain=ModelService.GetModel(model)

		String propertyName=domain.propertyName
		String gridName=(attrs.config?.gridName)?:"${propertyName}${extra}Grid"

		String output ="""
    function ${propertyName}Create() {
        if (!${propertyName}CreateWnd) {

            ${propertyName}CreateForm = Ext.widget({
                xtype: 'form',
                id: '${propertyName}CreateForm',
                layout: 'form',
                autoScroll:true,
                width:750,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                defaults: {
                        msgTarget: 'side'
                },
                items: ["""
		List<Map> fields=[]

		if(excluded)
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

        List layout = grailsApplication.getArtefactByLogicalPropertyName("Domain", model).getPropertyValue("layout")

        if (layout){
            output+= createComplexWnd(model,fields,layout,'Create')
        }else{
            for(int i=0;i<fields.size();i++)
            {
                output = output +"\r\n"+Scaffolding(fields[i], "create", model) + "${i<(fields.size()-1)?",":""}"
            }
        }

		String treeView = ""
        String submitFunc = ""
		if(domain.m.layout.type == "oneToManyTreeView"){
            submitFunc = """
                ${domain.m.layout.domain}Tree.getStore().reload();
            """
			treeView = """
                if(${domain.m.layout.domain}TreeNode){
                    var treeNodeValueField=${propertyName}CreateWnd.down('form').getForm().findField("${domain.m.layout.key}Value");
                    var treeNodeField=${propertyName}CreateWnd.down('form').getForm().findField("${domain.m.layout.key}");
                    if(treeNodeValueField){
                        treeNodeValueField.setValue(${domain.m.layout.domain}TreeText==""?${domain.m.layout.domain}Tree.getRootNode().data.text:${domain.m.layout.domain}TreeText);
                    }
                    if(treeNodeField){
                        treeNodeField.setValue(${domain.m.layout.domain}TreeNode);
                    }

                }
            """
		}
		output=output+"""
                    ],
                buttons: [
                    /*{
                        itemId: 'resetBtn',
                        text: '重置',
                        handler: function () {
                            this.up('form').getForm().reset();
                        }
                    },*/
                    {
                        text: '确定',
                        handler: function () {
                            var cmpForm = this.up('form').getForm();

                            if (cmpForm.isValid()) {

                                if(typeof(submitOnCreateWnd)=='function'){
                                    if(!submitOnCreateWnd()) return
                                }

                                cmpForm.submit({
                                    url: '${g.createLink([controller: controller, action: action])}', //params: {},
                                    success: function(form, response) {
                                        if(!response.result) return; //超时处理
                                        if(response.result.success){
                                            Ext.wintip.msg('操作成功!',  response.result.message);
                                            ${propertyName}CreateWnd.hide();
                                            ${gridName}.getStore().reload();
                                            ${submitFunc}
                                        }else{
                                            Ext.wintip.error('操作失败!',  response.result.message);
                                        }
                                    },
                                    failure: function(form, action) {
                                        Ext.wintip.error('操作失败!',  action.result.message);
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: '关闭',
                        handler: function () {
                            ${propertyName}CreateWnd.hide();
                        }
                    }
                ]
            });

            ${propertyName}CreateWnd = Ext.create('Ext.Window', {
                title: '新增${domain.m?.domain?.cn}',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [${propertyName}CreateForm],
                listeners:{
                    'afterrender': function(_self, opts){
                        if(typeof(onCreateWndInit) == 'function'){
                            onCreateWndInit(_self);
                        }
                    },
                    'show': function(_self, opts){
                        if(typeof(onCreateWndShow) == 'function'){
                            onCreateWndShow(_self);
                        }
                    }
                }
            });
        }
        ${propertyName}CreateWnd.down('form').form.reset();
        ${treeView}
        ${propertyName}CreateWnd.show();

        var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
        if(document.getElementById('${propertyName}CreateForm').scrollHeight>autoHeight){
            ${propertyName}CreateWnd.down('form').setHeight(autoHeight);
            ${propertyName}CreateWnd.down('form').setWidth(750);
            ${propertyName}CreateWnd.center();
        }
    }"""

		out << output
	}

	/**
	 * 生成默认的extjs Update对话框
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.excluded 不需要创建组件的domain 属性
	 * @param attrs.controller 数据提交的服务端控制器
	 * @param attrs.action 数据提交的服务器action
	 * @param attrs.extra 附件参数
	 * @param attrs.config 附件属性
	 * @return 窗口实体web代码
	 */
	def extUpdateWnd = { attrs, body ->

		String model=attrs.model
		List excluded = attrs.excluded
		String controller = attrs.controller ?: controllerName
		String action= attrs.action ?: "updateAction"
		String extra = attrs.extra?:""

		Map domain=ModelService.GetModel(model)

		String propertyName=domain.propertyName
		String gridName=(attrs.config?.gridName)?:"${propertyName}${extra}Grid"

		String output ="""
    function ${propertyName}Update() {
        var selection = ${gridName}.getView().getSelectionModel().getSelection();
        if(selection.length==0){
            Ext.MessageBox.show({
                title: '提示',
                msg: '请选择需要修改的${domain.m?.domain?.cn}！',
                buttons: Ext.MessageBox.OK
            });
            return;
        }

        if(selection.length>1){
            Ext.MessageBox.show({
                title: '提示',
                msg: '请选择一条${domain.m?.domain?.cn}记录进行修改！',
                buttons: Ext.MessageBox.OK
            });
            return;
        }

        if (!${propertyName}UpdateWnd) {
            ${propertyName}UpdateForm = Ext.widget({
                xtype: 'form',
                id: '${propertyName}UpdateForm',
                layout: 'form',
                autoScroll:true,
                width:750,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                defaults: {
                    msgTarget: 'side'
                },
                items: [
                    {xtype: 'hiddenfield', name: 'id'},"""

		List<Map> fields=[]

		if(excluded)
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

		List checkBoxFields = []
        List layout = grailsApplication.getArtefactByLogicalPropertyName("Domain", model).getPropertyValue("layout")

        if (layout){
            for(int i=0;i<fields.size();i++)
            {
                if(fields[i].type=="boolean" && fields[i].constraint.isCheckbox){ //对boolean 类型的checkbox复选框，commit时要特殊处理
                    checkBoxFields.push('"'+fields[i].name+'"')
                }
            }
            output+= createComplexWnd(model,fields,layout,'Update')
        }else {
            for(int i=0;i<fields.size();i++)
            {
                if(fields[i].type=="boolean" && fields[i].constraint.isCheckbox){ //对boolean 类型的checkbox复选框，commit时要特殊处理
                    checkBoxFields.push('"'+fields[i].name+'"')
                }
                output = output +"\r\n"+Scaffolding(fields[i], "update", model) + "${i<(fields.size()-1)?",":""}"
            }
        }
		output=output+"""
                    ],
                buttons: [
                    {
                        text: '确定',
                        handler: function () {
                            var cmpForm = this.up('form').getForm();
                            var params = {};
                            var fieldsArry = ${checkBoxFields};
                            for(var k=0;k<fieldsArry.length;k++){
                                params[fieldsArry[k]] = cmpForm.findField(fieldsArry[k]).getValue();
                            }

                            if (cmpForm.isValid()) {

                                if(typeof(submitOnUpdateWnd)=='function'){
                                    if(!submitOnUpdateWnd()) return
                                }

                                cmpForm.submit({
                                    url: '${g.createLink([controller: controller, action: action])}', params: params,
                                    success: function(form, response) {
                                        if(!response.result) return; //超时处理
                                        if(response.result.success){
                                            Ext.wintip.msg('操作成功!',  response.result.message);
                                            ${propertyName}UpdateWnd.hide();
                                            ${gridName}.getStore().reload();
                                        }else{
                                            Ext.wintip.error('操作失败!',  response.result.message);
                                        }
                                    },
                                    failure: function(form, action) {
                                        Ext.wintip.error('操作失败!',  action.result.message);
                                    }
                                });
                            }
                        }
                    },
                    {
                        text: '关闭',
                        handler: function () {
                            ${propertyName}UpdateWnd.hide();
                        }
                    }
                ]
            });

            ${propertyName}UpdateWnd = Ext.create('Ext.Window', {
                title: '${domain.m?.domain?.cn}编辑',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [${propertyName}UpdateForm],
                listeners:{
                    'afterrender': function(_self, opts){
                        if(typeof(onUpdateWndInit) == 'function'){
                            onUpdateWndInit();
                        }
                    },
                    'show': function(_self, opts){
                        if(typeof(onUpdateWndShow) == 'function'){
                            onUpdateWndShow();
                        }
                    }
                }
            });
        }

        ${propertyName}UpdateWnd.down('form').form.reset();

        ${propertyName}UpdateWnd.down('form').getForm().load({
            params: { id: selection[0].data.id},
            url: '${g.createLink([controller: controller, action: "detailAction"])} ',
            autoLoad: true,
            success:function(form,action){
                ${scaffoldingUtilService.getAllOne2OneFieldsScript('update', fields)}
                ${propertyName}UpdateWnd.show();

                var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
                if(document.getElementById('${propertyName}UpdateForm').scrollHeight>autoHeight){
                    ${propertyName}UpdateWnd.down('form').setHeight(autoHeight);
                    ${propertyName}UpdateWnd.down('form').setWidth(750);
                    ${propertyName}UpdateWnd.center();
                }
                if(typeof(onUpdateWndDataLoad) == 'function'){
                    onUpdateWndDataLoad();
                }
            },
            failure: function(form, action) { //失败返回：{ success: false, errorMessage:"指定的数据不存在" }
                if(!action.result) return; //超时处理
                Ext.MessageBox.show({
                    title: '数据加载失败',
                    msg: action.result.errorMessage,
                    buttons: Ext.MessageBox.OK
                });
            }
        });
    }"""

		out << output
	}

	def extDetailBindForm = {attrs, body ->
		String model=attrs.model
		String extra = attrs.extra?:""
		List excluded = attrs.excluded
		String controller = attrs.controller ?: controllerName
		String action= attrs.action ?: "detailAction"
//		String width= attrs.width?: "750"


		Map domain=ModelService.GetModel(model)

		String propertyName=domain.propertyName
		String gridName=(attrs.config?.gridName)?:"${propertyName}${extra}Grid"
		String output = """

		${propertyName}${extra}DetailBindForm = Ext.widget({
			xtype: 'form',
			id: '${propertyName}${extra}DetailForm',
			layout: 'form',
//			autoScroll:true,
			bodyPadding: '5 5 5 5',
			buttonAlign: 'center',
			defaultType: 'textfield',
			items: ["""
		List<Map> fields=[]

		if(excluded)
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
        List layout = grailsApplication.getArtefactByLogicalPropertyName("Domain", model).getPropertyValue("layout")

        if (layout){
            output+= createComplexWnd(model,fields,layout,'Detail')
        }else{
            for(int i=0;i<fields.size();i++)
            {
                output = output +"\r\n"+Scaffolding(fields[i], "detail", model) + "${i<(fields.size()-1)?",":""}"
            }
        }
		output=output+"""
			]
			});
		"""
		out << output
	}

	/**
	 * 生成默认的extjs Detail对话框
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.excluded 不需要创建组件的domain 属性
	 * @param attrs.controller 数据提交的服务端控制器
	 * @param attrs.action 数据提交的服务器action
	 * @param attrs.extra 附件参数
	 * @param attrs.width 窗口宽度
	 * @param attrs.config 附件属性
	 * @return 窗口实体web代码
	 */
	def extDetailWnd = { attrs, body ->

		String model=attrs.model
		String extra = attrs.extra?:""
		List excluded = attrs.excluded
		String controller = attrs.controller ?: controllerName
		String action= attrs.action ?: "detailAction"
		String width= attrs.width?: "750"


		Map domain=ModelService.GetModel(model)

		String propertyName=domain.propertyName
		String gridName=(attrs.config?.gridName)?:"${propertyName}${extra}Grid"

		String output ="""
    function ${propertyName}${extra}Detail() {
        var selection = ${gridName}.getView().getSelectionModel().getSelection();
        if(selection.length==0){
            Ext.MessageBox.show({
                title: '提示',
                msg: '请选择需要查看的${domain.m?.domain?.cn}！',
                buttons: Ext.MessageBox.OK
            });
            return;
        }

        if(selection.length>1){
            Ext.MessageBox.show({
                title: '提示',
                msg: '请选择一条${domain.m?.domain?.cn}记录进行查看！',
                buttons: Ext.MessageBox.OK
            });
            return;
        }


        if (!${propertyName}${extra}DetailWnd) {			
            ${propertyName}${extra}DetailForm = Ext.widget({
                xtype: 'form',
                id: '${propertyName}${extra}DetailForm',
                layout: 'form',
                autoScroll:true,
                width:${width},
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                items: ["""
		List<Map> fields=[]

		if(excluded)
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
        List layout = grailsApplication.getArtefactByLogicalPropertyName("Domain", model).getPropertyValue("layout")

        if (layout){
            output+= createComplexWnd(model,fields,layout,'Detail')
        }else{
            for(int i=0;i<fields.size();i++)
            {
                output = output +"\r\n"+Scaffolding(fields[i], "detail", model) + "${i<(fields.size()-1)?",":""}"
            }
        }
		output=output+"""
                    ],
                buttons: [
                    {
                        text: '关闭',
                        handler: function () {
                            ${propertyName}${extra}DetailWnd.hide();
                        }
                    }
                ]
            });
		
            ${propertyName}${extra}DetailWnd = Ext.create('Ext.Window', {
                title: '${domain.m?.domain?.cn}明细',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [${propertyName}${extra}DetailForm],
                listeners:{
                    'afterrender': function(_self, opts){
                        if(typeof(onDetailWndInit) == 'function'){
                            onDetailWndInit();
                        }
                    },
                    'show': function(_self, opts){
                        if(typeof(onDetailWndShow) == 'function'){
                            onDetailWndShow();
                        }
                    }
                }
            });
        }

        ${propertyName}DetailWnd.down('form').form.reset();

        ${propertyName}${extra}DetailWnd.down('form').getForm().load({
            params: { id: selection[0].data.id},
            url: '${g.createLink([controller: controller, action: action])} ',
            autoLoad: true,
            success:function(form,action){
                ${scaffoldingUtilService.getAllOne2OneFieldsScript('detail', fields)}

                ${propertyName}${extra}DetailWnd.show();

                var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
                if(document.getElementById('${propertyName}${extra}DetailForm').scrollHeight>autoHeight){
                    ${propertyName}${extra}DetailWnd.down('form').setHeight(autoHeight);
                    ${propertyName}${extra}DetailWnd.down('form').setWidth(750);
                    ${propertyName}${extra}DetailWnd.center();
                }
				if(typeof(onDetailWndDataLoad) == 'function'){
                            onDetailWndDataLoad();
                }
            },
            failure: function(form, action) { //失败返回：{ success: false, errorMessage:"指定的数据不存在" }
                if(!action.result) return; //超时处理
                Ext.MessageBox.show({
                    title: '数据加载失败',
                    msg: action.result.errorMessage,
                    buttons: Ext.MessageBox.OK
                });
            }
        });
    }"""
		out << output
	}

    /**
     * @author lch
     * @version 2014/9/18
     * 生成默认的extjs form数据重置
     * @param attrs.model 模型名称，类似: student, company
     * @param attrs.controller 数据提交的服务端控制器
     * @param attrs.associated 如果为Formtree类型，form数据重置时需要传入对应的树节点名字
     * @return 窗口实体web代码
     */
    def extResetWnd = { attrs, body ->
        String model = attrs.model
        String controller = attrs.controller ?: controllerName
        String id = attrs.associated?(attrs.associated+"TreeNode"):1

        Map domain = ModelService.GetModel(model)

        String propertyName = domain.propertyName

        String output = """
             function  ${propertyName}reset(){
                        var cmpForm = Ext.getCmp('${propertyName}Form').getForm();
                        cmpForm.load({
                            params: { id: ${id} },
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
             }
        """
        out << output
    }

    /**
     * @author lch
     * @version 2014/9/18
     * 生成默认的extjs 修改form数据对话框
     * @param attrs.model 模型名称，类似: student, company
     * @param attrs.controller 数据提交的服务端控制器
     * @return 窗口实体web代码
     */
    def extFormUpdateWnd = { attrs, body ->
        String model = attrs.model
        String controller = attrs.controller ?: controllerName

        Map domain = ModelService.GetModel(model)

        String propertyName = domain.propertyName

        String output = """
        function ${propertyName}FormUpdate(){
            Ext.MessageBox.confirm('确认', '提交的系统配置信息的记录?',function(sel){
                if(sel == "yes"){
                    var cmpForm = Ext.getCmp('${propertyName}Form').getForm();
                    var params = {};
                    var fieldsArry = [];
                    for(var k=0;k<fieldsArry.length;k++){
                        params[fieldsArry[k]] = cmpForm.findField(fieldsArry[k]).getValue();
                    }
                    if (cmpForm.isValid()) {
                        cmpForm.submit({
                            url: '${g.createLink([controller: controller, action: "updateAction"])} ',
                            params: params,
                            success: function (form, response) {
                                if(!response.result) return; //超时处理
                                if (response.result.success) {
                                    Ext.wintip.msg('操作成功!', response.result.message);

                                } else {
                                    Ext.wintip.error('操作失败!', response.result.message);
                                }
                            },
                            failure: function (form, action) {
                                Ext.wintip.error('操作失败!', action.result.message);
                            }
                        });
                    }else{
                        Ext.MessageBox.show({
                            title: '提示',
                            msg: '您的输入不符合要求！',
                            buttons: Ext.MessageBox.OK
                        });
                    }
                }
            });
        }
        """
        out << output
    }




}
