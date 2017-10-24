package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class EmailValidatorTest extends GroovyTestCase{
	void testValidate(){
		EmailValidator validator = new EmailValidator(true)
		assertTrue(validator.validate("215867983@qq.com")==true)
	}}