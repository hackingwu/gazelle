<%= packageName ? " package  ${packageName} \n\n ": '' %>


import framework.ModelService
import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonSlurper

@Transactional(readOnly = true)
class ${className}Controller {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def viewOperationService

    /**
     * AUTO: 调用拦截器
     */
//    def beforeInterceptor = {
//        log.info "\${new Date()} params:\${params}"
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
        Map domain =ModelService.GetModel("${domainClass.propertyName}")

        Closure c = { Map d, Map p, String mode->
            String searchKey = p.searchKey
            String searchValue = p.searchValue

            String sortProperty
            String sortDirection

            if(mode!="count" && p.sort){
                List<Map> sort = new JsonSlurper().parseText(p.sort)
                sortProperty = sort[0].property
                sortDirection = sort[0].direction
            }

            if (searchKey && searchValue) {
                String type = d.fieldsMap[searchKey].type

                if (type == "string") {
                    ilike searchKey, "%\${searchValue}%"
                } else if (type == "int") {
                    eq searchKey, Integer.parseInt(searchValue)
                }
            }

            if (mode!="count" && p.sort) {
                order(sortProperty, sortDirection.toLowerCase())
            }
        }

        long count = ${className}.createCriteria().count(c.curry(domain, params, "count"))
        List <${className}> datas = ${className}.createCriteria().list([max: params.limit, offset: params.start], c.curry(domain, params, "list"))

        List<Map> datasToView = []

        for(int i=0;i<datas?.size();i++)
        {
            datasToView << viewOperationService.ConvertToView("${domainClass.propertyName}", datas[i])
        }

        String output = [success: true, message: "", totalCount: count, data: datasToView] as JSON

        render output
    }

    /**
     * AUTO: 创建实例
     */
    @Transactional
    def create() {
        Map paramsMap = viewOperationService.ConvertFromView("${domainClass.propertyName}", params)

        new ${className}(paramsMap).save(failOnError: true, flash:true)

        render([success: true, message: "创建成功!"] as JSON)
    }

    /**
     * AUTO: 明细
     */
    def detail(){
        ${className} ${domainClass.propertyName} = ${className}.get(params["id"])

        String output = [
                success: true,
                message: "",
                data: viewOperationService.ConvertToView("${domainClass.propertyName}", ${domainClass.propertyName})
        ] as JSON

        render output
    }

    /**
     * AUTO: 更新
     */
    @Transactional
    def update(){
        ${className} ${domainClass.propertyName} = ${className}.get(params["id"])

        ${domainClass.propertyName}.properties = viewOperationService.ConvertFromView("${domainClass.propertyName}", params)

        ${domainClass.propertyName}.save(failOnError: true, flash: true)

        String output = [success: true, message: "更新成功!"] as JSON
        render output
    }

    /**
     * AUTO：删除
     */

    @Transactional
    def delete(){
        List<Long> ids=params.data.split(",").collect{Long.parseLong(it)}

        List<${className}> ${domainClass.propertyName}s=${className}.findAllByIdInList(ids)

        for(int i=0;i<${domainClass.propertyName}s.size();i++){
            ${domainClass.propertyName}s[i].delete(flush: true)
        }

        String output = [success: true, message: "删除成功!"] as JSON
        render output
    }
}