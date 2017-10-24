package com.nd.cube.common.validator

import java.lang.reflect.Array

/**
 * 支持数组，集合和字符串
 * @author wuzj
 * @since 2015/1/6.
 */
class MaxSizeValidator extends AbstractValidator{
    int maxSize
    String defaultMessage = "\\u7684\\u503C\\u7684\\u5927\\u5C0F\\u6BD4\\u6700\\u5927\\u503C\\u8FD8\\u5927"
    MaxSizeValidator(int maxSize) {
        this.maxSize = maxSize
    }

    @Override
    Boolean validate(Object target) {
        int length;

        if (target.getClass().isArray())
            length = Array.getLength(target)
        else if (target instanceof Collection)
            length = ((Collection)target).size()
        else if (target instanceof String)
            length = ((String)target).length()

        if (length < maxSize)
            return true
        return false

    }
}
