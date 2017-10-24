<!DOCTYPE html>
<html>
<head>
<title>${grailsApplication.config.grails.app.title}</title>
<meta charset="utf-8" />
<style type="text/css">
#nd_banner .title {
    font-size:25px;
    color:#FFF;
    /*line-height: 100px;*/
    font-weight: bold;
    margin-left:50px;
}
#nd_banner .subtitle {
    font-size:12px;
    font-style:italic;
    color:#F0F5FB;
    margin-left:175px;
    padding-top:3px;
}
#nd_banner {
    float:left;
    background:url("${grailsApplication.config.grails.app.logo?:g.resource([dir: "css/images", file: "logo.png"])}") no-repeat;
    /*height:100px;*/
    margin-left:5px;
}
</style>
<m:extResource/>
<script src="${g.resource([plugin: "scaffolding", dir: "/login/js", file: "jquery-1.8.3.min.js"])}"></script>

<script type="text/javascript">
    //点击菜单
    function menuItemClick(id,caption,url) {
        //获得tab容器nd_main
        var tabPanel = Ext.getCmp('nd_main');

        //获得被选中的tab组建对象
        var tab = tabPanel.getComponent(id + "-tab");

        //如果此tab还未被创建，则向tab容器添加一个tab
        if (!tab) {
            tab = tabPanel.add({
                id: id + "-tab",
                title: caption,
                border:false,
                closable: true,
                autoScroll: false,
                active: true,
                html: '<iframe frameborder="0" id="'+id+'-tab-frame" width="100%" height="100%" src="' + url + '"></iframe>'
            });
        }

        //将tab设置为当前可见可操作tab
        tabPanel.setActiveTab(tab);
    }

    Ext.onReady(function () {
        //菜单模板
        var menuTemplate = new Ext.XTemplate(
                '<tpl for=".">',
                '<div class="thumb-wrap" id="{caption:stripTags}">',
                '<div class="thumb"><img src="{icon}" title="{caption:htmlEncode}"></div>',
                '<span class="x-editable">{caption:htmlEncode}</span>',
                '</div>',
                '</tpl>',
                '<div class="x-clear"></div>'
        );

        //创建全景视图端
        var viewport = Ext.create('Ext.Viewport', {
            id: 'body_view',
            layout: 'border', //框式布局
            items: [
                //页头
                Ext.create('Ext.Component', {
                    region: 'north',
                    height: ${grailsApplication.config.grails.app.banner.height?:50},
                    contentEl: 'nd_banner'
                }),
                <m:extNavigation />
                //主页面
                Ext.create('Ext.tab.Panel', {
                    id: 'nd_main',
                    layout: 'fit',
                    //border:false,
                    region: 'center', //中心区在框布局中是必须定义的
                    activeTab: 0, //设置第一个选项卡为默认显示的
                    items: [{
                        id: 'tab_main',
                        title: "${grailsApplication.config.cube.navigation.home.title}",
                        closable: false,
                        autoScroll: true,
                        html: '<iframe frameborder="0" width="100%" height="99%" src="${g.createLink([controller:grailsApplication.config.cube.navigation.home.controller, action: grailsApplication.config.cube.navigation.home.action])}"></iframe>'
                    }],
                    listeners: {
                        tabchange: function(tab,newCard,oldCard,opts){
                            try{
                                document.getElementById(newCard.id+"-frame").contentWindow.onTabActive();
                            }catch(e) {

                            }
                        }
                    }
                }),
                //页脚
                Ext.create('Ext.Component', {
                    region: 'south',
                    height: 25,
                    contentEl: 'nd_footer'
                })
            ]
        });

    });

    //密码验证
    Ext.apply(Ext.form.VTypes,{
        passwordConfirm : function (val,field){
            if(field.passwordConfirm){
                var pwd = Ext.getCmp(field.passwordConfirm.targetCmpId);
                if(val == pwd.getValue()){
                    return true;
                }else{
                    return false;
                }
            }
        },
        passwordConfirmText : '两次输入的密码不一致'
    });
    //密码修改
    var passwordResetWnd;
    var required = '<span style="color:red;">*</span>';
    //退出
    function logOut() {
        Ext.MessageBox.confirm("退出系统", "你确定要退出？", function(btn){
            if (btn == 'yes'){

                jQuery.ajax({
                    type:"POST",
                    //这边怎么写
                    url:"<g:createLink controller="sysUser" action="logOut"/>",
                    dataType: "json",
                    success: function (json) {
                        window.location.href = json.url
                    }
                });
            }
            else{
            }

        });
    }

    function passwordReset() {
        if (!passwordResetWnd) {
            passwordResetForm = Ext.widget({
                xtype: 'form',
                id: 'passwordResetForm',
                layout: 'form',
                autoScroll:true,
                width:550,
                bodyPadding: '5 5 5 5',
                buttonAlign: 'center',
                defaultType: 'textfield',
                items: [
                    {fieldLabel: '旧密码',name: 'password', id:"oldpwd", allowBlank: false, afterLabelTextTpl: required, regex:/^[^\s]{6,16}$/, regexText:'密码长度6-16位，不能包含空格！',inputType: 'password' },
                    {fieldLabel: '新密码',name: 'newPasswordCheck', id: 'newpwd', allowBlank: false, afterLabelTextTpl: required, regex:/^[^\s]{6,16}$/, regexText:'密码长度6-16位，不能包含空格！',inputType: 'password' },
                    {fieldLabel: '再次输入新密码',name: 'newPassword',id: 'renewpwd', allowBlank: false,
                        afterLabelTextTpl: required, regex:/^[^\s]{6,16}$/,regexText:'密码长度6-16位，不能包含空格！',
                        inputType: 'password',vtype:'passwordConfirm', passwordConfirm:{targetCmpId:'newpwd'}}
                ],
                buttons: [
                    {
                        text: '确定',
                        handler: function () {
                            var newp =  Ext.getCmp('newpwd').getValue();
                            var renewp = Ext.getCmp('renewpwd').getValue();

                            if(renewp == newp ) {

                                var cmpForm = this.up('form').getForm();
                                if (cmpForm.isValid()) {
                                    cmpForm.submit({
                                        url: '${g.createLink([controller: "sysUser", action: "passwordReset"])} ',
                                        waitTitle: '请稍后',
                                        waitMsg: '正在提交数据...',
                                        success: function (form, action) {
                                            if(!action.result) return; //超时处理
                                            Ext.wintip.msg('修改成功', action.result.message);
                                            passwordResetForm.getForm().reset();
//                                                passwordResetGrid.getStore().reload();
                                        },
                                        failure: function (form, action) {
                                            Ext.wintip.error('修改失败', action.result.message);
                                            passwordResetForm.getForm().reset();
                                        }
                                    });
                                    passwordResetWnd.hide();
//                                    }
                                }
                            }
                        }
                    },
                    {
                        text: '关闭',
                        handler: function () {
                            passwordResetWnd.hide();
                        }
                    }
                ]
            });

            passwordResetWnd = Ext.create('Ext.Window', {
                title: '修改密码',
                plain: true,
                modal: true,
                resizable: false,
                closeAction: 'hide',
                items: [passwordResetForm]
            });
        }
        passwordResetWnd.down('form').form.reset();
        passwordResetWnd.show();
        var autoHeight = parseInt(document.documentElement.clientHeight*0.7);
        if(document.getElementById('passwordResetForm').scrollHeight>autoHeight){
            passwordResetWnd.down('form').setHeight(autoHeight);
            passwordResetWnd.down('form').setWidth(750);
            passwordResetWnd.center();
        }
    }
