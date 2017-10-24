<% import grails.persistence.Event %><%=packageName%><%

    Map mDomainClass = framework.ModelService.GetModel("${domainClass.propertyName}")

    Map mAssociatedDomainClass = [:]

    if(mDomainClass.m?.layout?.type=="oneToManyUpDown" || mDomainClass.m?.layout?.type=="oneToManyTreeView" || mDomainClass.m?.layout?.type=="formTreeView"){ //添加oneToManyTreeView组件by lichb 2014-6-4,添加formView组件by linchh 2014-9-16
        mAssociatedDomainClass = framework.ModelService.GetModel("${mDomainClass.m.layout.domain}")
    }

    out << "<" << "%" <<""" Map ${domainClass.propertyName}Model = framework.ModelService.GetModel("${domainClass.propertyName}") """ << "%" << ">"
%>

<% if(mDomainClass.m?.layout?.type=="oneToManyUpDown"){ %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>\${${domainClass.propertyName}Model.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var ${domainClass.propertyName}CreateWnd;
        var ${domainClass.propertyName}UpdateWnd;
        var ${domainClass.propertyName}DetailWnd;
        %{--var ${domainClass.propertyName}Store;--}%
        %{--var ${domainClass.propertyName}Grid;--}%

        var ${mAssociatedDomainClass.propertyName}DownDetailWnd;
        var ${mAssociatedDomainClass.propertyName}DownStore;
        var ${mAssociatedDomainClass.propertyName}DownGrid;

        var required = '<span style="color:#ff0000;">*</span>';

        //tab获得焦点时，执行该函数
        function onTabActive(){
            ${domainClass.propertyName}Store.reload();
        }

        <m:extCreateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="createAction" />
        <m:extUpdateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="updateAction" />
        <m:extDetailWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="detailAction" />

        <m:extDetailWnd model="${mAssociatedDomainClass.propertyName}" extra="Down" excluded="[]" controller="${mAssociatedDomainClass.propertyName}" action="detailAction" />

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker"/>', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload"/>', //上传控件路径，不使用上传控件时，可删除该代码
                'Ext.selectField':'<g:resource plugin="scaffolding" dir="js/selectField" />'
            }
        });

        Ext.require(['Go.form.field.DateTime']); //加载带时间选择的日期控件类,不使用时可删除该代码
        Ext.require(['Ext.upload.ImgUpload']); //加载图片上传类，不使用时可删除该代码
        Ext.require(['Ext.upload.FileUpload']); //加载文件上传类，不使用时可删除该代码

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extCRUDButtons model="${domainClass.propertyName}" excluded="[]" />
                    <m:extSingleSelectButton model="${mAssociatedDomainClass.propertyName}" excluded="[]" />
                    <m:extMultiSelectButton model="${mAssociatedDomainClass.propertyName}" excluded="[]" />
                    <m:extRowSelectButton model="${mAssociatedDomainClass.propertyName}" controller="${domainClass.propertyName}" action="rowSelAction" mode="single" />
                    <m:extRowSelectButton model="${mAssociatedDomainClass.propertyName}" controller="${domainClass.propertyName}" action="rowSelAction" mode="multi" />
                    '->',
                    <m:extSearchButtons model="${domainClass.propertyName}" mode="standard" excluded="[]" />
                ]
            });

            <m:extGridStore model="${domainClass.propertyName}" excluded="[]" />
            <m:extGrid model="${domainClass.propertyName}" associatedStore="${mAssociatedDomainClass.propertyName}DownStore" excluded="[]" height="0.4" />



            <m:extGridStore model="${mAssociatedDomainClass.propertyName}" extra="Down" excluded="['${mDomainClass.m?.layout?.key}']" />
            <m:extGrid model="${mAssociatedDomainClass.propertyName}" extra="Down" excluded="['${mDomainClass.m?.layout?.key}']" height="0.6" />

            <m:extLayout model="${domainClass.propertyName}" layout="${mDomainClass.m?.layout?.type}" associated="${mAssociatedDomainClass.propertyName}" />

            ${domainClass.propertyName}Store.loadPage(1);
        });
    </script>
