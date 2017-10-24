package export

import framework.CommonExportService
import framework.util.PropertyConfig
import framework.util.StringUtil
import grails.plugin.redis.RedisService
import groovy.json.JsonSlurper
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import redis.clients.jedis.Jedis
import com.nd.grails.plugins.log.LogService

/**
 * Created by Administrator on 2015/2/2.
 */
class LogFileExportJob {
    def concurrent = true

    RedisService redisService
    ImportInfoService importInfoService
    LogService logService
    def grailsApplication

    static triggers = {
        simple repeatInterval: 5000l // 5秒执行一次导入解析
    }

    def execute() {
        if(PropertyConfig.getBoolean("cpp.membrane.erp.exportJob", true)) {
            redisService.withRedis {Jedis redis->
                while(true){
                    String exportStr
                    if(StringUtil.isEmpty(exportStr = redis.lpop(grailsApplication.config.com.dd.log.redisKey.exportFile))){
                        break
                    }else{
                        int page = 0;
                        int max = 1000;
                        int total = 0;
                        Map export = new JsonSlurper().parseText(exportStr)
                        List fields = export["fields"]
                        String importInfoId = export["importInfoId"]
                        String sheetName = "Sheet1"
                        SXSSFWorkbook workbook = CommonExportService.getWorkBookWithFirstRow(fields,sheetName)
                        Sheet sheet = workbook.getSheet(sheetName)
                        ImportInfo importInfo = importInfoService.findById(Long.parseLong(importInfoId))
                        try{
                            for(;;page++){
                                List<Map<String,String>> resultList
                                resultList = logService.getExportLogList(page, max)

                                if (resultList == null || resultList.size() == 0) {
                                    break;
                                }
                                total += resultList.size()
                                Map formatters = [
                                        birthday:['convertDate':null]
                                ]
                                CommonExportService.writeToExcel(sheet, page*max+1, resultList, formatters)
                            }
                            String filePath = grailsApplication.config.cpp.membrane.erp.exportFilePath+"/"+importInfo.fileName
                            File file = new File(filePath)
                            File directory = file.getParentFile()
                            if(!directory.exists()){
                                directory.mkdirs()
                            }
                            OutputStream out = new FileOutputStream(filePath)
                            workbook.write(out)
                            workbook.dispose()
                            importInfo.fileStatus = 4
                            importInfo.total = total
                            importInfo.sucessConut  = total
                            importInfo.filePath = importInfo.fileName
                            importInfoService.update(importInfo)
                        }catch (Exception e){
                            log.error(e.getMessage())
                            importInfo.fileStatus = 5
                            importInfoService.update(importInfo)
                        }
                    }
                }
            }
        }
    }
}
