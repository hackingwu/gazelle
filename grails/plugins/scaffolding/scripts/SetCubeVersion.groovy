includeTargets << grailsScript("_GrailsInit")

target(setCubeVersion: "The description of the script goes here!") {
    String appVersion = grails.util.Metadata.getCurrent().getProperty("app.version")
    if(appVersion.indexOf("-")!=-1){
        appVersion = appVersion.substring(0,appVersion.indexOf("-"))
    }
    String newAppVersion = "${appVersion}-${new Date().format("yyyyMMddHHmm")}"
    grails.util.Metadata.getCurrent().setProperty("app.version",newAppVersion)
    grails.util.Metadata.getCurrent().persist()
    println "app.version = ${grails.util.Metadata.getCurrent().getProperty("app.version")}"
}

setDefaultTarget(setCubeVersion)
