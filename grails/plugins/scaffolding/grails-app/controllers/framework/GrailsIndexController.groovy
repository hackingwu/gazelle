package framework

import framework.util.MD5Util
import framework.util.PropertyConfig
import java.lang.management.ManagementFactory
import java.lang.management.MemoryPoolMXBean
class GrailsIndexController {


    static filterConfig = [

        index : 'fd4ce0594e10892b4e29546d1ae64f4e'//只有url带上正确的cube值，才能访问，URL如grailsIndex/index?cube=fd4ce0594e10892b4e29546d1ae64f4e
//        index : -1 //当index对应的值为-1时，则始终不能被访问。
    ]
    def index() {
        Properties properties = System.getProperties()
        Set keySet = properties.keySet()
        Iterator iterator = keySet.iterator()
        List propertyList = []
        List keys = ["user.dir","user.home","user.language","user.timezone"]
        keys.each {
            propertyList << "${it} = ${properties.getProperty(it)}"
        }
        keySet.each {
            if (!keys.contains(it)){
                propertyList << "${it} = ${properties.getProperty(it)}"
            }
        }
        String name = ManagementFactory.getRuntimeMXBean().getName()
        String pid = name.substring(0,name.indexOf("@"))
        Process process = null
        List javaProperties = []
        try{
            process = Runtime.getRuntime().exec("jinfo -flags ${pid}")
        }catch (Exception e){
            log.error(e.getMessage())
        }
        String s = null
        if(process?.inputStream!=null){
            InputStream inputStream = process.inputStream
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
            StringBuilder stringBuilder = new StringBuilder()
            while((s=reader.readLine())!=null){
                stringBuilder.append(s)
            }
            s = stringBuilder.toString()
            String[] temp1 = s.split(',')
            for(int i = 0 ; i < temp1.length ; i++){
                String[] temp2 = temp1[i].split("-D")
                if(temp2.length>0){
                    for(int j = 0 ; j < temp2.length;j++){
                        javaProperties << temp2[j]
                    }
                }else {
                    javaProperties << temp1[i]
                }
            }
        }
        if(javaProperties.size()<=1) {
            for(MemoryPoolMXBean mx:ManagementFactory.getMemoryPoolMXBeans()){
                javaProperties <<"${mx.getName()}=${mx.getUsage().getMax()/(1024*1024)}M"
            }
            javaProperties << "javaTotalMemory=${Runtime.getRuntime().totalMemory()/(1024*1024)}M"
            javaProperties << "javaMaxMemory=${Runtime.getRuntime().maxMemory()/(1024*1024)}M"
        }

        [propertyList:propertyList,javaProperties:javaProperties]
    }
}
