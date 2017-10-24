package framework

import com.nd.cube.common.validator.CubeValidator
import com.nd.cube.common.validator.ValidatorUtil
import framework.util.StringUtil
import grails.converters.JSON
import grails.util.Holders
import org.springframework.validation.Errors

/**
 * @author wuzj
 * @version 2014/10/20
 */
class ValidateDomainService {

    static String validate(Object instance) {
        if (instance.validate()) {
            return ([success: true, message: "操作成功!"] as JSON)
        } else {
            return [success: false, message: getErrorMessage(instance.errors).toString()] as JSON
        }
    }
    /**
     *
     * @param instance 要校验的实例对象
     * @param included 要校验的字段
     * @return
     */
    static String validateIncluded(Object instance, List included) {
        if (instance.validate(included)) {
            return ([success: true, message: "操作成功!"] as JSON)
        } else {
            return [success: false, message: getErrorMessage(instance.errors).toString()] as JSON
        }
    }
    /**
     *
     * @param instance 要校验的实例对象
     * @param excluded 要排除校验的字段
     * @return
     */
    static String validateExcluded(Object instance, List excluded) {
        char[] chars = instance.class.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()
        String modelName = new String(chars)
        List transField = ModelService.GetModel(modelName).transFields.collect { return it.name } - excluded
        if (instance.validate(transField)) {
            return ([success: true, message: "操作成功!"] as JSON)
        } else {
            return [success: false, message: getErrorMessage(instance.errors).toString()] as JSON
        }
    }



    /**
     * 用i18n来做报错信息的中文转换
     * @param instance domain校验后的Errors对象
     * @return
     */
    public static Map getMyErrorMessage(Errors errors) {
        Map resultMap = [:]
        String model = StringUtil.toLowerCaseFirstOne(errors.objectName.substring(errors.objectName.lastIndexOf(".")+1))
        Map fieldMap = ModelService.GetModel(model).get("fieldsMap")
        errors.allErrors.each {
            String defaultMessage = it.defaultMessage
            Object[] arguments = it.arguments
            int i = 0;
            while (true) {
                if (defaultMessage.indexOf("[{${i++}}]") == -1) {
                    i--
                    break
                }
            }
            String notice = ""
            if (it.codes[0].indexOf("matches") != -1) {
                //是正则表达式的验证，则去掉正则表达式值，arguments[3
                arguments[3] = ""
                notice = fieldMap.get(it.field.toString()).constraint.notice
                if (!StringUtil.isEmpty(notice)){
                    notice = ",要求："+notice
                }
            }
            //如果存在error，则argument[0]为属性名，argument[1]为类名
            while (--i > -1) {
                defaultMessage = defaultMessage.replace("[{${i}}]", arguments[i].toString())
            }
            String index = "类的属性"
            defaultMessage = defaultMessage.substring(defaultMessage.indexOf(index) + 4).replace("null", "空")
            resultMap.put(arguments[0].toString(), defaultMessage+"${notice?:""}")
        }
        return resultMap
    }

//    static String getErrorMessageStr(Errors errors){
//        Map map = getErrorMessage(errors)
//        String s = null
//        map.values().each {
//            s += it + ","
//        }
//        s.substring(0,s.length())
//    }

    static String getErrorMessageStr(Errors errors){
        return getErrorMessage(errors).collect{it.message}.join(",");
    }

