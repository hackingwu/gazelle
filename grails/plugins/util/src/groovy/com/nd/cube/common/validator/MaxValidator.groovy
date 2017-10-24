package com.nd.cube.common.validator

/**
 * 支持日期和数字的比较
 * @author wuzj
 * @since 2015/1/6.
 */
class MaxValidator extends AbstractValidator{
    Comparable max
    String defaultMessage = "\\u7684\\u503C\\u6BD4\\u6700\\u5927\\u503C \\u8FD8\\u5927"
    MaxValidator(Comparable max){
        this.max = max
    }
    @Override
    Boolean validate(Object target) {
        if (max != null && max.compareTo(target)>0)
            return true
        return false
    }
}
