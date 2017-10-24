package com.nd.cube.common.validator

import framework.util.StringUtil
import org.apache.commons.lang.StringEscapeUtils

import java.text.MessageFormat

/**
 * @author wuzj
 * @since 2015/1/6.
 */
class BlankValidator extends AbstractValidator{
    Boolean blank
    String defaultMessage = "\\u4E0D\\u80FD\\u4E3A\\u7A7A"
    BlankValidator(Boolean blank){
        this.blank = blank
    }

    @Override
    Boolean validate(Object target) {
        if (target instanceof String && blank || !StringUtil.isEmpty((String)target)){
            return true
        }
        return false
    }

}
