package framework
/**
* 生成extTree和extTreeStore的方法
* @className ExtTreeTagLib
* @author Su sunbin,Lch
* @version 2014-09-16 ExtTagLib中的extTree和extTreeStore标签拆分至此
* @version 2014-09-28 新增一个extFormTree标签，实现表单和树的联动
*/
class ExtTreeTagLib extends ExtTagLib{
	//static defaultEncodeAs = 'html'
	//static encodeAsForTags = [tagName: 'raw']

	/**
	 * 生成extjs tree的 Store
	 * @param attrs.model 模型名称，类似: student, company
	 * @return tree stroe的视图代码
	 */
	def extTreeStore = { attrs, body ->
		String model=attrs.model
		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName

		String output ="""
        ${propertyName}TreeStore = Ext.create('Ext.data.TreeStore', {
            fields: ['id', 'text'],
            proxy: {
                type: 'ajax',
                autoLoad: true,
                url: '${g.createLink([controller: propertyName, action: "treeList"])}'
            }
        });
    """
		out << output
	}

	/**
	 * 生成默认的extjs Tree
	 * @param attrs.model 模型名称，类似: student, company
	 * @param attrs.extra 而外的关联字段，用以区分变量，在一对多视图中使用
	 * @param attrs.width tree树的宽度
	 * @return tree视图代码
	 */
	def extTree = { attrs, body ->

		String model=attrs.model
		String extra = attrs.extra?:""
		String treeWidth = attrs.width?:"250"
		Map domain=ModelService.GetModel(model)
		String propertyName=domain.propertyName
//        String associatedDomain = domain.m.layout.domain

		String output ="""
        var ${propertyName}RootData;
        Ext.Ajax.request({
            async : false,
            url: '${g.createLink([controller: propertyName, action: "getRootNode"])}',
//            params: {
//                search: '[{"key":"parentId","value":"0"}];'
//            },
            success: function(response){
                ${propertyName}RootData = Ext.JSON.decode(response.responseText);
            }
        });

        ${propertyName}TreeNode = ${propertyName}RootData.id;

        ${propertyName}${extra}Tree = Ext.create('Ext.tree.Panel', {
            store: ${propertyName}TreeStore,
            width: ${treeWidth},
            height: ${controllerName}${extra}GridHeight-5,
//            resizable:true,
            useArrows: true,
//            stateful: true,
//            stateId: "test_stateId",
            root: {
                id: ${propertyName}RootData.id,
                text: ${propertyName}RootData.text,
                expanded: true
            },
            listeners:{
                itemclick: function (view, record, item, index, e, eOpts) {
                    ${propertyName}TreeNode =  record.data.id;
                    ${propertyName}TreeText =  record.data.text;

                    ${controllerName}${extra}Store.loadPage(1);
                }
            }
        });
    """
		out << output
	}

    /**
     * 生成默认的extjs 与Form关联的Tree
     * @param attrs.model 模型名称，类似: student, company
     * @param attrs.extra 而外的关联字段，用以区分变量，在一对多视图中使用
     * @param attrs.width tree树的宽度
     * @return tree视图代码
     */
    def extFormTree = { attrs, body ->

        String model=attrs.model
        String extra = attrs.extra?:""
        String treeWidth = attrs.width?:"200"
        Map domain=ModelService.GetModel(model)
        String propertyName=domain.propertyName
//        String associatedDomain = domain.m.layout.domain

        String output ="""
        var ${propertyName}RootData;
        Ext.Ajax.request({
            async : false,
            url: '${g.createLink([controller: propertyName, action: "getRootNode"])}',
//            params: {
//                search: '[{"key":"parentId","value":"0"}];'
//            },
            success: function(response){
                ${propertyName}RootData = Ext.JSON.decode(response.responseText);
            }
        });

        ${propertyName}TreeNode = ${propertyName}RootData.id;

        ${propertyName}${extra}Tree = Ext.create('Ext.tree.Panel', {
            store: ${propertyName}TreeStore,
            width: ${treeWidth},
            height: ${controllerName}${extra}FormHeight-5,
            useArrows: true,
            root: {
                id: ${propertyName}RootData.id,
                text: ${propertyName}RootData.text,
                expanded: true
            },
            listeners:{
                itemclick: function (view, record, item, index, e, eOpts) {
                    ${propertyName}TreeNode =  record.data.id;
                    ${propertyName}TreeText =  record.data.text;

                    var cmpForm = Ext.getCmp('${controllerName}Form').getForm();
                     cmpForm.load({
                                params: { id: record.data.id},
                                url: '${g.createLink([controller: controllerName, action: "detailAction"])} ',
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
            }
        });
    """
        out << output
    }

}
