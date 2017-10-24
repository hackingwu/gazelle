package framework
/**
 * 生成ToolBar上导入、导出按钮的方法
 * @className ExtToolBarTagLib
 * @author Su sunbin
 * @version 2014-09-16 ExtTagLib中的生成ToolBar上导入、导出按钮的方法拆分至此
 */
class ExtImportAndExportButtonTagLib extends ExtTagLib{
//	static defaultEncodeAs = 'html'
	//static encodeAsForTags = [tagName: 'raw']

	/**
	 * 导入数据
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.controller 数据提交的服务端控制器
	 * @param attrs.action 数据提交的服务端action
	 * @param attrs.text 导入按钮的标题
	 * @return 导出处理代码
	 */
	def extImportButton = { attrs, body ->
		String model=attrs.model
		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName
		String controller = attrs.controller ?: propertyName
		String action= attrs.action ?: "importData"
        String controllerTpl = attrs.controllerTpl ?: "importInfo"
        String actionTpl = attrs.actionTpl ?: "exportTplFile"

        String text = attrs.text?:"导入"
        boolean importDisable= false //(!authService.Button(session["sysRoleId"], "${controller}_import"))

		String output = """
            {
                text: '${text}',
                disabled: ${importDisable},

                iconCls: 'icon-data-import',
                handler: function () {
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
                                    //仅支持.xls结尾的文件类型
                                    var file_reg = /\\.(xls|xlsx){1}\$/;
                                    if (!file_reg.test(v)) {
                                        Ext.Msg.alert('提示', '请选择以.xls或.xlsx结尾的文件！');
                                        this.setRawValue('');
                                        return;
                                    }
                                }
                            }
                        },{
                           xtype:"text",
                           height: 30,
                           html:'<a href="${g.createLink([controller: controllerTpl, action:actionTpl])}?clz=${propertyName}" style="text-decoration:none;">下载导入模板</a>',
                           style:{
                                padding:'10px 0px 0px 0px'
                           }
                        }],
                        buttons: [{
                            text: '导入',
                            handler: function(){
                                var _self = this;
                                var form = _self.up('form').getForm();
                                if(form.isValid()){
                                    form.submit({
                                        url: '${g.createLink([controller: controller, action: action])}',
                                        waitMsg: '数据导入中，请稍后......',
                                        success: function(fp, o) {
                                            Ext.Msg.show({
                                                title: "导入成功",
                                                msg: o.result.message,
                                                modal: true,
                                                icon: Ext.Msg.INFO,
                                                buttons: Ext.Msg.OK
                                            });

                                            _self.up('window').close();
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
            },
        """
		out << output
	}

	/**
	 * 导出数据
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.controller 导出的数据源控制器
	 * @param attrs.action 导出的数据源action
	 * @param attrs.text 导出按钮的标题
	 * @param attrs.config.limit 每次导出的最大数据条数
	 * @param attrs.config.start 每次导出的数据起始索引
	 * @return 导出处理代码
	 */
	def extExportButton = { attrs, body ->
		String model=attrs.model
		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName
		String controller = attrs.controller ?: propertyName
		String action= attrs.action ?: "exportData"
		String text = attrs.text?:"导出"
        String rowSel = attrs.rowsel?attrs.rowsel:"all"
		String limit = (attrs.config?.limit)?:"1000000"
		String start = (attrs.config?.start)?:"0"
        String gridName=(attrs.config?.gridName)?:"${propertyName}Grid"
        boolean exportDisable=false //(!authService.Button(session["sysRoleId"], "${controller}_export"))
        boolean synExport= attrs.synExport ?: false
        String output

        String externCode = ""
        if(rowSel=="single"){
            externCode = """
                var data = ${gridName}.getView().getSelectionModel().getSelection();
                if(data.length==0|| data.length>1){
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: '请选择一行数据导出！',
                        buttons: Ext.MessageBox.OK
                    });
                    return;
                }
          """
        }
        if (synExport) {
            output = """
            {
                text: '${text}',
                iconCls: 'icon-data-export',
                disabled: ${exportDisable},
                handler: function () {
                    var exParams = "[";
                    var key = toolbar.down('#searchKey')?toolbar.down('#searchKey').getValue():"";
                    if(key!="defaultSearchVal"){
                        var value = toolbar.down('#searchStringValue')?toolbar.down('#searchStringValue').getValue():"";
                        exParams = '[{"key":"'+key+'","value":"'+value+'"},';
                    }

                    try{
                        var startDate = Ext.getCmp('sdate').getValue();
                        var endDate = Ext.getCmp('edate').getValue();
                        var startDateFmt = startDate?(Ext.Date.format(startDate,'Y-m-d H:i:s')):"";
                        var endDateFmt = endDate?(Ext.Date.format(endDate,'Y-m-d H:i:s')):"";

                        exParams += '{"key":"startDate","value":"'+startDateFmt+'"}';
                        exParams += ',{"key":"endDate","value":"'+endDateFmt+'"}';
                    }catch(e){}

                    exParams += "]";

                    window.open ('${
                g.createLink([controller: controller, action: action]) + "?limit=${limit}&start=${start}&search="
            }'+exParams,'_self')
                }
            },
        """
        } else {
            output = """
            {
                text: '${text}',
                iconCls: 'icon-data-export',
                disabled: ${exportDisable},
                handler: function () {
                    ${externCode}
                    var search_params='{"key":"'+(toolbar.down('#searchKey')?toolbar.down('#searchKey').getValue():"")+'","value":"'+(toolbar.down('#'+searchValue)?toolbar.down('#'+searchValue).getValue():"")+'"}'
                    Ext.Ajax.request({
                        url: '${g.createLink([controller: controller, action: action])}',
                        params: { limit: ${limit},start:${start}${rowSel == "single" ? ",id:data[0].get('id')}" : "}"},
                        method : 'post',
                        success: function(response) {
                            if(!response.responseText || response.responseText=="") return; //超时处理
                            var result = Ext.decode(response.responseText);
                            if(result.success){
                                Ext.wintip.msg('导出成功!',  "请到导入导出管理页面下载导出结果!");
                            }else{
                                Ext.wintip.msg('导出失败!',  result.message);
                            }
                        },
                        failure: function() {
                            Ext.wintip.msg('请求超时!',  '网络超时!');
                        }
                    });
                }
            },
        """
        }

		out << output
	}
}
