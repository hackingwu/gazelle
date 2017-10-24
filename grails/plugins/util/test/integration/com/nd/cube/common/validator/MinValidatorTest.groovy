package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class MinValidatorTest extends GroovyTestCase{
	void testValidate(){
		MinValidator validator = new MinValidator(3)
		assertTrue(validator.validate(5)==true)
	}}