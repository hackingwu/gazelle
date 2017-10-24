package framework

import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook


class CommonImportService {

    static List readExcelContentH(File file,String sheetName = "Sheet1",List properties){
        FileInputStream fileInputStream = new FileInputStream(file)
        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream)
        HSSFSheet sheet = workbook.getSheet(sheetName)
        int rows = sheet.getPhysicalNumberOfRows()
        List<Map> content = new ArrayList<>()
        for(int i = 1 ; i < rows;i++){
            HSSFRow r = sheet.getRow(i)
            Map map = new HashMap()
            for(int j = 0 ; j < properties.size();j++){
                HSSFCell cell = r.getCell(j)
                cell.setCellType(Cell.CELL_TYPE_STRING)
                map.put(properties.get(j),cell.getStringCellValue())
            }
            content.add(map)
        }
        return content
    }
    static List readExcelContentX(File file,String sheetName = "Sheet1",List properties){
        FileInputStream fileInputStream = new FileInputStream(file)
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)
        XSSFSheet sheet = workbook.getSheet(sheetName)
        int rows = sheet.getPhysicalNumberOfRows()
        List<Map> content = new ArrayList<>()
        for(int i = 1 ; i < rows;i++){
            XSSFRow r = sheet.getRow(i)
            Map map = new HashMap()
            for(int j = 0 ; j < properties.size();j++){
                XSSFCell cell = r.getCell(j)
                cell.setCellType(Cell.CELL_TYPE_STRING)
                map.put(properties.get(j),cell.getStringCellValue())
            }
            content.add(map)
        }
        return content
    }

    static List readExcelContent(File file,String sheetName = "Sheet1",List properties){
        if(file.name.endsWith(".xlsx")){
            return readExcelContentX(file,sheetName,properties)
        }else if(file.name.endsWith(".xls")){
            return readExcelContent(file,sheetName,properties)
        }
    }

    static List readTextContent(File file ,String split,List properties){
        FileInputStream fileInputStream = new FileInputStream(file)
        fileInputStream.readLines().collect{
            Map map = [:]
            it.split(split).eachWithIndex { String entry, int i ->
                map.put(properties.get(i),entry.trim())
            }
            map
        }
    }

}
