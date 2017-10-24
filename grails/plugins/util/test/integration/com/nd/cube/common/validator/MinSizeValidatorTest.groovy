package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class MinSizeValidatorTest extends GroovyTestCase{
	void testValidate(){
		MinSizeValidator validator = new MinSizeValidator(2)
		assertTrue(validator.validate("abc")==true)
	}}