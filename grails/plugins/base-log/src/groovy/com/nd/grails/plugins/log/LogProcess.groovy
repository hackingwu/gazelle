package com.nd.grails.plugins.log

/**
 * 日志保存前处理，根据应用需求可以配置不同的处理方式，如:统一处理ip地址及用户信息
 * sina 2014/8/4.
 */
public interface LogProcess {

    void process(Log log)

}