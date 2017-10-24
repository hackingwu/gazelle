package com.nd.cube.common.util

import framework.util.StringUtil

/**
 * Created by wuzj on 2015/1/14.
 */
class StringUtilTest extends GroovyTestCase{
    void testIsNumeric(){
        assertTrue(StringUtil.isNumeric("-12") == true)
        assertTrue(StringUtil.isNumeric("-12.123") == true)
        assertTrue(StringUtil.isNumeric("+12") == true)
        assertTrue(StringUtil.isNumeric("-12.312") == true)
        assertTrue(StringUtil.isNumeric("") == false)
        assertTrue(StringUtil.isNumeric("fdf") == false)
        assertTrue(StringUtil.isNumeric("++123") == false)
        assertTrue(StringUtil.isNumeric("+-321.313") == false)
    }
}
