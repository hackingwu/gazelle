/**
 * Created by Administrator on 2014/8/11.
 */
/**
 * 异步请求，Session超时控制
 * @author 林畅辉
 * @version 2014/9/1 修改登录路径写死的问题，采用通过Authfilters传值的方式
 */
var loginWnd;
function loginWndPop(url){
    if(!loginWnd){
        loginForm = Ext.widget({
            xtype: 'form',
            id: 'loginForm',
            layout: 'form',
            autoScroll:true,
            width:450,
            bodyPadding: '5 5 5 5',
            buttonAlign: 'center',
            defaultType: 'textfield',
            items: [
                {fieldLabel: '用户名',name: 'login', allowBlank: false, afterLabelTextTpl: required},
                {fieldLabel: '密码',name: 'password', allowBlank: false, afterLabelTextTpl: required, inputType: 'password'}
            ],
            buttons: [
                {
                    text: '确定',
                    handler: function () {
                        var cmpForm = this.up('form').getForm();
                        if (cmpForm.isValid()){
                            cmpForm.submit({
                                url: url,
                                waitTitle: '请稍后',
                                waitMsg: '正在提交数据...',
                                success: function (form, action) {
                                    if(action.result.success){
//                                        window.location.reload();
                                        loginWnd.hide();
                                    }else{
                                        Ext.wintip.error('登录失败!', action.result.message);
                                    }
                                },
                                failure: function (form, action) {
                                    Ext.wintip.error('登录失败!', action.result.message);
                                }
                            });
                        }
                    }
                }
            ]
        });

        loginWnd = Ext.create('Ext.Window', {
            title: '登录超时,请重新登录！',
            plain: true,
            modal: true,
            resizable: false,
            closable: false,
            items: [loginForm]
        });
    }
        loginWnd.down('form').form.reset();
        loginWnd.show();
        loginWnd.center();
}

Ext.Ajax.on('requestcomplete', function (conn, response, options) {
    //获取response里人为添加的sessionstatus超时判断字段
    if(("function" == typeof(response.getResponseHeader)) && (response.getResponseHeader('sessionstatus') != null)){

        var dataObj = eval("("+response.getResponseHeader('sessionstatus')+")");

        loginWndPop(dataObj.url);
        return false;
    }
});

