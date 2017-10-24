package com.nd.cube.common.validator
/**
 * @author wuzj
 * @since 2015/1/6.
 */
class InListLabelValidator extends AbstractValidator {
    List list = null
    String defaultMessage = "\\u7684\\u503C\\u4E0D\\u5728\\u5217\\u8868\\u7684\\u53D6\\u503C\\u8303\\u56F4\\u5185"

    InListLabelValidator(List list) {
        this.list = list
    }

    @Override
    Boolean validate(Object target) {
        if (list == null || list.contains(target))
            return true

        return false
    }
}
