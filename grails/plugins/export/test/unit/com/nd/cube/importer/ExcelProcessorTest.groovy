package com.nd.cube.importer

/**
 * Created by Administrator on 2015/2/5.
 */
class ExcelProcessorTest extends GroovyTestCase {
    ExcelProcessor excelProcessor
    void setUp() {
        super.setUp()
        FileInputStream fileInputStream = new FileInputStream("D:/area.xls")
        Excel excel = new Excel(fileInputStream,Excel.ExcelType.xls.ordinal(),"area")
        int strategy = 0
        excelProcessor = new ExcelProcessor(excel,strategy)
        excelProcessor.setFields(["areaCode","areaName","parent","level"])
        //excelProcessor.setLabels(["区域编号":"areaCode","区域名称":"areaName","上级编号":"parent","层级":"level"])

    }

    void testRead() {
        List list = excelProcessor.read()
        println list
    }
}
