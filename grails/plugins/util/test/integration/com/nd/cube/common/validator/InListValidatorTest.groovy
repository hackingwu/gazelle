package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class InListValidatorTest extends GroovyTestCase{
	void testValidate(){
		InListValidator validator = new InListValidator(["a","b","c"])
		assertTrue(validator.validate("a")==true)
	}}