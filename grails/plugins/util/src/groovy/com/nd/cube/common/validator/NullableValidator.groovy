package com.nd.cube.common.validator

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class NullableValidator extends AbstractValidator {
    Boolean nullable
    String defaultMessage = "\\u4E0D\\u80FD\\u4E3A\\u7a7a"
    NullableValidator(Boolean nullable) {
        this.nullable = nullable
    }

    @Override
    Boolean validate(Object target) {
        if (nullable || null != target)
            return true
        return false
    }
}
