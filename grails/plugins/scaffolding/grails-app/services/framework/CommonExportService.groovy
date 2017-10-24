package framework

import framework.util.MD5Util
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.DataFormat
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook

import java.text.SimpleDateFormat

/**
 * 公共导出服务类
 * @author wuzj
 * @version 2014-09-11
 */
class CommonExportService {

    //行首
    static SXSSFWorkbook getWorkBookWithFirstRow(List fields,String sheetName){
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000)
        Sheet sheet = workbook.createSheet(sheetName)
        Row firstRow = sheet.createRow(0)
        CellStyle cellStyle = workbook.createCellStyle()
        Font firstRowFont = workbook.createFont()
        firstRowFont.setFontName("宋体")
        firstRowFont.setFontHeightInPoints((short)10)
        firstRowFont.setBoldweight(Font.BOLDWEIGHT_BOLD)
        cellStyle.setFont(firstRowFont)
        for(int i = 0 ; i < fields.size();i++){
            Cell cell =firstRow.createCell(i)
            cell.setCellValue(fields.get(i))
            cell.setCellStyle(cellStyle)
        }
        return workbook
    }

    static void writeToExcel(Sheet sheet,int startRow,List<Map<String,Object>> result,Map<String,Map<String,Object>> formatters){
        SXSSFWorkbook workbook = sheet.getWorkbook()
        CellStyle contentCellStyle = workbook.createCellStyle()
        DataFormat format = workbook.createDataFormat()
        Font contentFont = workbook.createFont()
        contentFont.setFontName("宋体")
        contentFont.setFontHeightInPoints((short)10)
        contentFont.setBoldweight(Font.BOLDWEIGHT_NORMAL)
        contentCellStyle.setFont(contentFont)
        contentCellStyle.setDataFormat(format.getFormat("@"))//将列属性设置为文本类型

        for(int i = 0 ; i < result.size();i++) {
            Row row = sheet.createRow(startRow + i)
            int j = 0
            Set keySet = result[i].keySet()
            Iterator keyIt = keySet.iterator()
            while (keyIt.hasNext()) {
                String key = keyIt.next()
                Cell cell = row.createCell(j++)
                Object object = result[i][key]
                String value = object?object.toString():""
                Set formatSet = formatters.keySet()
                Iterator formatIt = formatSet.iterator()
                while (formatIt.hasNext()) {
                    String formatKey = formatIt.next()
                    if (key == formatKey) {
                        Map formatMap = formatters[formatKey]
                        String methodStr = formatMap.keySet()[0]
                        Object valueList = formatMap[methodStr]
                        if (valueList != null) {
                            value = "${methodStr}"(object,valueList)
                        } else {
                            value = "${methodStr}"(object)
                        }
                        break;
                    }
                }
                cell.setCellValue(value)
                cell.setCellStyle(contentCellStyle)
            }
        }
    }
    static String convertDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        String dateStr = ""
        if(date!=null){
            dateStr = sdf.format(date)
        }
        return dateStr
    }
    static String convertBool(Boolean flag){
        return flag?"是":"否"
    }

    static String convertBool(Boolean flag,Map boolMap){
        return boolMap[flag.toString()]
    }
    static String convertString(String s){
        return "\t" + s + "\t"
    }
    /**
     *
     * @param s
     * @param map 例如 Map map = ['identity':'身份证','passport':'护照','officer':'军官证']
     * @return
     */
    static String convertInList(String s,Map map){
        if(s!=null&&map.containsKey(s)){
            s = (String)map[s]
        }
        return s?:""
    }
    /**
     * md5_date
     * @return 通过当前时间得到一个文件名
     */
    static String getFileName(){
        Date now = new Date()
        String nowStr = new SimpleDateFormat("yyyyMMddHHmmss").format(now)
        String timeMd5 = MD5Util.getMD5String(nowStr)
        return timeMd5 + "-" + nowStr
    }

}
