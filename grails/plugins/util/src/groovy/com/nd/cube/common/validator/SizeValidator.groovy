package com.nd.cube.common.validator

import java.lang.reflect.Array

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class SizeValidator extends AbstractValidator{
    Range range
    String defaultMessage = "\\u7684\\u503C\\u7684\\u5927\\u5C0F\\u4E0D\\u5728\\u5408\\u6CD5\\u7684\\u8303\\u56F4\\u5185"
    SizeValidator(Range range) {
        this.range = range
    }

    @Override
    Boolean validate(Object target) {
        int length = getSize(target)
        if (range && range.contains(length))
            return true
        return false
    }

    int getSize(Object target){
        int length;

        if (target.getClass().isArray())
            length = Array.getLength(target)
        else if (target instanceof Collection)
            length = ((Collection)target).size()
        else if (target instanceof String)
            length = ((String)target).length()
        return length

    }
}
