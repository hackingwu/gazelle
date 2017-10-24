package framework

import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.joda.time.LocalDate

import java.util.regex.Pattern


/**
 * 用于Domain数据与View数据交换时的格式转换
 * @author linyu, lichb
 * @version 2014-07-22
 */
class ViewOperationService {

    static transactional = false
    def grailsApplication
    Map<String, DefaultGrailsDomainClass> entityMap=[:]

    /**
     * 转换从View提交到Controller的数据
     * @param model 模型名称，类似: student, company
     * @param params, gsp表单提交的数据
     * @return 适合存储到数据库的domain实体
     */
    Map ConvertFromView(String model, Map params){
        Map domainModel=ModelService.GetModel(model)

        domainModel.fields.each{Map field ->
            //对于日期类型，需要从String转换成Date
            if(field.type == "date" && params[field.name])
            {
                try {
                    if(field.widget == "datetimefield"){
//                        params[field.name] = new Date().parse("yyyy-MM-dd HH:mm:ss", params[field.name])
                          params[field.name] = new Date().parse("yyyy-MM-dd HH:mm", params[field.name])
                    }else{
                        if(params[field.name] instanceof  LocalDate){ //Bug fixed:防止传入的已经是一个时间对象，例如导入xlsx后缀的excel，经过poi解析后，1998/09/08会变为1988-08-26格式的时间对象，1988-08-26则为字符串
                            params[field.name] = new Date().parse("yyyy-MM-dd", params[field.name].toString()) //LocalDate类型需要转换成string后再转换
                        }else{
                            params[field.name] = new Date().parse("yyyy-MM-dd", params[field.name])
                        }
                    }
                }catch(Exception e)
                {
                    log.error(e.message)
                }
            }else if(field.widget == "checkbox") //复选框需要将list类型的值转换成字符串保存
            {
                if(params[field.name]) {
                    try{
                        params[field.name] = (params[field.name]).join(",")
                    }catch(Exception e){
                        params[field.name] = params[field.name]
                    }
                }else{
                    params[field.name] = "" //bug：解决没有选中任何checkbox时，发往服务器的值为null的问题
                }
            }
            //long,int类型必须赋值,如果不赋值，数据库默认设置为-1
            else if((field.type == "long" || field.type == "int" || field.type == "float" ) && (params[field.name]=="")){
                params[field.name] = -1
            }else if(field.type == "boolean"){
	            params[field.name] = Boolean.valueOf(params[field.name])
            }

        }

        return params
    }

	/**
     * 转换从Controller发送到View的数据
     * @param model 模型名称，类似: student, company
     * @param instance 实体对象
     * @return Map 包含实体属性的Map
     */
    Map ConvertToView(String model, instance){
		Map domainModel=ModelService.GetModel(model)

		Map entity=[:]

		entity.id=instance.id

		domainModel.transFields.each{Map field ->

			//多表合并显示
			if(field.constraint?.fromDomain){
				def fromDomain = grailsApplication.getArtefactByLogicalPropertyName("Domain",field.constraint.fromDomain).clazz.find({
					eq field.constraint.fromDomainId, instance.id
				})//fromDomain是关联的Domain对象
				if(fromDomain!=null){
					entity[field.name]=fromDomain[field.name]
				}
				if(field.type == "boolean"){
					entity[field.name] = entity[field.name].toString()
				}
			//日期类型的处理
			}else if(field.type == "date" && instance[field.name])
			{
				if(field.widget == "datetimefield"){
					entity[field.name]=instance[field.name].format("yyyy-MM-dd HH:mm:ss")
				}else{
					entity[field.name]=instance[field.name].format("yyyy-MM-dd")
				}
			}
			//对象类型处理
			else if(field.type == "long" && field.constraint.relation && field.constraint.domain)
			{
				if(entityMap[field.constraint.domain]==null)
				{
					entityMap[field.constraint.domain]=grailsApplication.getArtefactByLogicalPropertyName("Domain",field.constraint.domain)
				}

				try{
					entity[field.name]=instance[field.name]
					entity[field.name+"Value"]= (instance[field.name]==-1?"":entityMap[field.constraint.domain].clazz.get(instance[field.name]).toString())
				}catch(Exception e){
					entity[field.name]=-1
				}
			}
			//单选或者下拉框 值和Label转换
			else if(field.constraint.inList && field.constraint.inListLabel)
			{
				entity[field.name]=instance[field.name]
				List list = field.constraint.inList
				List listLabel = field.constraint.inListLabel
				for(int i=0;i<list.size();i++){
					if(list[i]==instance[field.name]){
						entity["${field.name}Lable"] = listLabel[i]
					}
				}
			}
			//复选框类型,字符串值要转换成数组
			else if(field.widget == "checkbox")
			{
				try {
					def reg = /,|，/
					entity[field.name]=instance[field.name].split(reg).toList()
				}catch(Exception e)
				{
					entity[field.name]=[]
				}
			}
			//long,int类型-1表示无意义
			else if(field.type == "long" /*|| field.type == "int"*/){
				entity[field.name]=(instance[field.name]!=-1?(instance[field.name]):-1) //关联区域时的处理
			}
			//radio的值只能指定为字符串
			else if(field.type == "boolean"){
				entity[field.name] = instance[field.name]?'true':'false'
			}
			else if(field.type == "list"){
				if(instance[field.name]==null||instance[field.name].size()==0){
					entity[field.name] = []
				}else{
					String value = instance[field.name]?.toString()
					entity[field.name] = value.substring(1,value.length()-1)
				}
			}
			//其他无需处理的类型
			else{
				entity[field.name]=instance[field.name]
			}
		}

		return entity
	}

