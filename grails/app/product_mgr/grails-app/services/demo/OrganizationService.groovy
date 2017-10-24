package demo



import framework.ModelService
import grails.transaction.Transactional
import groovy.json.JsonSlurper

@Transactional(readOnly = true)
class OrganizationService {

    /**
     * 保存数据
     * @param organization
     */
    @Transactional
    def save(Organization organization) throws Exception {
        //
        organization.save();
    }

    /**
     * 修改数据
     * @param  organization
     * @return
     */
    @Transactional
    def update(Organization organization) throws Exception {
        organization.save()
        // organization.merge()
    }

    /**
     * 根据id删除
     * @param organization
     * @return
     */
    @Transactional
    def delete(Organization organization) throws Exception {
        organization.delete()
    }

    @Transactional
    void deleteById(Long id) {
        Organization organization = Organization.get(id)
        organization?.delete()
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    Organization findById(Serializable id) {
        return Organization.get(id);
    }

    /**
     * 计算总数
     * @param params
     * @return
     */
    long count(Map params)
    {
        Map domain = ModelService.GetModel("organization")

        return Organization.createCriteria().count(GetFilter().curry(domain, params, "count"))
    }

    /**
     * 获取分页数据
     * @param params
     * @return
     */
    List<Organization> list(Map params)
    {
        Map domain = ModelService.GetModel("organization")

        return Organization.createCriteria().list([max: params.limit, offset: params.start] ,GetFilter().curry(domain, params, "list"))
    }

    /**
     * 获取计算闭包
     * @return
     */

    Closure GetFilter()
    {
        Closure c = { Map d, Map p, String mode ->
            String sortProperty
            String sortDirection

            if (mode != "count" && p.sort) {
                List<Map> sort = new JsonSlurper().parseText(p.sort)
                sortProperty = sort[0].property
                sortDirection = sort[0].direction
            }

            if(p.search){
                List<Map> search = new JsonSlurper().parseText(p.search)

                for(Map record:search){
                    if (record.key && record.value &&d.fieldsMap[record.key]){
                        String type = d.fieldsMap[record.key].type
                        if (type == "string") {
                            ilike record.key, "%${record.value}%"
                        }else if (type == "int") {
                            eq record.key, Integer.parseInt(record.value)
                        }else if (type == "long") {
                            eq record.key, Long.parseLong(record.value)
                        }
                    }
                }
            }

            if (mode != "count" && p.sort) {
                order(sortProperty, sortDirection.toLowerCase())
            }
        }

        return c
    }
}