</head>
<body style="overflow: auto;"></body>
</html>
<% } else if(mDomainClass.m?.layout?.type=="oneToManyTreeView") /* 添加oneToManyTreeView组件by lichb 2014-6-4 */ {%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>\${${domainClass.propertyName}Model.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var ${domainClass.propertyName}CreateWnd;
        var ${domainClass.propertyName}UpdateWnd;
        var ${domainClass.propertyName}DetailWnd;
        %{--var ${domainClass.propertyName}Store;--}%
        %{--var ${domainClass.propertyName}Grid;--}%
        var ${mAssociatedDomainClass.propertyName}Tree;
        var ${mAssociatedDomainClass.propertyName}TreeNode=1;
        var ${mAssociatedDomainClass.propertyName}TreeText="";
        var required = '<span style="color:red;">*</span>';

        //tab获得焦点时，执行该函数
        function onTabActive(){
            ${domainClass.propertyName}Store.reload();
        }

        <m:extCreateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="createAction" />
        <m:extUpdateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="updateAction" />
        <m:extDetailWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="detailAction" />

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker" />', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload" />' //上传控件路径，不使用上传控件时，可删除该代码
            }
        });

        Ext.require(['Go.form.field.DateTime']); //加载带时间选择的日期控件类,不使用时可删除该代码
        Ext.require(['Ext.upload.ImgUpload']); //加载图片上传类，不使用时可删除该代码
        Ext.require(['Ext.upload.FileUpload']); //加载文件上传类，不使用时可删除该代码

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extCRUDButtons model="${domainClass.propertyName}" excluded="[]" />
                    <m:extSingleSelectButton model="${mAssociatedDomainClass.propertyName}" excluded="[]" />
                    <m:extMultiSelectButton model="${mAssociatedDomainClass.propertyName}" excluded="[]" />
                    '->',
                    <m:extSearchButtons model="${domainClass.propertyName}" mode="standard" excluded="['${mDomainClass.m?.layout?.key}']" />
                ]
            });

            <m:extGridStore model="${domainClass.propertyName}" associated="${mAssociatedDomainClass.propertyName}" excluded="['${mDomainClass.m?.layout?.key}']" />
            <m:extGrid model="${domainClass.propertyName}" excluded="['${mDomainClass.m?.layout?.key}']" />

            <m:extTreeStore model="${mAssociatedDomainClass.propertyName}" excluded="[]" />
            <m:extTree model="${mAssociatedDomainClass.propertyName}" excluded="[]" />

            <m:extLayout model="${domainClass.propertyName}" layout="${mDomainClass.m?.layout?.type}" associated="${mAssociatedDomainClass.propertyName}" />

            ${domainClass.propertyName}Store.loadPage(1);
        });
    </script>
</head>
<body style="overflow: auto;"></body>
</html>
<% } else if(mDomainClass.m?.layout?.type=="formTreeView") /* 添加formTreeView组件by linchh 2014-9-16 */ {%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>\${${domainClass.propertyName}Model.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">

        var ${mAssociatedDomainClass.propertyName}Tree;
        var ${mAssociatedDomainClass.propertyName}TreeNode=1;
        var ${mAssociatedDomainClass.propertyName}TreeText="";
        var required = '<span style="color:red;">*</span>';


        <m:extFormUpdateWnd model="${domainClass.propertyName}" controller="${domainClass.propertyName}"  />
        <m:extResetWnd model="${domainClass.propertyName}" controller="${domainClass.propertyName}" associated="${mAssociatedDomainClass.propertyName}"/>

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker" />', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload" />' //上传控件路径，不使用上传控件时，可删除该代码
            }
        });

        Ext.require(['Go.form.field.DateTime']); //加载带时间选择的日期控件类,不使用时可删除该代码
        Ext.require(['Ext.upload.ImgUpload']); //加载图片上传类，不使用时可删除该代码
        Ext.require(['Ext.upload.FileUpload']); //加载文件上传类，不使用时可删除该代码

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extFormButtons model="${domainClass.propertyName}" excluded="[]" />
                ]
            });
            <m:extForm model="${domainClass.propertyName}"  associated="${mAssociatedDomainClass.propertyName}" excluded="['${mDomainClass.m?.layout?.key}']"/>


            <m:extTreeStore model="${mAssociatedDomainClass.propertyName}" excluded="[]" />
            <m:extFormTree model="${mAssociatedDomainClass.propertyName}" excluded="[]" />

            <m:extLayout model="${domainClass.propertyName}" layout="${mDomainClass.m?.layout?.type}" associated="${mAssociatedDomainClass.propertyName}" />

        });
    </script>
