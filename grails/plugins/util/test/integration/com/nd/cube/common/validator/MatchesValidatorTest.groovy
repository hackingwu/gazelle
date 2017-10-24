package com.nd.cube.common.validator
/**
 * 校验器测试
 * @author wuzj
 * @since  2015/1/8
 */ 
class MatchesValidatorTest extends GroovyTestCase{
	void testValidate(){
		MatchesValidator validator = new MatchesValidator("\\d")
		assertTrue(validator.validate("f")==false)
	}}