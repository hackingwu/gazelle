package demo

import framework.biz.GenericService
import grails.transaction.Transactional

/**
 * @author Administrator
 * @since 2014-09-17
 */
@Transactional(readOnly = true)
class FormTreeViewService extends GenericService<FormTreeView,Long>{

    public FormTreeViewService(){
        super(FormTreeView.class)

    }

/**
 * 输入的参数为修改的配置信息对应的id
 * 同时遍历修改的配置信息对应的节点下所有子节点的配置信息
 */
    @Transactional
    Boolean updateConfig(long id){
        try{
            long rootId = Organization.findByFormTreeViewId(id).id

            function(rootId)
            return true
        }catch (Exception e){
            println e.getMessage()
            return false
        }

    }
    /**
     * 输入参数为执行遍历的树节点
     * 如果当前节点没有配置信息则将其父节点的配置信息读入
     * 如果当前节点没有叶节点则返回，如果有叶节点则迭代遍历其所有页节点
     */
    @Transactional
    void function(long id){
        Organization root = Organization.findById(id)

        if(root.formTreeViewId == null){
            //这里是直接new还是去调用service中的save方法

            FormTreeView formTreeView = new FormTreeView(
                    companyId: id,
                    name: FormTreeView.findById(Organization.findById(root.parentId).formTreeViewId).name,
                    gender: FormTreeView.findById(Organization.findById(root.parentId).formTreeViewId).gender,
                    date: FormTreeView.findById(Organization.findById(root.parentId).formTreeViewId).date,
                    flag: FormTreeView.findById(Organization.findById(root.parentId).formTreeViewId).flag
            ).save()

            root.formTreeViewId = formTreeView.id
            root.save()
        }



        if(Organization.countByParentId(id) != 0){
            //对所有该节点下的子节点进行遍历
            Organization.findAllByParentId(id).each {Organization it ->
                function(it.id)
            }
        }else{
            //页节点则返回
            return
        }

    }


}