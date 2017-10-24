package com.nd.cube.common.validator

import framework.util.StringUtil
import org.apache.commons.lang.StringEscapeUtils

import java.text.MessageFormat

/**
 * Created by Administrator on 2015/2/9.
 */
abstract class AbstractValidator implements CubeValidator{
    String defaultMessage = ""
    @Override
    String validate(Object target, String message) {
        if (StringUtil.isEmpty(message)){
            message = StringEscapeUtils.unescapeJava(defaultMessage)
        }
        MessageFormat.format(message)
        return null
    }
}
