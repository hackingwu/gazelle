package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class NotEqualValidatorTest extends GroovyTestCase{
	void testValidate(){
		NotEqualValidator validator = new NotEqualValidator("abc")
		assertTrue(validator.validate("abcd")==true)
	}}