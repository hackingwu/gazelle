package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class CreditCardValidatorTest extends GroovyTestCase{
	void testValidate(){
		CreditCardValidator validator = new CreditCardValidator(true)
		assertTrue(validator.validate("3501281943562")==false)
	}}