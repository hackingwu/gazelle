package com.nd.cube.common.validator

import framework.util.StringUtil

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class EmailValidator extends AbstractValidator{
    Boolean email
    String defaultMessage = "\\u7684\\u503C{2}\\u4E0D\\u662F\\u4E00\\u4E2A\\u5408\\u6CD5\\u7684\\u7535\\u5B50\\u90AE\\u4EF6\\u5730\\u5740"
    EmailValidator(Boolean email){
        this.email = email
    }

    @Override
    Boolean validate(Object target) {
        org.apache.commons.validator.EmailValidator commonEmailValidator = org.apache.commons.validator.EmailValidator.getInstance()
        if ( (StringUtil.isEmpty((String)target)) || (target instanceof String && !email) || (commonEmailValidator.isValid((String)target)) )
            return true

        return false
    }
}
