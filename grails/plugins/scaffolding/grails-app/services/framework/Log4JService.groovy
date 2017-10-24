package framework

import grails.converters.JSON
import grails.transaction.Transactional

import framework.util.PropertyConfig
import org.apache.commons.io.input.BoundedInputStream

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Log4J的Service
 * @author wuzj
 * @version 2014/10/10
 */
class Log4JService{

    void clean(){
        int logCleanInterval = PropertyConfig.getInt("com.nd.volunteers_mgr.logCleanInterval",7)
        String logPath = PropertyConfig.getProperty("log4j.logPath","/data/weblogs/dailyRollin.log")
        logPath = logPath.substring(0,logPath.lastIndexOf(File.separator)+1)
        clean(logCleanInterval,logPath)
    }
    void clean(int logCleanInterval,String logPath) {
        File file = new File(logPath)
        if(file.exists()&&file.isDirectory()){
            File[] logs = file.listFiles()
            List<File> expiredLog = new ArrayList<File>()
            for(int i= 0 ; i < logs.length ; i++){
                String fileName = logs[i].getName()
                if(fileName.indexOf("dailyRollin.log")>-1||fileName.indexOf("dailyRollin.log_stacktrace.log")>-1){
                    String lastStr = fileName.substring(fileName.lastIndexOf(".")+1)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
                    Date date = null
                    String regex = "[1-9][0-9]{3}-[0-9]{2}-[0-9]{2}"
                    Pattern pattern = Pattern.compile(regex)
                    Matcher matcher = pattern.matcher(lastStr)
                    if(matcher.matches()){
                        try {
                            date = sdf.parse(lastStr)
                        }catch (ParseException e){
                            e.printStackTrace()
                            log.error(e.getMessage())
                        }
                        if(date!=null && Math.abs((date.getTime()-new Date().getTime()))/(1000*60*60*24)>=logCleanInterval){
                            expiredLog.add(logs[i])
                        }
                    }
                }

            }
            Iterator it = expiredLog.iterator()
            while(it.hasNext()){
                File tempFile = it.next()
                tempFile.delete()
            }
        }
    }

    String readLog(String logName){


        File file = new File(logName)
        if(!file.exists()){
            return "文件不存在"
        }
        long length = file.length()
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
        if(length > 2*1024*1024){
            bufferedReader.skip(length/2)
        }
        StringBuilder sb = new StringBuilder()
        String data = null
        while((data = bufferedReader.readLine())!=null){
            sb.append(data+"<br>")
        }
        bufferedReader.close()
        return sb.toString()

    }

    String readLog(String logName,int start,int size){
        File file = new File(logName)
        if(!file.exists()){
            return ['totalPage':0,'log':'文件不存在'] as JSON
        }
        int totalPage = Math.ceil(file.length()/(size*1024))
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))
        int bufferSize = 1024
        char[] logContent = new char[bufferSize*size]
        bufferedReader.skip(start*bufferSize*size)
        bufferedReader.read(logContent,0,bufferSize*size)
        String log = String.valueOf(logContent)
        bufferedReader.close()
        return ['totalPage':totalPage,'log':log] as JSON
    }

    String readLogByChannel(String logName,int start,int size){
        File file = new File(logName)
        if(!file.exists()){
            return ['totalPage':0,'log':'文件不存在'] as JSON
        }

        FileInputStream f = new FileInputStream(file)
        FileChannel channel = f.getChannel()
        int totalPage = Math.ceil(channel.size()/(size*1024))
        int bufferSize = 1024
//        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize)
        channel.position(start*bufferSize*size)
//        channel.truncate(bufferSize*size)
        String log = ""
        channel.position(size*bufferSize*start)
        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize)
        StringBuilder stringBuilder = new StringBuilder()
        while (size--){
            if(channel.read(byteBuffer)!=-1){
                byteBuffer.flip()
                byte[] array = new byte[bufferSize]
                byteBuffer.get(array,0,byteBuffer.remaining())
                stringBuilder.append(new String(array))
                byteBuffer.clear()
            }
        }
        log = stringBuilder.toString()
        channel.close()
        f.close()
        return ['totalPage':totalPage,'log':log] as JSON
    }
}
