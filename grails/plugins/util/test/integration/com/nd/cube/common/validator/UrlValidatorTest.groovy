package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class UrlValidatorTest extends GroovyTestCase{
	void testValidate(){
		UrlValidator validator = new UrlValidator(true)
		assertTrue(validator.validate("www.baidu.com")==false)
		assertTrue(validator.validate("http://www.baidu.com")==true)
	}}