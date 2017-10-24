grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "http://repo.grails.org/grails/core"

    }
    dependencies {
//        <dependency>
//        <groupId>xerces</groupId>
//      <artifactId>xercesImpl</artifactId>
//        <version>2.9.0</version>
//      <scope>runtime</scope>
//        </dependency>
//    <dependency>
//      <groupId>net.sf.opencsv</groupId>
//        <artifactId>opencsv</artifactId>
//      <version>2.3</version>
//        <scope>compile</scope>
//    </dependency>
//        <dependency>
//        <groupId>com.lowagie</groupId>
//      <artifactId>itext</artifactId>
//        <version>2.1.7</version>
//      <scope>compile</scope>
//        </dependency>
//    <dependency>
//      <groupId>com.lowagie</groupId>
//        <artifactId>itext-rtf</artifactId>
//      <version>2.1.7</version>
//        <scope>compile</scope>
//    </dependency>
//        <dependency>
//        <groupId>org.odftoolkit</groupId>
//      <artifactId>odfdom-java</artifactId>
//        <version>0.8.5</version>
//      <scope>compile</scope>
//        </dependency>
//    <dependency>
//      <groupId>net.sourceforge.jexcelapi</groupId>
//        <artifactId>jxl</artifactId>
//      <version>2.6.12</version>
//        <scope>compile</scope>
//    </dependency>
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        compile 'xerces:xercesImpl:2.9.0'
        compile 'net.sf.opencsv:opencsv:2.3'
        compile 'com.lowagie:itext:2.1.7'
        compile 'com.lowagie:itext-rtf:2.1.7'
        compile 'org.odftoolkit:odfdom-java:0.8.5'
        compile 'net.sourceforge.jexcelapi:jxl:2.6.12'

    }

    plugins {
        runtime ":hibernate:3.6.10.13" // or ":hibernate4:4.3.5.1"
        build(":release:3.0.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}

grails.plugin.location.scaffolding="../../plugins/scaffolding"
grails.plugin.location.util="../../plugins/util"

