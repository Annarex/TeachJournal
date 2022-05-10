package com.donntu.teachjournal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.donntu.teachjournal.utils.ParseXML


class MainActivity : AppCompatActivity() {
    //удалил комментарий
    var path: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun openFileDialog(view: View) {
        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        } else {
            val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 777 && data != null) {
            val filePath = data?.data?.path
            path = filePath!!
        }
    }
    fun ReadXml(view: View){
        if(!path.endsWith("xls")){
            val a = ParseXML()
            a.readFromExcelFile(this.applicationContext,"/storage/emulated/0/2.xls");
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 777) {
            val filePath = data?.data?.path
        }
    }
}