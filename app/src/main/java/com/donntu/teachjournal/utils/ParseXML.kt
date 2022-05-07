package com.donntu.teachjournal.utils
import java.io.File
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileInputStream


class ParseXML {
    fun readFromExcelFile(path: String) {
        val inputStream = FileInputStream(path)
        //Instantiate Excel workbook using existing file:
        val xlWb = WorkbookFactory.create(inputStream)
        //val rowNumber = 0
        //val columnNumber = 0
        var index = 10
        //Get reference to first sheet:
        val xlWs = xlWb.getSheetAt(0)
        //условное добавление названия факультета
        println(xlWs.getRow(2).getCell(3))
        while(xlWs.getRow(index).getCell(2)!=null && xlWs.getRow(index+1).getCell(2)!=null) {
            //условное добавление названия группы
            println(xlWs.getRow(index).getCell(2))
            index + 2
            if (xlWs.getRow(index).getCell(2) != null) {
                while (xlWs.getRow(index).getCell(2) != null) {
                    //условное добавление cтудента
                    println(xlWs.getRow(index).getCell(2))
                    //
                    index++
                }
            } else{
                index+1
            }
        }
        }
    }