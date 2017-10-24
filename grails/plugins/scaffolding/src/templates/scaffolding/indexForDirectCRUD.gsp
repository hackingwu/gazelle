<% import grails.persistence.Event %><%=packageName%><%
    out << "<" << "%" <<""" Map mDomainClass = framework.ModelService.GetModel("${domainClass.propertyName}") """ << "%" << ">"
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="content-type" content="text/html" charset="utf-8">
<title>\${mDomainClass.m.domain.cn}管理</title>
<m:extResource/>
<script type="text/javascript">
    var ${domainClass.propertyName}CreateWnd;
    var ${domainClass.propertyName}UpdateWnd;
    var ${domainClass.propertyName}DetailWnd;
    var ${domainClass.propertyName}Store;
    var ${domainClass.propertyName}Grid;
    var required = '<span style="color:red;">*</span>';

    <m:extCreateWnd model="${domainClass.propertyName}" excluded='[]' controller="${domainClass.propertyName}" action="create"/>
    <m:extUpdateWnd model="${domainClass.propertyName}" excluded='[]' controller="${domainClass.propertyName}" action="update"/>
    <m:extDetailWnd model="${domainClass.propertyName}" excluded='[]' controller="${domainClass.propertyName}" action="detail"/>


    Ext.Loader.setConfig({
        enabled:true,
        disableCaching:false,
        paths:{
            'Go':'<g:resource plugin="scaffolding" dir="js/extExtendCmp/dateTimePicker"/>'
        }
    });

    Ext.require(['Go.form.field.DateTime']); //加载带时间选择的日期控件类,@注意：这个加载是异步的

    Ext.onReady(function () {

        Ext.QuickTips.init();
        Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息

        //创建toolbar
        var toolbar = Ext.create('Ext.Toolbar', {
            minWidth: 800,
            items: [
                <m:extCRUDButtons model="${domainClass.propertyName}" excluded='[]' />
                '->',
                <m:extSearchButtons model="${domainClass.propertyName}" mode="standard" excluded='[]'/>
                {
                    itemId: 'resetSearch',
                    text: '重置',
                    iconCls: 'icon-reset',
                    handler: function () {
                        toolbar.down('#searchValue').setValue("");
                        ${domainClass.propertyName}Store.loadPage(1);
                    }
                }
            ]
        });

        <m:extGridStore model="${domainClass.propertyName}" excluded='[]'/>
        <m:extGrid model="${domainClass.propertyName}" excluded='[]'/>
        <m:extLayout model="${domainClass.propertyName}" excluded='[]'/>

        ${domainClass.propertyName}Store.loadPage(1);
    });
</script>
</head>
<body style="overflow: auto;">
</body>
</html>
