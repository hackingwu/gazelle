package com.nd.cube.importer

/**
 * Created by Administrator on 2015/2/5.
 */
class ExcelTest extends GroovyTestCase {
    Excel excel
    void setUp() {
        super.setUp()
        FileInputStream fileInputStream = new FileInputStream("D:/area.xls")
         excel = new Excel(fileInputStream,Excel.ExcelType.xls.ordinal(),"area")
    }

    void testGetWorkbook() {
        assertNotNull(excel.getWorkbook())
    }

    void testGetSheet() {
        assertNotNull(excel.getSheet())
    }

    void testGetContent() {
        println excel.getContent()
    }
}
