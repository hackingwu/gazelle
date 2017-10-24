package com.nd.cube.common.validator

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class RangeValidator extends AbstractValidator{

    Range range
    String defaultMessage = "\\u7684\\u503C\\u4E0D\\u5728\\u5408\\u6CD5\\u7684\\u8303\\u56F4\\u5185"
    RangeValidator(Range range) {
        this.range = range
    }

    @Override
    Boolean validate(Object target) {
        return new InListValidator(range).validate(target)
    }
}
