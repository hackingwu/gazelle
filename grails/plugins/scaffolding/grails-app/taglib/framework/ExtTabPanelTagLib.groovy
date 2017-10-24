package framework

import grails.converters.JSON

import java.util.regex.Pattern

class ExtTabPanelTagLib extends ExtTagLib{
//    static defaultEncodeAs = 'html'
    //static encodeAsForTags = [tagName: 'raw']

    /**
     * [model:model,tabs:[[type:"grid|form",attrs:[:]]]]
     */
    def extTabPanel = {attrs,body->

        String model = attrs.model
        Map domain=ModelService.GetModel(model)
        String controller = attrs.controller ?: model
        String action = attrs.action ?: "detailAction"
        String title = attrs.title ?: "name"
        String windowName = attrs.windowName ?: "${model}UpdateWnd"
        attrs.tabs.each{
            it.attrs.put("parentModel",model)
        }
        //todo 假定一定会有form，有空要拆分
        String output = """
            var selection = ${model}Grid.getSelectionModel().getSelection();
            if(selection.length<1){
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '请先选择一条${domain.m.domain.cn}记录！',
                    buttons: Ext.MessageBox.OK
                });
                return;
            }
            if(selection.length>1){
                Ext.MessageBox.show({
                    title: '提示',
                    msg: '最多只能选择一条${domain.m.domain.cn}记录！',
                    buttons: Ext.MessageBox.OK
                });
                return;
            }
            var ${model}Name = selection[0].data.${title};
            var tabPanel = Ext.create('Ext.tab.Panel',{

                height:'100%',
                width:'100%',
                border:false,
                tabPosition: 'top',
                listeners:{
                    tabchange: function(tab,newCard,oldCard,opts){
                        try{
                            if(newCard.xtype === "grid") Ext.getCmp(newCard.id).getStore().reload();
                            else if(newCard.xtype === "from") newCard.getForm().load({
                                params:{id:selection[0].data.id},
                                url:'${g.createLink([controller: controller,action: action])}',
                                autoLoad:true
                            })
                        }catch(e){}
                    },
                    afterrender: function(_self, opts){
                        if(typeof(onTabPanelInit) == 'function'){
                            onTabPanelInit(_self);
                        }
                    }
                },
                items: [${tabs(attrs.tabs)}]
            });

            tabPanel.down('form[id="${attrs.tabs.find{it.type=="form"}.attrs.id}"]').getForm().load({
                    params: { id: selection[0].data.id},
                    url: '${g.createLink([controller: controller, action:action])}',
                    autoLoad: true,
                    success:function(form,action){
                         ${windowName}= Ext.create('Ext.Window', {
                                        title: '${domain.m.domain.cn}（'+Ext.String.htmlEncode(${model}Name)+'）的详细信息',
                                        width: 750,
                                        modal: true,
                                        resizable: false,
                                        buttonAlign: 'center',
                                        items: [
                                            tabPanel
                                        ],
                                        buttons: [
                                            {text: '关闭',handler: function(){this.up('window').close();}}
                                        ],
                                        listeners: {
                                            close:function(){${model}Grid.getStore().reload();curImgId=0;}
                                        }
                                    });
                          ${windowName}.show();
                            if(typeof(tabPanelForm) == 'function'){
                                tabPanelForm(form,action)
                            }
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

    String tabs(List<Map> tabs){
        String output = "";
        tabs.eachWithIndex{Map tab,int i->
            if (tab.type == "grid"){
                output += grid(tab.attrs)
            }else if(tab.type == "form"){
                output += form(tab.attrs)
            }else if(tab.type == "imagePanel"){
                output += imagePanel(tab.attrs)
            }
            if (i <tabs.size()-1) output += ","
        }
        return output
    }

    /**
     *[title:title,id:id(grid的id),columns:[header:,flex:,sortable:,dataIndex:,renderer:]（弃用）,dataIndex，columnIndexs:[](支持正则)]
     * @param attrs
     * @return
     */
    String grid(Map attrs){
        String title = attrs.title
        String id   = attrs.id
        String gridStoreId = attrs.gridStoreId ?: id+"Store"
        String controller = attrs.controller
        String action = attrs.action
        String model = attrs.model
        String parentModel = attrs.parentModel
        Map domain=ModelService.GetModel(model)
        List<Map> dockedItems = attrs.dockedItems
        Map search = attrs.search //[domain:"",included:""]
        List<Map> columns  = attrs.columns
        String foreignerKey = attrs.foreignerKey ?: parentModel
        Map crudItems = attrs.crudItems

        List columnIndexs = attrs.columnIndexs
        List storeFields = attrs.storeFields ?: columnIndexs
        List<Map> fields=[]
        for(int i=0; i<domain.transFields.size();i++)
        {
            for(int j=0;j<columnIndexs.size();j++){
                Pattern pattern = Pattern.compile(columnIndexs[j])
                if(pattern.matcher(domain.transFields[i].name).matches()){
                    fields << domain.transFields[i]
                }
            }
        }
        String output = """
           {
                xtype:'grid',
                title:'${title}',
                id   :'${id}',
                autoWidth:true,
                border: false,
                height:((_height=document.documentElement.clientHeight*0.7)<450?_height:450),
                selModel: Ext.create('Ext.selection.CheckboxModel',{mode:'MULTI'}),
                columns: [${gridColumnStr(fields,model)}],
                store: Ext.create('Ext.data.Store', {
                    storeId: '${gridStoreId}',
                    remoteSort: true,
                    pageSize:12,
                    fields: ${storeFields as JSON},
                    proxy:{
                        type: 'ajax',
                        url: '${g.createLink([controller: controller, action:action])}',
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

                            var selection = ${parentModel}Grid.getSelectionModel().getSelection();
                            var search_params= ""
                            var searchParams=new Array();
                            if(selection.length>0){
                                var searchParam = new Object();
                                searchParam.key = '${foreignerKey}';
                                searchParam.value = selection[0].data.id+'';
                                searchParams.push(searchParam);
                            }
                            //var searchValue = Ext.getCmp('${id}Search').getValue();
                            //var searchKey = Ext.getCmp('${id}Key').getValue();
                            ${search != null ? "searchParams.searchKey=searchKey;searchParams.searchValue=searchValue;" :""}

                            search_params=Ext.JSON.encode(searchParams);
                            var query_params={
                                search:  search_params
                            };
                            Ext.apply(this.proxy.extraParams, query_params);
                        }
                    }
                }),
                dockedItems:[{
                    xtype: 'toolbar',
                    dock: 'top',
                    items:[
                        ${crudItems == null ? "" : m.extCRUDButtons(crudItems)}
                        ${m.extOtherButtons([items: dockedItems])}
                        ${search !=null ?",'->'," + m.extSearchButtons([:]):""}
                    ]

                }]
                ,bbar: {
                            xtype: 'pagingtoolbar',
                            prevText:'',
                            nextText:'',
                            firstText:'',
                            lastText:'',
                            refreshText:'',
                            store: '${gridStoreId}',
                            displayInfo: true,
                            displayMsg: '显示 {0} - {1} 条，共 {2} 条',
                            emptyMsg: "没有数据"
                        }
                  ,listeners: {}
           }
        """
        return output
    }

    String gridColumns(List<Map> columns){
        String output = """
            ["""

        columns.eachWithIndex{Map column,int i$->
                String columnStr = "{header:'${column.header}',flex:${column.flex?:1},sortable:${column.sortable?:false},dataIndex:'${column.dataIndex}'"
                if (column.containsKey("renderer")&&column.renderer.size()>0){
                    Map renderer = column.renderer
                    columnStr += ",renderer:function(value){"
                        renderer.eachWithIndex { Map.Entry<String, String> entry, int i ->
                            if (i == 0){
                                columnStr += """
                                    if(value=="${entry.key}"){return "${entry.value}"}
                                """
                            }else{
                                columnStr += """
                                    else if(value=="${entry.key}"){return "${entry.value}"}
                                """
                            }
                        }


                        columnStr+="}"
                }
                columnStr+="}${i$ < columns.size() - 1 ?  ",":""}"
                output += columnStr
            }

        output+="""
            ]
        """
        return output
    }

    /**
     *required:[id,title]
     * @param attrs [id:"",title:"",excluded:[],model:"",grid:"",controller:"",action:""]
     * @return
     */
    String form(Map attrs){
        List<Map> fields=[]
        List excluded = attrs.excluded
        List included = attrs.included
        String model = attrs.model
        Map domain=ModelService.GetModel(model)
        String gridName = attrs.grid ?: model + "Grid"
        String controller = attrs.controller ?: model
        String action = attrs.action ?: "updateAction"
        boolean disabled = false
        String title = attrs.title ?: "基本信息"
        String id = attrs.id
        String btnTitle = attrs.btnTitle ?: "修改"
        String output = """
            {
                title:'${title}',
                xtype: 'form',
                id: '${id}',
                layout: 'form',
                autoScroll:true,
                height:((_height=document.documentElement.clientHeight*0.7)<450?_height:450),
                border:false,
//                width:750,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                defaults: {
                    msgTarget: 'side'
                },
                items: [
                    {xtype: 'hiddenfield', name: 'id'},
                """
        if(excluded)
        {
            for(int i=0; i<domain.transFields.size();i++)
            {
                boolean flag = false //是否匹配正则
                for(int j=0;j<excluded.size();j++){
                    Pattern pattern = Pattern.compile(excluded[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        flag = true
                        break
                    }
                }
                if(!flag){
                    fields << domain.transFields[i]
                }
            }
        }else if(included){
            for(int i=0; i<domain.transFields.size();i++)
            {
                for(int j=0;j<included.size();j++){
                    Pattern pattern = Pattern.compile(included[j])
                    if(pattern.matcher(domain.transFields[i].name).matches()){
                        fields << domain.transFields[i]
                    }
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

        output +="""
                ],
                dockedItems:[{
                    xtype:'toolbar',
                    dock:'top',
                    items:[
                        {
                            text: '${btnTitle}',
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
                        }

                    ]
                }

                ]
            }
        """

        return output
    }

    String imagePanel(Map attrs){
        String title = attrs.title ?: "图片管理"
        String id    = attrs.id
        String parentModel = attrs.parentModel
        String model = attrs.model
        String controller = attrs.controller ?: model
        String listAction = attrs.listAction ?: "list"
        String createAction = attrs.createAction ?: "createAction"
        String deleteAction = attrs.deleteAction ?: "deleteAction"
        List<Map> dockedItems = attrs.dockedItems
        String output ="""
        {
            title: '${title}',
            xtype: 'panel',
            id: '${id}Panel',
            disabled: ${attrs.disabled ?: false},
            autoWidth:true,
            border: false,
            height:((_height=document.documentElement.clientHeight*0.7)<450?_height:450),
            items:[{
                       xtype: 'image',
                       id:'${id}',

                   }],
            bbar: [
                    { xtype: 'button', text: '上一张', id:'preImgBtn', handler: function(){
                        if(curImgId == 0){
                            Ext.MessageBox.show({
                                title: '提示',
                                msg: '已是第一张图片！',
                                buttons: Ext.MessageBox.OK
                            });
                            return;
                        }

                        curImgId = curImgId - 1;
                        Ext.getCmp('${id}').setSrc(imgArry[curImgId].picturePath);
                        Ext.getCmp('${id}Label').setText('（共 ' + imgArry.length + ' 张图片，当前为第'+(curImgId+1)+'张）');
                    }
                    },
                    { xtype: 'button', text: '下一张', id:'nextImgBtn', handler: function(){
                        if(curImgId == imgArry.length-1){
                            Ext.MessageBox.show({
                                title: '提示',
                                msg: '已是最后一张图片！',
                                buttons: Ext.MessageBox.OK
                            });
                            return;
                        }
                        curImgId = curImgId + 1;
                        Ext.getCmp('${id}').setSrc(imgArry[curImgId].picturePath);
                        Ext.getCmp('${id}Label').setText('（共 ' + imgArry.length + ' 张图片，当前为第'+(curImgId+1)+'张）');
                    }
                    },{
                        xtype: 'label',
                        id: '${id}Label'
                    }
            ],
            listeners:{
                afterrender: function(_self, opts){
                    var ${parentModel}Id = ${parentModel}Grid.getSelectionModel().getSelection()[0].data.id;
                    _getPhotoList = function(){
                        Ext.Ajax.request({
                            async:false,
                            url: '${g.createLink([controller: controller,action: listAction])}',
                            method : 'post',
                            params: {search:'[{"key":"${parentModel}Id","value":"'+${parentModel}Id+'"}]'},
                            success: function(response) {
                                if(!response.responseText || response.responseText=="") return; //超时处理
                                var result = Ext.decode(response.responseText);
                                if(result.success){
                                    imgArry = [];
                                    Ext.getCmp('${id}').setSrc('');

                                    var listData = result.data;
                                    if(listData.length > 0){
                                        for(var k=0;k<listData.length;k++){
                                            imgArry.push({id: listData[k].id, picturePath:listData[k].picturePath/*, comment:listData[k].comment*/});
                                        }

                                        if(curImgId!=0){
                                            curImgId = ((curImgId+1) > imgArry.length?(imgArry.length-1):curImgId);
                                        }

                                        Ext.getCmp('${id}').setSrc(imgArry[curImgId].picturePath);

                                        Ext.getCmp('preImgBtn').setDisabled(false);
                                        Ext.getCmp('nextImgBtn').setDisabled(false);
                                    }else{ //没有活动图片
                                        Ext.getCmp('preImgBtn').setDisabled(true);
                                        Ext.getCmp('nextImgBtn').setDisabled(true);
                                    }
                                    Ext.getCmp('${id}Label').setText('（共 ' + listData.length + ' 张图片，当前为第'+(curImgId+1)+'张）');
                                }else{
                                    Ext.wintip.error('获取图片列表失败!',  result.message);
                                }
                            },
                            failure: function() {
                                Ext.wintip.error('请求超时!',  '网络超时!');
                            }
                        });
                    }
                    _getPhotoList();
                }
            }
            ,dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items:[
                         {
                            itemId:'${id}Update',
                            text: '上传照片',
                            disabled: false,
                            iconCls: 'icon-edit',
                            handler: function () {

                                var ${parentModel}Id = ${parentModel}Grid.getView().getSelectionModel().getSelection()[0].data.id;
                                var \$ = jQuery;
                                // 初始化Web Uploader
                                uploader = WebUploader.create({
                                    // swf文件路径
                                    swf:'${g.resource( plugin:"scaffolding", dir:"/webuploader", file:"Uploader.swf")}',
                                    // 文件接收服务端
                                    server: '${g.createLink([controller: controller,action: createAction])}?${parentModel}Id='+${parentModel}Id,
                                    pick: '#filePicker',
                                    // 只允许选择图片文件
                                    accept: {
                                        title: 'Images',
                                        extensions: 'gif,jpg,jpeg,bmp,png',
                                        mimeTypes: 'image/*'
                                    },
                                    disableGlobalDnd: true,
                                    chunked: true,
                                    fileNumLimit: 16, //文件个数限制
                                    fileSizeLimit: 150 * 1024 * 1024,    // 文件大小限制
                                    fileSingleSizeLimit: 5 * 1024 * 1024    // 单个文件大小限制
                                });

                                var \$list = \$('#fileList');

                                \$list.html("");
                                \$('#errorMsg').html("");
                                \$('#filePicker').show();

                                // 当有文件添加进来的时候
                                uploader.on( 'fileQueued', function( file ) {
                                    var \$li = \$(
                                                '<div id="' + file.id + '" class="file-item thumbnail">' +
                                                '<img>' +
                                                '<div class="info">' + file.name + '</div>' +
                                                '</div>'
                                        ),
                                        \$img = \$li.find('img');


                                    \$list.append( \$li );

                                    // 创建缩略图
                                    // 如果为非图片文件，可以不用调用此方法。
                                    // thumbnailWidth x thumbnailHeight 为 100 x 100
                                    uploader.makeThumb( file, function( error, src ) {
                                        if ( error ) {
                                            \$img.replaceWith('<span>不能预览</span>');
                                            return;
                                        }

                                        \$img.attr( 'src', src );
                                    }, 100, 100 );
                                });

                                // 文件上传过程中创建进度条实时显示。
                                uploader.on( 'uploadProgress', function( file, percentage ) {
                                    var \$li = \$( '#'+file.id ),
                                        \$percent = \$li.find('.progress span');

                                    // 避免重复创建
                                    if ( !\$percent.length ) {
                                        \$percent = \$('<p class="progress"><span></span></p>')
                                            .appendTo( \$li )
                                            .find('span');
                                    }

                                    \$percent.css( 'width', percentage * 100 + '%' );
                                });

                                // 文件上传成功，给item添加成功class, 用样式标记上传成功。
                                uploader.on( 'uploadSuccess', function( file ) {
                                    var uploadInfo = uploader.getStats();
                                    \$( '#'+file.id ).addClass('upload-state-done');
                                    \$('#errorMsg').text("上传进度（张）："+ uploadInfo.successNum + "/" + uploader.getFiles().length);
                                });

                                // 文件上传失败，显示上传出错。
                                uploader.on( 'error', function( code ) {
                                    var text = "";
                                    switch( code ) {
                                        case 'F_EXCEED_SIZE':
                                            text = '文件大小超出，最大为5M';
                                            break;
                                        case 'F_DUPLICATE' :
                                            text = '文件已在列表中';
                                            break;
                                        case 'Q_EXCEED_NUM_LIMIT':
                                            text = '一次最多只能上传16张图片';
                                            break;
                                        case 'Q_EXCEED_SIZE_LIMIT':
                                            text = '文件大小超过限制';
                                            break;
                                        default:
                                            text = '上传失败，请重试';
                                            break;
                                    }

                                    \$('#errorMsg').text(text);
                                });

                                // 完成上传完了，成功或者失败，先删除进度条。
                                uploader.on( 'uploadComplete', function( file ) {
                                    \$("#filePicker").hide();
                                });
                                uploadPhotosWnd = Ext.create('Ext.Window', {
                                        title: '上传照片',
                                        plain: true,
                                        modal: true,
                                        resizable: false,
                                        width:550,
                                        height:450,
                                        bodyPadding: '5 5 5 5',
                                        buttonAlign: 'center',
                                        closeAction:'hide',
                                        contentEl:'uploader-container',
                                        listeners: {
                                            hide:function(){
                                                curImgId = 0;
                                                uploader.destroy();
                                                \$('#filePicker').hide();
                                                _getPhotoList();
                                            }
                                        },
                                        buttons: [
                                            {
                                                text: '上传',
                                                handler: function () {
                                                    uploader.upload();
                                                }
                                            },
                                            {
                                                text: '关闭',
                                                handler: function () {
                                                    uploadPhotosWnd.hide();
                                                }
                                            }
                                        ]
                                    });

                                    uploadPhotosWnd.show();
                                    uploadPhotosWnd.center();
                            }
                        },
                        ${m.extOtherButtons([items: dockedItems])}
                        {
                            itemId:'${id}Del',
                            text: '删除当前照片',
                            disabled: false,
                            iconCls: 'icon-delete',
                            handler: function () {
                                _deletePhotos = function(type, id){
                                    var ${parentModel}Id = ${parentModel}Grid.getSelectionModel().getSelection()[0].data.id;

                                    Ext.Ajax.request({
                                        url: '${g.createLink([controller: controller,action: deleteAction])}',
                                        method : 'post',
                                        params: {${parentModel}Id:${parentModel}Id, type:type ,id:id},
                                        success: function(response) {
                                            var result = Ext.decode(response.responseText);
                                            if(result.success){
                                                Ext.wintip.msg('操作成功!', result.message);

                                                if(type == 'all'){
                                                    curImgId = 0;
                                                }
                                                _getPhotoList();
//                                                Ext.getCmp('${id}Panel').fireEvent('afterrender',Ext.getCmp('${id}Panel'));
                                            }else{
                                                Ext.wintip.error('操作失败!',  result.message);
                                            }
                                        },
                                        failure: function() {
                                            Ext.wintip.error('请求超时!',  '网络超时!');
                                        }
                                    });
                                }

                                _deletePhotos('del',imgArry[curImgId].id);
                            }
                        }/*,
                        {
                            itemId:'${id}DelAll',
                            text: '删除全部照片',
                            disabled: false,
                            iconCls: 'icon-delete',
                            handler: function () {
                                Ext.MessageBox.confirm('确认', '确定要删除所有图片？',function(sel){
                                    if(sel=='no' || sel=='cancel') return;
                                    _deletePhotos('all', -1);
                                });
                            }
                        }*/
                    ]
                }
            ]

        }

        """
    }

}
