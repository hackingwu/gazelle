package com.nd.cube.common.validator

import framework.util.StringUtil

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class ValidatorFactory {
    private static BlankValidator notBlankValidator = new BlankValidator(false)
    private static CreditCardValidator creditCardValidator = new CreditCardValidator(true)
    private static EmailValidator emailValidator = new EmailValidator(true)
    private static NullableValidator nullableValidator = new NullableValidator(true)
    private static UrlValidator urlValidator = new UrlValidator(true)
    private static CubeValidator build(String name,Object arg){
        try{
            return "get${StringUtil.toUpperCaseFirstOne(name)}Validator"(arg)
        }catch (e){
            return null //不支持的校验方式，返回null
        }
    }
    private static CubeValidator getBlankValidator(Object arg){
        if (arg instanceof Boolean){
            if (arg == false){
                return notBlankValidator
            }
            else{
                return new BlankValidator(true)
            }
        }
        return null
    }
    private static CubeValidator getCreditCardValidator(Object arg){
        if (arg instanceof Boolean){
            if (arg == true){
                return creditCardValidator
            }else{
                return new CreditCardValidator(false)
            }
        }
        return null
    }
    private static CubeValidator getEmailValidator(Object arg){
        if (arg instanceof Boolean){
            if (arg == true){
                return emailValidator
            }else{
                return new EmailValidator(false)
            }
        }
        return null
    }

    private static CubeValidator getInListValidator(Object arg){
        if (arg instanceof List)
            return new InListValidator(arg)
        return null
    }
    private static CubeValidator getInListLabelValidator(Object arg){
        if (arg instanceof List)
            return new InListLabelValidator(arg)
        return null
    }
    private static CubeValidator getMatchesValidator(Object arg){
        if (arg instanceof String)
            return new MatchesValidator(arg)
        return null
    }
    private static CubeValidator getMaxValidator(Object arg){
        if (arg instanceof Comparable)
            return new MaxValidator(arg)
        return null
    }
    private static CubeValidator getMaxSizeValidator(Object arg){
        if (arg instanceof Integer)
            return new MaxSizeValidator(arg)
        return null
    }
    private static CubeValidator getMinValidator(Object arg){
        if (arg instanceof Comparable)
            return new MinValidator(arg)
        return null
    }
    private static CubeValidator getMinSizeValidator(Object arg){
        if (arg instanceof Integer)
            return new MinSizeValidator(arg)
        return null
    }
    private static CubeValidator getNotEqualValidator(Object arg){
        if (arg != null)
            return new NotEqualValidator(arg)
        return null
    }
    private static CubeValidator getRangeValidator(Object arg){
        if (arg instanceof Range)
            return new RangeValidator(arg)
        return null
    }
    private static CubeValidator getSizeValidator(Object arg){
        if (arg instanceof Range)
            return new SizeValidator(arg)
        return null
    }
    private static CubeValidator getUrlValidator(Object arg){
        if (arg instanceof Boolean){
            if (arg == true){
                return urlValidator
            }else{
                return new UrlValidator(false)
            }
        }
        return null
    }
    private static CubeValidator getNullableValidator(Object arg){
        if (arg instanceof Boolean){
            if (arg == true){
                return nullableValidator
            }else{
                return new NullableValidator(false)
            }
        }
        return null
    }
}