</script>
</head>

<body>
<!--页头-->
<div id="nd_banner">
    <div class="title">${grailsApplication.config.grails.app.title}</div>
    <div class="subtitle">${grailsApplication.config.grails.app.subtitle}</div>
</div>
<div id="top_menu">
    <ul class="top_nav">
        %{--<li><a href="http://www.baidu.com" target="_blank">客户服务</a></li>--}%
        %{--<li><a href="#" target="dialog">产品微博</a></li>--}%
        %{--<li><a href="#" target="dialog">修改密码</a></li>--}%
        <li style="color: #fff;">欢迎您！${userName}（${userRole}）</li>
        <li><a href="javaScript:passwordReset()">修改密码</a></li>
        <li><a href="javaScript:logOut()">退出</a></li>
    </ul>
</div>
<!--页脚-->
<div id="nd_footer">
    <div style="text-align:center;padding:3px 0 0 2px;color:#FFF;" >${grailsApplication.config.grails.app.footer}</div>
</div>
<script>
    var _hmt = _hmt || [];
    (function() {
        var hm = document.createElement("script");
        hm.src = "//hm.baidu.com/hm.js?555a0a1e1d37c927eeb209b05b38a9c1";
        var s = document.getElementsByTagName("script")[0];
        s.parentNode.insertBefore(hm, s);
    })();
</script>
</body>
</html>