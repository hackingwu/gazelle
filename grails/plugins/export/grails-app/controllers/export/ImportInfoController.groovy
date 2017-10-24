 package  export 

import framework.biz.GenericController
 import grails.converters.JSON
 import groovy.json.JsonSlurper

 /**
 *
 * @author Administrator
 * @since  2015-01-30
 */
class ImportInfoController extends GenericController<ImportInfo,Long> {

    static buttons = [
        [title:'查看详细',label:'查看详细',isMenu: false,parent: 'importInfo_index',code: 'importInfo_detail'],
        [title:'删除记录',label:'删除记录',isMenu: false,parent: 'importInfo_index',code: 'importInfo_delete']
    ]

    public ImportInfoController(){
        super(ImportInfo.class)
    }

    /**
     * AUTO: 静态列表(数据通过JSON方式加载)
     */
    def index() {

    }

    /**
     * AUTO: 提供列表数据
     */
    def list() {
        if(session["sysHospital"]){
            List<Map> search = new JsonSlurper().parseText(params.search)
            search.add(["key":"creatorId","value":"${session["adminId"]}"])
            params.search = (search as JSON)?.toString()
        }
        params << ["sort":'[{"property":"operTime","direction":"DESC"}]'] //导出记录按时间降序输出
        super.list()
    }

    /**
     * AUTO: 创建实例
     */
    def createAction() {
        if (session.adminId){
            params.creatorId = session.adminId
        }
        super.createAction()
    }

    /**
     * AUTO: 明细
     */
    def detailAction(){
        super.detailAction()
    }

    /**
     * AUTO: 更新
     */
    def updateAction(){
        super.updateAction()
    }

    /**
     * AUTO：删除
     */
    def deleteAction(){
        super.deleteAction()
    }

    /**
     *  导出出错的excel数据信息
     */
    def exportFile(){
        try{
            String subPath = grailsApplication.config.cpp.membrane.erp.exportFilePath
            String filePath = subPath+"/"+params.filename   //文件上传路径
            File file = new File(filePath)

            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment;filename=${params.filename}")

            BufferedInputStream resBuffer = file.newInputStream()
            response.outputStream << resBuffer
            resBuffer.close()
        }catch (e){
            log.error(e.getMessage())
        }
    }

    /**
     * 导出模板
     */
    def exportTplFile(){
        try{
            String fileName = "${params.clz}Tpl.xlsx"
            String filePath = grailsApplication.config.cpp.membrane.erp.exportFilePath+"/tpl/"+fileName   //文件名首字母小写
            File file = new File(filePath)

            response.setContentType("application/octet-stream")
            response.setHeader("Content-disposition", "attachment;filename=${fileName}")

            BufferedInputStream resBuffer = file.newInputStream()
            response.outputStream << resBuffer
            resBuffer.close()
        }catch (e){
            log.error(e.getMessage())
        }
    }
}