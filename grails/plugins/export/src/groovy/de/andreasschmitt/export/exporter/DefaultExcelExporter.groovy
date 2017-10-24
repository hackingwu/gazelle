package de.andreasschmitt.export.exporter

import de.andreasschmitt.export.builder.ExcelBuilder
import jxl.format.Colour

/**
 * @author Andreas Schmitt,liuxy
 * @version 2014-08-28  by liuxy 扩展了一个红色单元格格式，及增加接受cellStyleMap参数 ，对单元格自定格式，后续待改进
 *
 *
 */
class DefaultExcelExporter extends AbstractExporter {

    protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException{
        try {
            def builder = new ExcelBuilder()

            // Enable/Disable header output
            boolean isHeaderEnabled = true
            if(getParameters().containsKey("header.enabled")){
                isHeaderEnabled = getParameters().get("header.enabled")
            }

            boolean useZebraStyle = false
            if(getParameters().containsKey("zebraStyle.enabled")){
                useZebraStyle = getParameters().get("zebraStyle.enabled")
            }
            Map cellStyleMap=[:]
            if(getParameters().containsKey("cellStyleMap")){
                cellStyleMap=getParameters().get("cellStyleMap")
            }

            builder {
                workbook(outputStream: outputStream){
                    sheet(name: getParameters().get("title") ?: "Export", widths: getParameters().get("column.widths"), numberOfFields: data.size(), widthAutoSize: getParameters().get("column.width.autoSize")){

                        format(name: "title"){
                            font(name: "arial", bold: true, size: 14)
                        }

                        format(name: "header"){
                            if (useZebraStyle){
                                font(name: "arial", bold: true, backColor: Colour.GRAY_80, foreColor: Colour.WHITE, useBorder: true)
                            } else{
                                // Use default header format
                                font(name: "arial", bold: true)
                            }
                        }
                        format(name: "odd"){
                            font(backColor: Colour.GRAY_25, useBorder: true)
                        }
                        format(name: "even"){
                            font(backColor: Colour.WHITE, useBorder: true)
                        }

                        format(name:"red"){
                            font(backColor:Colour.RED,useBorder: true,bold: true)
                        }

                        int rowIndex = 0

                        // Option for titles on top of data table
                        def titles = getParameters().get("titles")
                        titles.each {
                            cell(row: rowIndex, column: 0, value: it, format: "title")
                            rowIndex++
                        }

                        //Create header
                        if(isHeaderEnabled){
                            fields.eachWithIndex { field, index ->
                                String value = getLabel(field)
                                cell(row: rowIndex, column: index, value: value, format: "header")
                            }

                            rowIndex++
                        }

                        //Rows
                        data.eachWithIndex { object, k ->

                            fields.eachWithIndex { field, i ->
                                Object value = getValue(object, field)
                                String format = ""
                                if(cellStyleMap.get(k+"_"+i)!=null){
                                    format=cellStyleMap.get(k+"_"+i)
                                }else{
                                    format=useZebraStyle ? ( (k % 2) == 0 ? "even" : "odd" ) : ""
                                }
                                cell(row: k + rowIndex, column: i, value: value, format: format)
                            }
                        }
                    }
                }
            }

            builder.write()
        }
        catch(Exception e){
            throw new ExportingException("Error during export", e)
        }
    }

}