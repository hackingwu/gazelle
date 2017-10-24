import net.sourceforge.plantuml.SourceStringReader

includeTargets << grailsScript("_GrailsInit")

target(generateUml: "Generates UML images for documentation") {

    new File("${basedir}/src/docs/uml").listFiles().each{ File srcUmlFile->

        if(srcUmlFile.name.contains("guml")) {
            try {
                String name = srcUmlFile.name - ".guml" + ".png"
                SourceStringReader s = new SourceStringReader(srcUmlFile.text)
                FileOutputStream file = new FileOutputStream("${basedir}/src/docs/images/${name}")
                s.generateImage(file);
                file.close()
            } catch (Exception e) {
                println "${srcUmlFile.name} generate UML error: ${e?.message}"
            }
        }

    }

    grailsConsole.info "Generation of UML images completed!"
}

setDefaultTarget(generateUml)
