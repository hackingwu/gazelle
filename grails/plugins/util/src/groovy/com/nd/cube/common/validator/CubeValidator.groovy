package com.nd.cube.common.validator

/**
 * Created by Administrator on 2015/1/6.
 */
interface CubeValidator {

    Boolean validate(Object target);

    String validate(Object target,String message);
}