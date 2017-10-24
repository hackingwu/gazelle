package com.nd.cube.importer

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.text.SimpleDateFormat

/**
 * Created by wuzj on 2015/2/4.
 */
class Excel {
    InputStream inputStream
    int type
    String sheetName
    Boolean header
    Workbook workbook
    Sheet sheet
    List content

    enum ExcelType{
        xls,xlsx
    }
    Excel(InputStream inputStream, int type,String sheetName="Sheet1", Boolean header=true) {
        this.inputStream = inputStream
        this.type = type
        this.sheetName = sheetName
        this.header = header

    }

    Workbook getWorkbook() {
        if (workbook == null){
            if (type == ExcelType.xls.ordinal()){
                workbook = new HSSFWorkbook(inputStream)
            }else if(type == ExcelType.xlsx.ordinal()){
                workbook = new XSSFWorkbook(inputStream)
            }
        }
        return workbook
    }

    void setWorkbook(Workbook workbook) {
        this.workbook = workbook
    }

    Sheet getSheet() {
        if (sheet == null){
            sheet = getWorkbook().getSheet(sheetName)
        }
        return sheet
    }

    void setSheet(Sheet sheet) {
        this.sheet = sheet
    }

    List getContent() {
        if (content==null){
            content = new ArrayList()
            int rows = getSheet().getPhysicalNumberOfRows()
            for(int i = 0 ; i < rows ; i++){
                List rowList = new ArrayList()
                Row row = sheet.getRow(i)
                int cells = row.getLastCellNum()
                for (int j = 0 ; j < cells ; j++){
                    Cell cell = row.getCell(j)
                    if(cell==null){
                        rowList.add("")
                    }else{
                        rowList.add(getCellStringValue(cell))
                    }
                }
                content.add(rowList)
            }
        }
        return content
    }

    List getContentHeader(){
        List rowList = new ArrayList()

        Row row = getSheet().getRow(0)
        int cells = row.getLastCellNum()
        for (int j = 0 ; j < cells ; j++){
            Cell cell = row.getCell(j)
            rowList.add(cell.getStringCellValue().trim())
        }

        return rowList
    }

    void setContent(List content) {
        this.content = content
    }

    InputStream getInputStream() {
        return inputStream
    }

    void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream
    }

    int getType() {
        return type
    }

    void setType(int type) {
        this.type = type
    }

    Boolean getHeader() {
        return header
    }

    void setHeader(Boolean header) {
        this.header = header
    }

    public String getCellStringValue(Cell cell) {
        String cellValue = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING://字符串类型
                cellValue = cell.getStringCellValue();
                if(cellValue.trim().equals("")||cellValue.trim().length()<=0)
                    cellValue="";
                break;
            case Cell.CELL_TYPE_NUMERIC: //数值类型
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                cellValue=" ";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                break;
            case Cell.CELL_TYPE_ERROR:
                break;
            default:
                break;
        }
        return cellValue;
    }

    /**
     * 根据Cell类型设置数据
     * @param cell
     * @return
     */
    private String getCellFormatValue(Cell cell) {
        String cellvalue = ""
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
            // 如果当前Cell的Type为NUMERIC
                case Cell.CELL_TYPE_NUMERIC:
//                case Cell.CELL_TYPE_FORMULA:
                    //判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为Data格式

                        //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                        //cellvalue = cell.getDateCellValue().toLocaleString();

                        //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                        Date date = cell.getDateCellValue()
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
                        cellvalue = sdf.format(date)
                    }
                    // 如果是纯数字
                    else {
                        // 取得当前Cell的数值
                        cellvalue = String.format("%.0f", cell.getNumericCellValue()) //取消科学计数法
                    }
                    break
                case Cell.CELL_TYPE_FORMULA: //公式型也按string取
                case Cell.CELL_TYPE_STRING:
                    // 取得当前的Cell字符串
                    try{
                        cellvalue = cell.getRichStringCellValue().getString()
                    }catch (e){
                        cellvalue = ""
                    }
                    break
            // 默认的Cell值
                default:
                    cellvalue = ""
            }
        } else {
            cellvalue = ""
        }
        return cellvalue.trim()
    }

}
