/*
 * 这是一个扩展类，实现了图片的预览上传功能，并能根据type类型，展现不同UI
 * 调用方式:
    {
        xtype:'imageuploadfield', //扩展类
        type:'all', //all：支持图片的上传，预览；upload：仅支持图片上传；preview：仅支持图片预览
        fieldLabel: '头像', //必选
        name: 'imgurl',  //必选，预览对应的url字段，预览url由服务返回，例如：[{name:'张三',age:28,imgurl:'./zhang.jpg'},{name:'李四',age:38,imgurl:'./li.jpg'}]
        url:'/creative/student/upload' //图片上传路径，type=preview时，为可选
    }
 **/

    Ext.define('Ext.upload.ImgUpload', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.imageuploadfield',

    layout: 'hbox',
    combineErrors: true,

    sucess:null,

    initComponent: function () {
        Ext.apply(this, this.option, {
            items: [this.createTextField(), this.createPreviewBtn(), this.createUploadBtn()]
        });

        this.callParent(arguments);
    },
    
    /**
     * 创建预览文本路径
     */
    createTextField: function(){
        return /*(this.type=='upload'?null:*/Ext.widget({
            xtype: 'hiddenfield',
            name: this.name,  //标签化时name必须保证唯一
            itemId: this.name,
            value: ''
        });//);
    },

    /**
     * 创建预览按钮
     */
    createPreviewBtn: function(){
        var me = this;
    
        return /*(me.type=='upload'?null:*/Ext.widget({
            xtype: 'button',
            margin:'0 0 0 5',
            text: '预览',
            handler: function(){
                var imgPath =  me.down("hiddenfield[name='"+me.name+"']").getValue();
                if((imgPath.lastIndexOf('/null')!=-1) || (imgPath=="")){
                    Ext.MessageBox.show({
                        title: '提示',
                        msg: '您还未上传图片，请先上传图片！',
                        buttons: Ext.MessageBox.OK
                    });
                    return;
                }

                //贴到window上
                var imgViewWin = Ext.create('Ext.Window', {
                    title: '图片预览',
                    maxWidth: (document.documentElement.clientWidth-10),
                    maxHeight:(document.documentElement.clientHeight-10),
                    minWidth:300,
                    minHeight:300,
                    modal: true,
//                    resizable:false,
                    autoScroll:true,
                    buttonAlign: 'center',
                    items: [{
                        xtype: 'image',
//                        autoRender:true,
                        src: imgPath//,
//                        autoEl:'div'//, //使图片保持原大小
//                        resizable:true,
//                        border:false
                    }],
                    buttons: [{
                        text: '关闭',
                        handler: function() {
                            this.up('window').close();
                        }
                    }]
                });

                imgViewWin.show(); //Ext只有show后才会渲染
                imgViewWin.hide(); //隐藏，避免弹出框闪烁

                setTimeout(function(){
                    var imgWidth = imgViewWin.down('image').getWidth() + 12;
                    var imgHeight = imgViewWin.down('image').getHeight() + 83;
                    var winWidth = (imgWidth>imgViewWin.maxWidth?imgViewWin.maxWidth:imgWidth);
                    var winHeight= (imgHeight>imgViewWin.maxHeight?imgViewWin.maxHeight:imgHeight);

                    imgViewWin.setWidth(winWidth);
                    imgViewWin.setHeight(winHeight);
                    imgViewWin.show();
                    imgViewWin.center();
                }, 500);

//                imgViewWin.addListener("resize",function(me, width, height, opts){
//                    me.down('image').setWidth(width);
//                    me.down('image').setHeight(height);
//                });

            }
        });
    },
    
    /**
     * 创建上传按钮
     */
    createUploadBtn: function(){
        var me = this;
        return (me.type=='preview'?null:Ext.widget({
            xtype: 'button',
            margin:'0 0 0 5',
            text: '上传', //(me.type=='upload'?'上传':'更新'),
            handler: function(){
                //建form表单
                var required = '<span style="color:red;font-weight:bold">*</span>';

                var imgViewForm = Ext.widget({
                    xtype: 'form',
                    width:350,
                    bodyPadding: 5,
                    items: [{
                            xtype: 'filefield',
                            //id: 'form-image',
                            name: 'upload-image',
                            emptyText: '请选择图片',
                            fieldLabel: '图片名',
                            afterLabelTextTpl: required,
                            labelWidth: 50,
                            msgTarget: 'side',
                            allowBlank: false,
                            anchor: '90%',
                            buttonText: '',
                            buttonConfig: {
                                iconCls: 'icon-upload'
                            },
                            listeners: {
                                change: function (self, v, opts) {
                                    //是否是规定的图片类型
                                    var img_reg = /\.([jJ][pP][gG]){1}$|\.([jJ][pP][eE][gG]){1}$|\.([gG][iI][fF]){1}$|\.([pP][nN][gG]){1}$|\.([bB][mM][pP]){1}$/;
                                    if(!img_reg.test(v)){
                                        Ext.Msg.alert('提示', '请选择图片类型的文件！');
                                        self.setRawValue("");
                                    }
                                }
                            }
                        }
                    ],
                    buttons: [{
                        text: '上传',
                        handler: function(){
                            var _self = this;
                            var form = _self.up('form').getForm();
                            if(form.isValid()){
                                form.submit({
                                    url: me.url,
                                    waitMsg: '图片上传中，请稍等......',
                                    success: function(fp, o) {
                                        var name = me.name.replace(/Btn/,"");
                                        //var objId = (me.type=='all'?('#'+name+'Update'):('#'+name))
                                        var uploadPath=Ext.ComponentQuery.query("hiddenfield[itemId='"+name+"']")[0];
//                                        alert(Ext.JSON.decode(o.response.responseText).path+">>>>>>"+Ext.ComponentQuery.query("hiddenfield[itemId='"+name+"']").length+"**[1]**"+Ext.ComponentQuery.query("hiddenfield[itemId='"+name+"']")[1]+"****name="+name);

                                        var servicePath = Ext.JSON.decode(o.response.responseText).data.path;

                                        if(uploadPath){
                                              uploadPath.setValue(servicePath);
                                          }

                                        me.down("hiddenfield[name='"+me.name+"']").setValue(servicePath);

//                                        Ext.Msg.show({
//                                            title: "上传成功",
//                                            msg: o.result.message,
//                                            modal: true,
//                                            icon: Ext.Msg.INFO,
//                                            buttons: Ext.Msg.OK
//                                        });
                                        _self.up('window').close();
                                        try{
                                            if(typeof(me.sucess) === "function"){
                                                me.sucess(Ext.JSON.decode(o.response.responseText));
                                            }
                                        }catch (e){}
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
                var imgViewWin = Ext.create('Ext.Window', {
                    title: '图片上传',
//                    width: 300,
                    modal: true,
                    resizable: false,
                    items: [imgViewForm]
                });

                imgViewWin.show();
            }
        }));

    }
});