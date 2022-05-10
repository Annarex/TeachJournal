package com.donntu.teachjournal

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donntu.teachjournal.db.DBJournalHelper
import com.donntu.teachjournal.utils.ParseXML
import com.donntu.teachjournal.utils.StInGrAdapter


class MainActivity : AppCompatActivity() {
    //удалил комментарий
    var path: Uri? = null
    val db by lazy { DBJournalHelper.getDatabase(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //ExporterImporterDB().copyFile(db, this, "TeachJournal.db")
    }
    fun openFileDialog(view: View) {
        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ){
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        } else {
            val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
            //val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).putExtra(DocumentsContract.EXTRA_INITIAL_URI, DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Android"))

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 777 && data != null) {
            val filePath = data?.data
            path = filePath
        }
    }
    fun ReadXml(view: View){
        //val path = "/document/primary:Documents/2_5323700053171246751.xls"
        val all = db.studyGroupDAO().getAllGroupsWithStudents()
        val recView = findViewById<RecyclerView>(R.id.list)
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = StInGrAdapter(all)
        if(path.toString().endsWith("xls")){
            //pb.visibility = View.VISIBLE
            val a = ParseXML(context=this).readFromExcelFile(db, path!!);
            //pb.visibility = View.INVISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        db.close()
    }
    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}