// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
// redis 配置
grails {
    redis {
        poolConfig {
            // jedis pool specific tweaks here, see jedis docs & src
            // ex: testWhileIdle = true
        }
        timeout = 2000 //default in milliseconds
        // password = "somepassword" //defaults to no password

        // requires either host & port combo, or a sentinels and masterName combo

        // use a single redis server (use only if nore using sentinel cluster)
        port = 6379
        host = "localhost"

        // use redis-sentinel cluster as opposed to a single redis server (use only if not use host/port)
        // sentinels = [ "host1:6379", "host2:6379", "host3:6379" ] // list of sentinel instance host/ports
        // masterName = "mymaster" // the name of a master the sentinel cluster is configured to monitor
    }

}
com.dental_doctor.redisKey.log = "log" //日志服务队列