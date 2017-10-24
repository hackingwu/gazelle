 package  com.nd.grails.plugins.log

 import export.ImportInfo
 import framework.CommonExportService
 import framework.ModelService
 import framework.util.MD5Util

 //导入依赖包
import grails.converters.JSON
 import groovy.json.JsonSlurper
 import grails.transaction.Transactional

 //import org.apache.poi.ss.usermodel.WorkbookFactory  //导入依赖包
import org.springframework.web.multipart.MultipartFile //导入依赖包

/**
 * @className LogController
 * @author
 * @since  Tue Aug 26 14:18:09 CST 2014
 */
class LogController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    static buttons = [
            [title:'查看日志',label:'查看',isMenu: false,parent: 'log_index',code: 'log_detail'],
            [title:'导出日志',label:'导出',isMenu: false,parent: 'log_index',code: 'log_export']
    ]

    def viewOperationService
    def logService
    def excelImportService
    def importInfoService
    def redisExportQueueService

    /**
     * AUTO: 调用拦截器
     */
//    def beforeInterceptor = {
//        println "${new Date()} params:${params}"
//    }

    /**
     * AUTO: 静态列表(数据通过JSON方式加载)
     */
    def index() {

    }

    /**
     * AUTO: 提供列表数据
     */
    def list() {
        String output
        try {
            logService.cacheEmpty()
            String userRole = session.roles
            if(userRole=='areaAdmin'){
                //只能看info级别的
                List<Map> searchs = new JsonSlurper().parseText(params.search)
                searchs << ["key":"level","value":"1"]
                params.search = (String)(searchs as JSON)
            }
            long count = logService.count(params)
            List <Log> datas = logService.list(params)

            List<Map> datasToView = []

            for(int i=0;i<datas?.size();i++)
            {
                datasToView << viewOperationService.ConvertToView("log", datas[i])
            }

            output = [success: true, message: "", totalCount: count, data: datasToView] as JSON
        }catch (e){
            output = [success: false, message: "请输入正确的查询条件！"] as JSON
        }
        render output


    }

    /**
     * 导出数据
     */
    def exportData(){
        String output
        //行首
        Map domain = ModelService.GetModel('log')
        List fields = new ArrayList()
        Map fullFieldsMap = new HashMap()
        domain.transFields.each{
            fields.add(it.cn)
            fullFieldsMap.put(it.name,it.cn)
        }

        //不导出的列
        List excluded = ["level","application","module","objectId"]
        excluded.each {
            fields.remove(fullFieldsMap.get(it))
        }

        //记录日志
        logService.info(new Log(catalog: "系统日志",operator: session.login,logMsg: "日志导出"))

        //文件名 用户-md5值.xlsx
        String fileName = "export-${session["login"]}-${CommonExportService.getFileName()}.xlsx"

        //导出信息保存
        ImportInfo fileInfo = new ImportInfo()
        fileInfo.userName =session["login"]
        fileInfo.fileStatus =3
        fileInfo.total = 0
        fileInfo.sucessConut = 0
        fileInfo.filePath = ""
        fileInfo.fileName = fileName;
        fileInfo.md5 = MD5Util.getMD5String(fileName)
        fileInfo.operType = 12
        fileInfo.operTime = new Date()
        importInfoService.save(fileInfo)

        String export = [importInfoId:fileInfo.id,fields:fields] as JSON

        try{
            output = redisExportQueueService.logExport(export)
            Map result = new JsonSlurper().parseText(output)

            if(result.success == false){
                fileInfo.fileStatus = 5
                importInfoService.update(fileInfo)
            }
        }catch (Exception e){
            fileInfo.fileStatus = 5
            importInfoService.update(fileInfo)

            output = (["success":false,"message":"导出失败"] as JSON)
        }

        render output
    }


    /**
     * 导入数据，只支持excel格式
     */
    def importData(){
        MultipartFile file = request.getFile('upload-file')

        //定义导入列与domain属性字段的映射关系，需要根据具体业务修改
        Map config = [
            sheet:'员工', //数据所在的sheet
            startRow: 1,  //有效数据起始行
            columnMap:  [ //domain class与excel列之间的映射
                'A':'companyId', //A列值对应domain class的companyId值
                'B':'name',      //B列值对应domain class的name值
                'C':'gender'     //C列值对应domain class的gender值
            ]
        ]

        //读取excel数据
        List members = excelImportService.columns(WorkbookFactory.create(new ByteArrayInputStream(file.bytes)), config)

        List<String> errorMessages = [] //字段校验错误信息保存
        int index = 2

        //校验每行字段值的合法性，具体验证规则需要根据具体业务修改
        members.each {it ->
            StringBuilder message = new StringBuilder()

            //companyId字段值校验
            if(it['companyId'] > 88){
                message.append(',公司代号必须小于88') //输出出错信息
            }

            //name字段值校验
            if(it['name']?.length() > 15){
                message.append(',员工姓名长度超出15个字符') //输出出错信息
            }

            //如果导入的数据非法，向用户输出错误信息，包括出错行及错误内容
            if(message.length() > 0){
                message.insert(0,"行${index++}数据验证出错")
                message.append('.')
                errorMessages.add(message.toString())
            }
        }

        def output
        if(errorMessages.isEmpty()){ //导入成功
            output = """{ success: true, message: "导入成功！"}"""
        }else { //数据非法，导入失败
            output = """{ success: false, message: "导入失败！",errorMessages:${errorMessages as JSON}}"""
        }
        render output
    }

    /**
     * AUTO: 为对象关联查询提供查询数据
     */
    def association(){


        long count = logService.count(params)
        List <Log> datas = logService.list(params)

        List<Map> datasToView = []

        for(int i=0;i<datas?.size();i++)
        {
            datasToView << [id: datas[i].id, text: datas[i].toString()]
        }

        String output = [success: true, message: "", totalCount: count, data: datasToView] as JSON

        render output
    }

    /**
     * AUTO: 创建实例
     */
    @Transactional
    def createAction() {
        Map paramsMap = viewOperationService.ConvertFromView("log", params)

        logService.save(new Log(paramsMap))

        render([success: true, message: "创建成功!"] as JSON)
    }

    /**
     * AUTO: 明细
     */
    def detailAction(){
        Log log = logService.findById(params["id"])

        String output = [
                success: true,
                message: "",
                data: viewOperationService.ConvertToView("log", log)
        ] as JSON

        render output
    }

    /**
     * AUTO: 更新
     */
    @Transactional
    def updateAction(){
        Log log = logService.findById(params["id"])

        log.properties = viewOperationService.ConvertFromView("log", params)

        logService.save(log)

        String output = [success: true, message: "更新成功!"] as JSON
        render output
    }

    /**
     * AUTO：删除
     */

    @Transactional
    def deleteAction(){
        List<Long> ids=params.data.split(",").collect{Long.parseLong(it)}

        ids.each {
            logService.deleteById(it)
        }

        String output = [success: true, message: "删除成功!"] as JSON
        render output
    }

    /**
     * AUTO：加载树节点
     */

    def treeList() {
        long id = Long.parseLong(params.node?:"1") //当前tree节点的ID值
        List<Log> nodeDatas=Log.findAllByParentId(id)
        List<Map> outputList=[];
        for(int k=0;k<nodeDatas.size();k++){
            outputList.add(['id':nodeDatas[k].id,'text':nodeDatas[k].toString(),leaf:Log.findAllByParentId(nodeDatas[k].id).size()==0])
        }

        render outputList as JSON
    }

    /**
     * AUTO：文件上传处理函数
     * 应答格式:
     *           @成功 render ([success:true, message: "文件上传成功！"] as JSON)
     *           @失败 render ([success:false, message: "文件上传失败:磁盘空间不足！"] as JSON)
     */

    def fileUpload() {
        def f = request.getFile('upload-file')
        if (f.empty) {
            render """{ success: false, message: "文件不能为空！"}"""
            return
        }
        f.transferTo(new File("./${params.Filename}"))
        render """{ success: true, message: "文件已成功上传！"}"""
    }

    /**
     * AUTO：图片上传处理函数
     * 应答格式:
     *           @成功 render ([success:true, message: "图片上传成功！"] as JSON)
     *           @失败 render ([success:false, message: "图片上传失败:磁盘空间不足！"] as JSON)
     */

     def imageUpload() {
         def f = request.getFile('upload-image')
	     if (f==null || f.empty) {
		     render """{ success: false, message: "文件不能为空！"}"""
		     return
	     }
	     def fileName = f.getOriginalFilename() //获取文件名
	     def regex = grailsApplication.config.cube.demo.imageUploadRegex //图片类型的正则验证，配置在config文件中
	     if(!(fileName ==~ regex)){
		     render """{ success: false, message: "请选择图片类型(.jpg,.gif,.png,.jpeg,.bmp)且图片名称不能包含空格！"}"""
		     return
	     }
	     def size = f.getSize() //获取文件大小
	     def imageUploadMaxSize = grailsApplication.config.cube.demo.imageUploadMaxSize //图片上传的最大值限制，配置在config文件中
	     if(size > imageUploadMaxSize){
		     render """{ success: false, message: "文件大小不能超过${imageUploadMaxSize/(1024*1024)}MB！"}"""
		     return
	     }
	     def imageUploadPath = grailsApplication.config.cube.demo.imageUploadPath   //获取图片上传路径，配置在config文件中
	     File dir = new File(imageUploadPath)
	     if(!dir.exists()){
		     dir.mkdirs()
	     }
	     //获取文件MD5值
	     InputStream is = f.getInputStream();
	     def strMD5 = MD5Util.getFileMD5String(is)
	     File destFile = new File("${imageUploadPath}"+"//" + strMD5 + fileName.substring(fileName.lastIndexOf("."))) //目标文件：文件上传路径+MD5值+文件后缀
	     if(!destFile.exists()){
		     f.transferTo(destFile) //文件写入磁盘
	     }else{
		     render """{ success: false, message: "文件已经存在，请勿重复上传！"}"""
		     return
	     }
	     //设置对象要保存的值,在文件写入磁盘后调用Service中的Sava()方法给数据库添加一条记录
	     render """{ success: true, message: "图片已成功上传！"}"""
    }

    /**
     * 应答格式:
     *           @成功 render ([success:true, message: "文件上传成功！"] as JSON)
     *           @失败 render ([success:false, message: "文件上传失败:磁盘空间不足！"] as JSON)
     * */
    def multiFileUpload() {
        def f = request.getFile('Filedata')
        if (f.empty) {
            render """{ success: false, message: "文件不能为空！"}"""
            return
        }
        f.transferTo(new File("./${params.Filename}"))
        render """{ success: true, message: "文件上传成功"}"""
    }

    /**
     * AUTO：单选提交处理函数
     */

    def singleSelCommitAction() {
        String output = [success: true, message: "数据已提交服务器!"] as JSON
        render output
    }

    /**
     * AUTO：获取单选选择的内容
     */

    def getSingleSelRecord() {
        Log record = Log.findById(2)

        String output = [success: true, record: record] as JSON //如没有选中ID则返回[success: false, record: []]
        render output
    }

    /**
     * AUTO：多选提交处理函数
     */

    def multiSelCommitAction() {
        String output = [success: true, message: "数据已提交服务器!"] as JSON
        render output
    }

    /**
     * AUTO：获取多选选择的内容
     */

    def getMultiSelRecord() {
        List<Log> datas = Log.findAllById(1)
        List<Map> datasToView = [];
        if(datas.size()>=3){
            for (int k = 0; k < 3; k++) {
                datasToView << datas[k]
            }
        }
        render datasToView as JSON
    }

    /**
     * AUTO：单/多行选择提交处理函数
     */

    def rowSelAction() {
        String output = [success: true, message: "数据已提交服务器!"] as JSON
        render output
    }

    /**
     * toolbar的联动combo示例
     */
    def combo1(){
        String output = [
                ['name':'项目1','value':'1'],
                ['name':'项目2','value':'2'],
                ['name':'项目3','value':'3'],
                ['name':'项目4','value':'4'],
                ['name':'项目5','value':'5'],
                ['name':'项目6','value':'6']
        ] as JSON
        render output
    }

    /**
     * toolbar的联动combo示例
     */
    def combo2(){

        String output = [
                ['name':'版本1','value':'11'],
                ['name':'版本2','value':'22'],
                ['name':'版本3','value':'33'],
                ['name':'版本4','value':'44']
        ] as JSON

        if(params.select_app_code=="3"){
            output = [
                    ['name':'版本a1','value':'a11'],
                    ['name':'版本a2','value':'aa22'],
                    ['name':'版本a3','value':'aa33'],
                    ['name':'版本a4','value':'aa44']
            ] as JSON

        }
        render output
    }
}