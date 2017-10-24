package com.nd.cube.common.validator

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class NotEqualValidator extends AbstractValidator {

    Object notEqual
    String defaultMessage = "\\u7684\\u503C{2}\\u4E0E{3}\\u4E0D\\u76F8\\u7B49"
    NotEqualValidator(Object notEqual) {
        this.notEqual = notEqual
    }
    @Override
    Boolean validate(Object target) {
        if (notEqual != null && !notEqual.equals(target))
            return true
        return false
    }
}
