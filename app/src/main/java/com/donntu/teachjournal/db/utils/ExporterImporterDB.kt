package com.donntu.teachjournal.db.utils

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.donntu.teachjournal.db.DBJournalHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel

class ExporterImporterDB{
    fun copyFile(db: DBJournalHelper, context: Context, path: String) {
        if(db.isOpen)
            db.close()
        try {
            val sd: File = Environment.getExternalStorageDirectory()
            val data: File = Environment.getDataDirectory()
            if (sd.canWrite()) {
                val path = context.getDatabasePath(path).absolutePath
                val currentDB = File(path)
                val isExists = currentDB.exists()
                val backupDB = File(sd,path.split("/").last())
                if (isExists) {
                    val src: FileChannel = FileInputStream(currentDB).getChannel()
                    val dst: FileChannel = FileOutputStream(backupDB).getChannel()
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    Toast.makeText(context, "DB copy to path: "+backupDB, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}