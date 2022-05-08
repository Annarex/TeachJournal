package com.donntu.teachjournal.utils
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


class ParseXML {
    fun readFromExcelFile(context: Context, path: String) {
        val ParcelFileDescriptor = context.contentResolver.openFileDescriptor(Uri.fromFile(File(path)), "r", null)
        val inputStream: FileInputStream = FileInputStream(path)
        //Instantiate Excel workbook using existing file:
        val xlWb = WorkbookFactory.create(inputStream)
        //val rowNumber = 0
        //val columnNumber = 0
        var index = 9
        //Get reference to first sheet:
        val xlWs = xlWb.getSheetAt(0)
        while(xlWs.getRow(index).getCell(1)!=xlWs.getRow(index+1).getCell(1)) {
            //условное добавление названия группы
            val group: String = xlWs.getRow(index).getCell(0).toString().removePrefix("Группа: ")
            index += 2
            if (xlWs.getRow(index).getCell(1) != null) {
                while (xlWs.getRow(index).getCell(1) != null) {
                    //условное добавление cтудента
                    val fio = xlWs.getRow(index).getCell(1)
                    //
                    index++
                }
            } else{
                index+1
            }
        }
        }
    }