package com.nd.cube.common.validator

import java.lang.reflect.Array

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class MinSizeValidator extends AbstractValidator{
    int minSize
    String defaultMessage = "\\u7684\\u503C\\u7684\\u5927\\u5C0F\\u6BD4\\u6700\\u5C0F\\u503C\\u8FD8\\u5C0F"
    MinSizeValidator(int minSize){
        this.minSize = minSize
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

        if (length >= minSize)
            return true
        return false
    }
}