	Map ConvertToViewAll(String model, instance){
		ConvertToView(model,instance,null,null)
	}

	/**
	 * 对象转换成map传递到前端的处理
	 * @param model 模型名称，类似: student, company
	 * @param instance 实体对象
	 * @param included 对象中要转换的属性
	 * @return Map 包含实体属性的Map
	 */
	Map ConvertToViewIncluded(String model, instance,List included){
		ConvertToView(model,instance,included,null)
	}
	/**
	 * 对象转换成map传递到前端的处理
	 * @param model 模型名称，类似: student, company
	 * @param instance 实体对象
	 * @param excluded 对象中不需要转换的属性
	 * @return Map 包含实体属性的Map
	 */
	Map ConvertToViewExcluded(String model, instance,List excluded){
		ConvertToView(model,instance,null,excluded)
	}


	/**
	 * 对象转换成map传递到前端的处理 included 和 excluded写其一即可
	 * @param model 模型名称，类似: student, company
	 * @param instance 实体对象
	 * @param included 对象中要转换的对属性
	 * @param excluded 对象中不需要转换的属性
	 * @return Map 包含实体属性的Map
	 */
	Map ConvertToView(String model,instance,List<String> included,List<String> excluded){
		Map domainModel=ModelService.GetModel(model)

//		List transFields = domainModel.transFields

		Map entity=[:]

		entity.id=instance.id

		//正则匹配 排除属性
//		if(excluded){
//			for(int i=0; i<transFields.size();i++)
//			{
//				boolean flag = false //是否匹配正则
//				for(int j=0;j<excluded.size();j++){
//					Pattern pattern = Pattern.compile(excluded[j])
//					if(pattern.matcher(transFields[i].name).matches()){
//						flag = true
//					}
//				}
//				if(flag){
//					print(transFields[i])
//					transFields.remove(i)
//				}
//			}
//		}

		domainModel.transFields.each{Map field ->
			if((included!=null && included?.contains(field.name))||(excluded!=null && !excluded?.contains(field.name))
					||(included==null && excluded == null)){
//				if(converter != null && converter?.containsKey(field.name)){
//					entity.putAll(converter.get(field.name))
//				}else{//通用处理
					//日期处理
					if(field.type == "date" && instance[field.name])
					{
						if(field.widget == "datetimefield"){
							entity[field.name]=instance[field.name].format("yyyy-MM-dd HH:mm:ss")
						}else{
							entity[field.name]=instance[field.name].format("yyyy-MM-dd")
						}
					}
					else if(field.type == "long" && field.widget == 'area'){
						try {
							if(entityMap[field.constraint.domain]==null)
							{
								entityMap[field.constraint.domain]=grailsApplication.getArtefactByLogicalPropertyName("Domain",field.constraint.domain)
							}
							entity[field.name]=instance[field.name]
							entity[field.name+"Name"] = (instance[field.name]==-1?"":entityMap[field.constraint.domain].clazz.findByAreaId(instance[field.name])?.areaName)
						} catch (e) {
							log.error(e.getMessage())
							entity[field.name]= -1
							entity[field.name+"Name"]= ""
						}
					}
					//long数据类型的对象类型处理 如：long orgId
					else if(field.type == "long" && field.constraint.relation && field.constraint.domain)
					{
						try {
							if(entityMap[field.constraint.domain]==null)
							{
								entityMap[field.constraint.domain]=grailsApplication.getArtefactByLogicalPropertyName("Domain",field.constraint.domain)
							}
							entity[field.name]=instance[field.name]
							entity[field.name+"Name"]= instance[field.name]==-1?"":(entityMap[field.constraint.domain].clazz.get(instance[field.name]).toString())
						} catch (e) {
							log.error(e.getMessage())
							entity[field.name]= -1
							entity[field.name+"Name"]= ""
						}
					}
					//对象类型数据处理
					else if(field.include){
						entity[field.name]=(instance[field.name])?.id
						entity[field.name+"Name"]= (instance[field.name]== -1?"":(instance[field.name]).toString())
					}
					//单选或者下拉框 值和Label转换
					else if(field.constraint.inList && field.constraint.inListLabel)
					{
						entity[field.name]=instance[field.name]
						List list = field.constraint.inList
						List listLabel = field.constraint.inListLabel
						for(int i=0;i<list.size();i++){
							if(list[i]==instance[field.name]){
								entity["${field.name}Lable"] = listLabel[i]
							}
						}
					}
					//复选框类型,字符串值要转换成数组
					else if(field.widget == "checkbox")
					{
						try {
							if(entityMap[field.constraint.domain]==null)
							{
								entityMap[field.constraint.domain]=grailsApplication.getArtefactByLogicalPropertyName("Domain",field.constraint.domain)
							}

							def reg = /,|，/
							List convertList = []
							List list = instance[field.name].split(reg).toList()
							list?.each {
								long id = Long.valueOf(it)
								String name = entityMap[field.constraint.domain].clazz.get(id).toString()
								convertList << [id: id, name: name]
							}
							entity[field.name]= convertList
						}catch(Exception e)
						{
							log.error(e.getMessage())
							entity[field.name] = [[id: -1, name: ""]]
						}
					}
					//布尔数据类型处理
					else if(field.type == "boolean"){
						entity[field.name] = Boolean.valueOf(instance[field.name])
					}
					//List数据类型处理
					else if(field.type == "list"){
						if(instance[field.name]==null||instance[field.name].size()==0){
							entity[field.name] = "无"
						}else{
							String value = instance[field.name]?.toString()
							entity[field.name] = value.substring(1,value.length()-1)
						}
					}
					//其他无需处理的类型
					else{
						entity[field.name]=instance[field.name]
					}
				}
			}
//		}

		return entity
	}
}