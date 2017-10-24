package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class MaxValidatorTest extends GroovyTestCase{
	void testValidate(){
		MaxValidator validator = new MaxValidator(7)
		assertTrue(validator.validate(6)==true)
	}}