    static List<Map> getErrorMessage(Errors errors){
        def tagLib = Holders.grailsApplication.mainContext.getBean("org.codehaus.groovy.grails.plugins.web.taglib.ValidationTagLib")
//        tagLib.message(error:it)
        List<Map> errorMessage = new ArrayList<HashMap>()
        errors.fieldErrors.each {
            Map m = new HashMap()
            m.put("field",it.field)
            m.put("message",tagLib.message(error:it))
            errorMessage.add(m)
        }
        return errorMessage
    }
    /**
     * format:[field:xxx,value:xxx,rule:[:,:],message:[xxx,xxx]],rule
     * @param params
     * @return
     */
    static List<Map> validateMap(List<Map> params){
        List<Map> errorMessage = new ArrayList<HashMap>()
        int i = 0
        params.each {
            List<CubeValidator> cubeValidatorList = ValidatorUtil.getCubeValidator(it.get("rule"))
            if(cubeValidatorList!=null && !cubeValidatorList.isEmpty()){
                i = 0
                cubeValidatorList.each {cubeValidator->
                    if(it.get("value")!=null && !cubeValidator.validate(it.get("value"))){
                        Map m = ["field":it.get("field")]
                        String message = ""
                        List messageList = it.get("message")
                        if (messageList!=null && !messageList.isEmpty()){
                            if (i < messageList.size())
                                message = messageList.get(i++)
                            else
                                message = messageList.get(0)
                        }

                        m.put("message",message)
                        errorMessage.add(m)
                    }
                }
            }

        }
        return errorMessage
    }

    static List<Map> validateMap(Map valueMap,List<Map> ruleMap){
        List<Map> params = new ArrayList<>(ruleMap)
        List toRemove = []
        for (int i = 0 ;i < ruleMap.size();i++){
            String key = ruleMap.get(i).get("field")
            if (valueMap.containsKey(key)){
                params.get(i).put("value",valueMap.get(key))
            }else{
                toRemove.add(i)
            }
        }
        toRemove.each {
            params.remove(it)
        }
        return validateMap(params)
    }




    private static Map getModelTransFieldMap(Object instance) {
        char[] chars = instance.class.getSimpleName().toCharArray()
        chars[0] = chars[0].toLowerCase()
        String modelName = new String(chars)
        List transField = ModelService.GetModel(modelName).transFields
        Map transFieldMap = new HashMap()
        transField.each {
            transFieldMap.put(it.name, it)
        }
        return transFieldMap
    }

    /**
     * 计算Domain信息填写完整度
     * 如果是数字类型的，当其大于-1，则算其有填写（下拉的不填即为-1）
     * 其他类型（包括对象）传过来的都是string类型，不为空则算其有填
     * 隐藏字段，算其有填
     * @param model Domain的propertyName，例如volunteer，team
     * @param params 需要计算的Map
     * @return
     */
    static int calculateDomainCompletion(String model, Map params) {
        List transField = ModelService.GetModel(model).transFields
        Map transFieldMap = new HashMap()
        transField.each {
            transFieldMap.put(it.name, it)
        }
        int i = 0
        Map flagMap = new HashMap()
        Map triggerMap = new HashMap()
        params.each {
//            flagMap.put(it.key, 0)
            if (transFieldMap.containsKey(it.key)) {
                flagMap.put(it.key,0)
                Map constraint = transFieldMap.get(it.key)
                if (constraint?.trigger?.listeners == "change" && constraint?.trigger?.trigger) {
                    triggerMap.put(constraint.trigger.trigger, it.value)
                }
                if (constraint.type.matches("int|float|long|double")) {
                    if ("${it.value}" && Double.valueOf(it.value) > -1)//数字转成double类型，不会丢失精度
                        flagMap.put(it.key, 1)
                } else {
                    if ("${it.value}" && it.value !=null )
                        flagMap.put(it.key, 1)
                }
            }
        }

        transFieldMap.each { fieldMap ->
            if (fieldMap?.value?.register?.listeners && triggerMap.containsKey(fieldMap.value.register.listeners)) {
                fieldMap.value.register.defaultAction.each {
                    if (String.valueOf(it.value) == triggerMap.get(fieldMap.value.register.listeners) && it.action == "hide") {
                        flagMap.put(it.key, 1)
                    }
                }
            }
        }
        flagMap.collect {
            if (it.value > 0) {
                i++
            }
        }
        return i / flagMap.size()*100//不留小数点

    }


}
