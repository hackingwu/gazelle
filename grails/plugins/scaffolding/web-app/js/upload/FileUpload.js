/*
 * 这是一个扩展类，实现了文件的上传，下载功能，并能根据type类型，展现不同UI
 * 调用方式:
    {
        xtype:'fileuploadfield', //扩展类
        type:'all', //all：支持文件的上传，下载；upload：仅支持文件上传；download：仅支持文件下载
        fieldLabel: '简历上传', //必选
        name: 'fileurl',  //必选，下载对应的url字段，下载url由服务器返回，例如：[{name:'张三',age:28,resumurl:'./zhang.doc'},{name:'李四',age:38,resumurl:'./li.doc'}]
        url:'/creative/student/upload' //文件上传路径，type=download时，为可选
    }
 **/

    Ext.define('Ext.upload.FileUpload', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.fileuploadfield',

    layout: 'hbox',
    combineErrors: true,

    initComponent: function () {
        Ext.apply(this, this.option, {
            items: [this.createTextField(), this.createDownloadBtn(), this.createUploadBtn()]
        });

        this.callParent(arguments);
    },
    
    /**
     * 创建预览按钮
     */
    createTextField: function(){
        return (this.type=='upload'?null:Ext.widget({
            xtype: 'textfield',
            name: this.name,  //标签化时name必须保证唯一
            value: 'http://99touxiang.com/public/upload/nansheng/30/20-071359_256.jpg',
            readOnly: true
        }));
    },
    
    /**
     * 创建下载按钮
     */
    createDownloadBtn: function(){
        var me = this;
        return (me.type=='upload'?null:Ext.widget({
            xtype: 'button',
            margin:'0 0 0 5',
            text: '下载',
            handler: function(){
                window.open( me.down("[name='"+me.name+"']").value );
            }
        }));
    },
    
    /**
     * 创建上传按钮
     */
    createUploadBtn: function(){
        var me = this;
        return (me.type=='download'?null:Ext.widget({
            xtype: 'button',
    //      hideLabel: true,
            margin:'0 0 0 5',
            text: '上传', //(me.type=='upload'?'上传':'更新'),
            handler: function(){
                //建form表单
                var required = '<span style="color:red;font-weight:bold">*</span>';

                var fileUploadForm = Ext.widget({
                    xtype: 'form',
                    layout: 'form',
                    height:100,
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
                        //id: 'form-file',
                        name: 'upload-file',
                        emptyText: '请选择文件',
                        fieldLabel: '文件名',
                        afterLabelTextTpl: required,
                        buttonText: '',
                        buttonConfig: {
                            iconCls: 'icon-upload'
                        }
                    }],
                    buttons: [{
                        text: '上传',
                        handler: function(){
                            var _self = this;
                            var form = _self.up('form').getForm();
                            if(form.isValid()){
                                form.submit({
                                    url: me.url,
                                    waitMsg: '文件上传中，请稍等......',
                                    success: function(fp, o) {
                                        Ext.Msg.show({
                                            title: "上传成功",
                                            msg: o.result.message,
                                            modal: true,
                                            icon: Ext.Msg.INFO,
                                            buttons: Ext.Msg.OK
                                        });

                                        _self.up('window').close();
                                    },
                                    failure: function(form, action) {
                                        Ext.Msg.show({
                                            title: "上传失败",
                                            msg: action.result.message,
                                            modal: true,
                                            icon: Ext.Msg.ERROR,
                                            buttons: Ext.Msg.OK
                                        });
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
                    title: '文件上传',
                    width: 350,
                    plain: true,
                    modal: true,
                    resizable: false,
                    items: [fileUploadForm]
                });

                fileUploadWin.show();
            }
        }));
    }
});