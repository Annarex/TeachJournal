package com.donntu.teachjournal.db.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import com.donntu.teachjournal.db.DBJournalHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URI
import java.nio.channels.FileChannel


class ExporterImporterDB{
    fun importDB(db: DBJournalHelper, context: Context, db_name: String) {
        if(db.isOpen)
            db.close()
        try {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (downloadDir.canWrite()) {
            val path = context.getDatabasePath(db_name).absolutePath
            val currentDB = File(path)
            val backupDB = File(downloadDir,path.split("/").last())
            if(currentDB.exists()){
                val src: FileChannel = FileInputStream(currentDB).channel
                val dst: FileChannel = FileOutputStream(backupDB).channel
                dst.transferFrom(src, 0, src.size())
                src.close()
                dst.close()
                Toast.makeText(context, "База данных сохранена по пути: $backupDB", Toast.LENGTH_SHORT).show()

            }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun exportDB(db: DBJournalHelper, context: Context, uri: Uri, db_name: String) {
        if(db.isOpen)
            db.close()
        try {
            val sd: File = File(URI(uri.toString()))
                val path = context.getDatabasePath(db_name).absolutePath
                val currentDB = File(path)
                val isExists = currentDB.exists()
                if (isExists) {
                    val src: FileChannel = FileInputStream(sd).channel
                    val dst: FileChannel = FileOutputStream(currentDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    Toast.makeText(context, "DB замещена на копию: $sd", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}