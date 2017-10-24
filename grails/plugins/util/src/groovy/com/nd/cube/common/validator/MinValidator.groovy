package com.nd.cube.common.validator

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class MinValidator extends AbstractValidator {
    Comparable min
    String defaultMessage = "\\u7684\\u503C\\u6BD4\\u6700\\u5C0F\\u503C\\u8FD8\\u5C0F"
    MinValidator(Comparable min) {
        this.min = min
    }

    @Override
    Boolean validate(Object target) {
        if (min != null && min.compareTo(target) < 0)
            return true
        return false
    }
}
