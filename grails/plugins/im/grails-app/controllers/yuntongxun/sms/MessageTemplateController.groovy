 package yuntongxun.sms


 import framework.ModelService
 import framework.ValidateDomainService
 import framework.ViewOperationService
import grails.converters.JSON
import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.InitializingBean
 import org.springframework.validation.Errors
 import org.springframework.web.servlet.ModelAndView

class MessageTemplateController implements InitializingBean{

    static buttons = [
            //这些配在配置管理里的页面，都需要在对应的controller里去配置一个页面
            [title:"新建messageTemplate",label:'新增',isMenu: false,parent: "messageTemplate_index",code: "messageTemplate_create"],
            [title:"删除messageTemplate",label:'删除',isMenu: false,parent: "messageTemplate_index",code: "messageTemplate_delete"],
            [title:"更新messageTemplate",label:'更新',isMenu: false,parent: "messageTemplate_index",code: "messageTemplate_update"],
            [title:"查看messageTemplate",label:'查看',isMenu: false,parent: "messageTemplate_index",code: "messageTemplate_detail"]
    ]
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    protected Class<MessageTemplate> persistentClass = null//(Class<T>) ((ParameterizedType)(this.getClass().getGenericSuperclass())).getActualTypeArguments()[0]

    protected String modelName

    private String serviceName

    def viewOperationService

    MessageTemplateService service

    def exportService

    def excelImportService

    GrailsApplication grailsApplication


    public MessageTemplateController() {

    }



    /**
     * AUTO: 调用后置拦截器
     */
    def afterInterceptor = { model, modelAndView ->
        // println "afterInterceptor"
    }

    def handleException(Exception e) {

        /**
         * TODO 应用错误处理接入
         */

//        e.printStackTrace()
        log.error(e.getMessage())
        if(this.isAjaxRequest()){
            String output = [success: false, message: "出错了,${e.message}！"] as JSON
            log.error('',e)
            render output
            return
        }else {
            return new ModelAndView('/error')
        }
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
        String output
        try {
            long count = service.count(params)
            List <MessageTemplate> datas = service.list(params)

            List<Map> datasToView = []

            for(int i=0;i<datas?.size();i++)
            {
                datasToView << viewOperationService.ConvertToView(this.modelName, datas[i])
            }

            output = [success: true, message: "", totalCount: count, data: datasToView] as JSON
        }catch (e){
            log.error('',e)
            output = [success: false, message: "请输入正确的查询条件！"] as JSON
        }
        render output
    }

    /**
     * AUTO: 使用全文检索提供列表数据
     */
    def search(){
        String output

        Map searchResult = service.search(params)

        long count =  searchResult['total'] //userSearchableService.count(params)
        List <MessageTemplate> datas =  searchResult['results'] //userSe

        List<Map> datasToView = []

        for(int i=0;i<datas?.size();i++)
        {
            datasToView << viewOperationService.ConvertToView(this.modelName, datas[i])
        }

        output = [success: true, message: "", totalCount: count, data: datasToView] as JSON

        render output
    }

    /**
     * AUTO: 创建实例
     */
    def createAction() {
        Map paramsMap = viewOperationService.ConvertFromView(this.modelName, params)
        MessageTemplate messageTemplate = persistentClass.newInstance([paramsMap] as Object[])
        Errors errors = messageTemplate.errors
        if (errors.hasErrors()){
            Map errorMessage = ValidateDomainService.getErrorMessage(errors)
            render ([success:false,message:errorMessage.toString()] as JSON);return ;
        }
        service.save( messageTemplate)

        render([success: true, message: "创建成功!"] as JSON)
    }

    /**
     * AUTO: 明细
     */
    def detailAction(){
        MessageTemplate instance = service.findById(params["id"])

        String output = [
                success: true,
                message: "",
                data: viewOperationService.ConvertToView(this.modelName, instance)
        ] as JSON

        render output
    }

    /**
     * AUTO: 更新
     */
    def updateAction(){
        MessageTemplate instance = service.findById(params["id"])

        address.properties = viewOperationService.ConvertFromView(this.modelName, params)

        service.save(instance)

        String output = [success: true, message: "更新成功!"] as JSON
        render output
    }

    /**
     * AUTO：删除
     */

    def deleteAction(){
        List<Long> ids=params.data.split(",").collect{Long.parseLong(it)}

        ids.each {
            service.deleteById(it)
        }

        String output = [success: true, message: "删除成功!"] as JSON
        render output
    }


