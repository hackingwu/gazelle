package com.nd.cube.importer

/**
 * Created by Administrator on 2015/2/6.
 */
class DomainExcelProcessorTest extends GroovyTestCase {
    void testGet() {
        FileInputStream fileInputStream = new FileInputStream("D:/area.xls")
        Excel excel = new Excel(fileInputStream,Excel.ExcelType.xls.ordinal(),"area")
        ExcelProcessor excelProcessor = new ExcelProcessor(excel,0)
        excelProcessor.setLabels(["区域编号":"areaCode","区域名称":"areaName","上级编号":"parent","层级":"level"])
        println new DomainExcelProcessor("area",excelProcessor).get()
    }
    void testGetWithValidation(){
        FileInputStream fileInputStream = new FileInputStream("D:/area.xls")
        Excel excel = new Excel(fileInputStream,Excel.ExcelType.xls.ordinal(),"area")
        ExcelProcessor excelProcessor = new ExcelProcessor(excel,0)
        excelProcessor.setLabels(["区域编号":"areaCode","区域名称":"areaName","上级编号":"parent","层级":"level"])
        println new DomainExcelProcessor("area",excelProcessor).getWithValidation()
    }
    void testGetWithoutValidation(){
        FileInputStream fileInputStream = new FileInputStream("D:/area.xls")
        Excel excel = new Excel(fileInputStream,Excel.ExcelType.xls.ordinal(),"area")
        ExcelProcessor excelProcessor = new ExcelProcessor(excel,0)
        excelProcessor.setLabels(["区域编号":"areaCode","区域名称":"areaName","上级编号":"parent","层级":"level"])
        println new DomainExcelProcessor("area",excelProcessor).getWithoutValidation()
    }
}
