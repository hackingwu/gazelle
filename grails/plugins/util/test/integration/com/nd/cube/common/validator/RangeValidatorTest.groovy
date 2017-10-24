package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class RangeValidatorTest extends GroovyTestCase{
	void testValidate(){
		RangeValidator validator = new RangeValidator(2..5)
		assertTrue(validator.validate(3)==true)
	}}