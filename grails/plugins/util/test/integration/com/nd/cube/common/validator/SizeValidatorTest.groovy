package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class SizeValidatorTest extends GroovyTestCase{
	void testValidate(){
		SizeValidator validator = new SizeValidator(3..6)
		assertTrue(validator.validate("fdasf")==true)
	}}