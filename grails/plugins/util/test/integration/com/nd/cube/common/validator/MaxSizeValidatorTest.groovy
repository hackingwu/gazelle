package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class MaxSizeValidatorTest extends GroovyTestCase{
	void testValidate(){
		MaxSizeValidator validator = new MaxSizeValidator(5)
		assertTrue(validator.validate("abc")==true)
	}}