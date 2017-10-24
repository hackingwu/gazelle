package com.nd.cube.common.validator

/**
 * @author wuzj
 * @since 2015/1/8.
 */
class NullableValidatorTest extends GroovyTestCase{
    void testValidate(){
        NullableValidator validator = new NullableValidator(false)
        assertTrue(validator.validate(null) == false)
    }
}
