<%@ page import="accessResources.BtnAndMenu" %><% Map btnAndMenuModel = framework.ModelService.GetModel("btnAndMenu") %>


<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html" charset="utf-8">
    <title>${btnAndMenuModel.m.domain.cn}管理</title>
    <m:extResource/>
    <script type="text/javascript">
        var btnAndMenuCreateWnd;
        var btnAndMenuUpdateWnd;
        var btnAndMenuDetailWnd;
        %{--var btnAndMenuStore;--}%
        %{--var btnAndMenuGrid;--}%
        var required = '<span style="color:red;">*</span>';

        <m:extCreateWnd model="btnAndMenu" excluded="[]" controller="btnAndMenu" action="createAction" />
        <m:extUpdateWnd model="btnAndMenu" excluded="[]" controller="btnAndMenu" action="updateAction" />
        <m:extDetailWnd model="btnAndMenu" excluded="[]" controller="btnAndMenu" action="detailAction" />

        function getSelectValue() {
            var str = DWRUtil.getValue("check");
            var ids = "";
            var names = "";
            for ( var j = 0; j < str.length; j++) {
                var array = str[j].split("-");
                if(typeof(returnType) == "undefined" || returnType ==""){  //默认获取叶子 节点
                    if (array[2] == 'true') {
                        ids += array[0];
                        names += array[1];
                        if (str.length - 1 != j) {
                            ids += ",";
                            names += ",";
                        }
                    }
                }else if(returnType == "node"){
                    if (array[2] == 'false') {
                        ids += array[0];
                        names += array[1];
                        if (str.length - 1 != j) {
                            ids += ",";
                            names += ",";
                        }
                    }
                }else if(returnType=="all"){
                    ids += array[0];
                    names += array[1];
                    if (str.length - 1 != j) {
                        ids += ",";
                        names += ",";
                    }
                }

            }
            var array = new Array();
            array[0] = ids;
            array[1] = names;
            return array;
        }

        function showTree() {
            var root = new Ext.tree.AsyncTreeNode( {
                text : rootName,
                draggable : false,
                id : rootId
            });
            var tree = new Ext.tree.TreePanel( {
                checkModel : check, // //多选: 'multiple'(默认) 单选: 'single' 级联多选:
                // 'cascade'(同时选父和子);'parentCascade'(选父);'childCascade'(选子)
                id:'tree',
                onlyLeafCheckable : onlyLeafCheckable, // 是否只有叶子节点可选
                rootVisible : true,
                height : 400 - 68,
                //autoHeight:true,
                draggable : false,
                //width:width - 16,
                margins : '0 0 0 0',
                autoScroll : true,
                animate : true,
                enableDD : false,
                root : root, // You must define the root variable before when you set
                // the root config
                loader : new Ext.tree.TreeLoader( {
                    dataUrl : dataUrl,
                    baseAttrs : {
                        uiProvider : Ext.ux.TreeCheckNodeUI
                    }
                })
            });
            root.expand();

            var win = new Ext.Window( {
                title : '请选择',
                id : 'win',
                plain:true,
                height : 400,
                width:250,
                //autoScroll : true,
                layout:"form",
                labelWidth:60,
                shadow :false,
                shadowOffset:0,
                animCollapse:true,
                modal : true, // 遮罩
                items : tree
            });

            var ok = new Ext.Button({
                id:"ok",
                text:"确定"
            });
            ok.on("click",function() {
                var array = getSelectValue();  //选中值array[0] 为ID，array[1]为名称,格式都为"xx,xx"
                if(array[0].length == 0){
                    Ext.Msg.alert("提示","您还没有选择！")
                    return;
                }
                if (typeof (returnName) != "undefined" && returnName != "") {
                    document.getElementById(returnName).value = array[1];
                }
                if (typeof (returnId) != "undefined" && returnId != "") {
                    document.getElementById(returnId).value = array[0];
                }
                Ext.getCmp("win").close();
            })
            var clear = new Ext.Button({
                id:"clear",
                text:"取消"
            });
            clear.on("click",function () {
                //  var array = getSelectValue();;  //选中值array[0 为ID，array[1]为名称,格式都为"xx,xx"
                if (typeof (returnName) != "undefined" && returnName != "") {
                    document.getElementById(returnName).value = "";
                }
                if (typeof (returnId) != "undefined" && returnId != "") {
                    document.getElementById(returnId).value = "";
                }
                Ext.getCmp("win").close();
            })
            win.addButton(ok);
            win.addButton(clear);
            win.show(document);
        }

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
                    <m:extCRUDButtons model="btnAndMenu" excluded="[]" />
                    <m:extMoreButtons items="[[text:'多选树', handler:'showTree()']]" />

                    %{--<m:extSingleSelectButton model="btnAndMenu" excluded="[]" />
                    <m:extMultiSelectButton model="btnAndMenu" excluded="[]" />
                    <m:extImportButton model="btnAndMenu" />
                    <m:extExportButton model="btnAndMenu" />--}%
                    '->',
                    %{--<m:extGangCombo model="btnAndMenu" combo="[[label:'项目',name:'select_app_code',controller:'btnAndMenu',action:'combo1'],[label:'版本',name:'select_app_version',controller:'btnAndMenu',action:'combo2']]" />--}%
                    <m:extSearchButtons model="btnAndMenu" mode="standard" excluded="[]" />
                ]
            });

            <m:extGridStore model="btnAndMenu" excluded="[]" />
            <m:extGrid model="btnAndMenu" excluded="[]" config="[column:8,rowHeight:25]" />

            <m:extLayout model="btnAndMenu" />

            btnAndMenuStore.loadPage(1); //extGridStore配置name属性时，需要手动修改此属性
        });
</script>
</head>
<body style="overflow: auto;"></body>
</html>
