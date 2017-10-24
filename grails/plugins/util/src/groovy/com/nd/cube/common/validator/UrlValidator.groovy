package com.nd.cube.common.validator

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class UrlValidator extends AbstractValidator{
    Boolean url
    String defaultMessage = "\\u7684\\u503C\\u4E0D\\u662F\\u4E00\\u4E2A\\u5408\\u6CD5\\u7684URL"
    UrlValidator(Boolean url) {
        this.url = url
    }

    @Override
    Boolean validate(Object target) {
        org.apache.commons.validator.UrlValidator commonUrlValidator = new org.apache.commons.validator.UrlValidator()
        if (target instanceof String && !url || commonUrlValidator.isValid((String)target))
            return true
        return false
    }
}