</head>
<body style="overflow: auto;"></body>
</html>
<% } else if(mDomainClass.m?.layout?.type=="formView") /* 添加formView组件by linchh 2014-9-17 */ {%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>\${${domainClass.propertyName}Model.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">


        var required = '<span style="color:red;">*</span>';


        <m:extFormUpdateWnd model="${domainClass.propertyName}" controller="${domainClass.propertyName}"  />
        <m:extResetWnd model="${domainClass.propertyName}" controller="${domainClass.propertyName}"/>

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker" />', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload" />' //上传控件路径，不使用上传控件时，可删除该代码
            }
        });

        Ext.require(['Go.form.field.DateTime']); //加载带时间选择的日期控件类,不使用时可删除该代码
        Ext.require(['Ext.upload.ImgUpload']); //加载图片上传类，不使用时可删除该代码
        Ext.require(['Ext.upload.FileUpload']); //加载文件上传类，不使用时可删除该代码

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extFormButtons model="${domainClass.propertyName}" excluded="[]" />
                ]
            });
            <m:extForm model="${domainClass.propertyName}" excluded="[]" />


            <m:extLayout model="${domainClass.propertyName}" layout="${mDomainClass.m?.layout?.type}"/>

        });
    </script>
</head>
<body style="overflow: auto;"></body>
</html>
<% } else { %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>\${${domainClass.propertyName}Model.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var ${domainClass.propertyName}CreateWnd;
        var ${domainClass.propertyName}UpdateWnd;
        var ${domainClass.propertyName}DetailWnd;
        %{--var ${domainClass.propertyName}Store;--}%
        %{--var ${domainClass.propertyName}Grid;--}%
        var required = '<span style="color:red;">*</span>';

        <m:extCreateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="createAction" />
        <m:extUpdateWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="updateAction" />
        <m:extDetailWnd model="${domainClass.propertyName}" excluded="[]" controller="${domainClass.propertyName}" action="detailAction" />

        Ext.Loader.setConfig({
            enabled:true,
            disableCaching:false,
            paths:{
                'Go':'<g:resource plugin="scaffolding" dir="js/dateTimePicker" />', //带时间选择的日期控件路径，不使用带时间选择的日期控件时，可删除该代码
                'Ext.multiUpload':'<g:resource plugin="scaffolding" dir="js/multiUpload" />',
                'Ext.upload':'<g:resource plugin="scaffolding" dir="js/upload" />', //上传控件路径，不使用上传控件时，可删除该代码
                'Ext.selectField':'<g:resource plugin="scaffolding" dir="js/selectField" />'
            }
        });

        Ext.require(['Go.form.field.DateTime']); //带时间选择的日期控件
        Ext.require(['Ext.upload.ImgUpload']); //图片上传
        Ext.require(['Ext.upload.FileUpload']); //单文件上传
        Ext.require(['Ext.multiUpload.Panel']); //多文件上传
        Ext.require(['Ext.selectField.Select']); //单选控件
        Ext.require(['Ext.selectField.MultiSelect']); //多选控件

        Ext.onReady(function () {

            Ext.QuickTips.init();
            Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));   //运行此语句后，可以在组件中使用stateful: true;结合stateId保存用户定制信息
            Ext.supports.Placeholder = false;

            //创建toolbar
            var toolbar = Ext.create('Ext.Toolbar', {
                autoScroll: true,
                items: [
                    <m:extCRUDButtons model="${domainClass.propertyName}" excluded="[]" />
                    %{--<m:extSingleSelectButton model="${domainClass.propertyName}" excluded="[]" />
                    <m:extMultiSelectButton model="${domainClass.propertyName}" excluded="[]" />
                    <m:extImportButton model="${domainClass.propertyName}" />
                    <m:extExportButton model="${domainClass.propertyName}" />--}%
                    '->',
                    %{--<m:extGangCombo model="${domainClass.propertyName}" combo="[[label:'项目',name:'select_app_code',controller:'${domainClass.propertyName}',action:'combo1'],[label:'版本',name:'select_app_version',controller:'${domainClass.propertyName}',action:'combo2']]" />--}%
                    <m:extSearchButtons model="${domainClass.propertyName}" mode="standard" excluded="[]" />
                ]
            });

            <m:extGridStore model="${domainClass.propertyName}" excluded="[]" />
            <m:extGrid model="${domainClass.propertyName}" excluded="[]" config="[column:8,rowHeight:25]" />

            <m:extLayout model="${domainClass.propertyName}" />

            ${domainClass.propertyName}Store.loadPage(1); //extGridStore配置name属性时，需要手动修改此属性
        });
</script>
</head>
<body style="overflow: auto;"></body>
</html>
<% } %>