    /**
     * 导出数据
     */
    def exportData(){
        List <MessageTemplate> datas = service.list(params)

        response.setHeader("Content-disposition", "attachment; filename=${this.modelName}.xls")

        Map domain=ModelService.GetModel(this.modelName)
        List fields = []
        Map labels = [:]

        for(int i=0; i<domain.transFields.size();i++){
            fields << domain.transFields[i].name
            labels.put(domain.transFields[i].name,domain.transFields[i].cn)
        }

        Map parameters = [title: domain.m.domain.cn]

        exportService.export('excel', response.outputStream, datas, fields, labels, [:], parameters)
    }


    /**
     * 导入数据，只支持excel格式
     */
    def importData(){
//        MultipartFile file = request.getFile('upload-file')
//
//        //定义导入列与domain属性字段的映射关系，需要根据具体业务修改
//        Map config = [
//                sheet:'员工', //数据所在的sheet
//                startRow: 1,  //有效数据起始行
//                columnMap:  [ //domain class与excel列之间的映射
//                              'A':'companyId', //A列值对应domain class的companyId值
//                              'B':'name',      //B列值对应domain class的name值
//                              'C':'gender'     //C列值对应domain class的gender值
//                ]
//        ]
//
//        //读取excel数据
//        List members = excelImportService.columns(WorkbookFactory.create(new ByteArrayInputStream(file.bytes)), config)
//
//        List<String> errorMessages = [] //字段校验错误信息保存
//        int index = 2
//
//        //校验每行字段值的合法性，具体验证规则需要根据具体业务修改
//        members.each {it ->
//            StringBuilder message = new StringBuilder()
//
//            //companyId字段值校验
//            if(it['companyId'] > 88){
//                message.append(',公司代号必须小于88') //输出出错信息
//            }
//
//            //name字段值校验
//            if(it['name']?.length() > 15){
//                message.append(',员工姓名长度超出15个字符') //输出出错信息
//            }
//
//            //如果导入的数据非法，向用户输出错误信息，包括出错行及错误内容
//            if(message.length() > 0){
//                message.insert(0,"行${index++}数据验证出错")
//                message.append('.')
//                errorMessages.add(message.toString())
//            }
//        }
//
//        def output
//        if(errorMessages.isEmpty()){ //导入成功
//            output = """{ success: true, message: "导入成功！"}"""
//        }else { //数据非法，导入失败
//            output = """{ success: false, message: "导入失败！",errorMessages:${errorMessages as JSON}}"""
//        }
//        render output
    }

    /**
     * AUTO: 为对象关联查询提供查询数据
     */
    def association(){
        long count = service.count(params)
        List <MessageTemplate> datas = service.list(params)

        List<Map> datasToView = []

        for(int i=0;i<datas?.size();i++)
        {
            datasToView << [id: datas[i].id, text: datas[i].toString()]
        }

        String output = [success: true, message: "", totalCount: count, data: datasToView] as JSON

        render output
    }

    /**
     * AUTO：加载树节点
     */

    def treeList() {
        long id = Long.parseLong(params.node?:"1") //当前tree节点的ID值
        List<MessageTemplate> nodeDatas= persistentClass.findAllByParentId(id)
        List<Map> outputList=[];
        for(int k=0;k<nodeDatas.size();k++){
            outputList.add(['id':nodeDatas[k].id,'text':nodeDatas[k].toString(),leaf:persistentClass.findAllByParentId(nodeDatas[k].id).size()==0])
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
        MessageTemplate record = persistentClass.findById(2)

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
        List<MessageTemplate> datas = persistentClass.findAllById(1)
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
     * 通过i18n取数据
     * @param code
     * @return
     */
    protected String getMessage(String code){
        return getMessage([code:code])
    }

    /**
     * 通过i18n取数据
     * @param code
     * @param args
     * @return
     */
    protected String getMessage(String code,List args){
        if(args == null){
            return getMessage([code:code])
        }
        return getMessage([code:code,args:args])
    }

    protected String getMessage(Map map){
        return message(map)
    }

    protected boolean isAjaxRequest(){
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("x-requested-with"))
    }

    void afterPropertiesSet() throws Exception {
        this.persistentClass = MessageTemplate.class

        char[] chars = persistentClass.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()

        modelName = new String(chars)

        serviceName = "MessageTemplateService"

        service = (MessageTemplateService)grailsApplication.mainContext.getBean("${modelName}Service")
    }
}