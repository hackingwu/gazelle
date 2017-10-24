package com.nd.cube.common.validator
/**
 * @author wuzj
 * @since 2015/1/6.
 */
class ValidatorUtil {

    static List<CubeValidator> getCubeValidator(List<Map> rule){
        List<CubeValidator> cubeValidatorList = new ArrayList<>()
        rule.each {
            CubeValidator cubeValidator = ValidatorFactory.build(it.key,it.value)
            cubeValidatorList.add(cubeValidator)
        }
        return cubeValidatorList
    }

    static Map<String,CubeValidator> getCubeValidatorMap(Map validator){
        Map cubeValidatorMap = new HashMap()
        validator.each {field,rule->
            cubeValidatorMap.put(field,getCubeValidator(rule))
        }
        return cubeValidatorMap
    }
    static CubeValidator getCubeValidator(Map rule){
        if (rule == null || rule.isEmpty()){
            return null
        }
        Iterator iterator = rule.keySet().iterator()
        String key = iterator.next()
        return ValidatorFactory.build(key,rule.get(key))
    }
    static Map getErrorMessage(List<Map> validator,Map values){

        Map errorMessages = new HashMap()
        validator.each {
            if (values.containsKey(it.field)){
                CubeValidator cubeValidator = getCubeValidator(it.rule)
                if (cubeValidator && (!cubeValidator.validate(values.get(it.field)))){
                    if (errorMessages.containsKey(it.field)){
                        errorMessages.put(it.field,errorMessages.get(it.field)+","+it.message)
                    }else{
                        errorMessages.put(it.field,it.message)
                    }
                }
            }
        }
        return errorMessages
    }
}
