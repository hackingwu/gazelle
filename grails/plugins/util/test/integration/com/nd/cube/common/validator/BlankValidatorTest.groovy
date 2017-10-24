package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class BlankValidatorTest extends GroovyTestCase{
	void testValidate(){
		BlankValidator validator = new BlankValidator(false)
		assertTrue(validator.validate(" ")==false)
	}}