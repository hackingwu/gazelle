package com.nd.cube.common.validator

import framework.util.StringUtil

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class MatchesValidator extends AbstractValidator{
    String matches
    String defaultMessage = "{0}\\u7684\\u503C{2}\\u4E0E\\u5B9A\\u4E49\\u7684\\u6A21\\u5F0F\\u4E0D\\u5339\\u914D"
    MatchesValidator(String matches){
        this.matches = matches
    }
    @Override
    Boolean validate(Object target) {
        if( (StringUtil.isEmpty((String)target)) || ( target instanceof String && null == matches) || (((String)target).matches(matches)))
            return true

        return false
    }

}
