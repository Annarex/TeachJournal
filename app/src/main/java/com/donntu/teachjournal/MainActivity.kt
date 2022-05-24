package com.donntu.teachjournal

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.donntu.teachjournal.db.DBJournalHelper
import com.donntu.teachjournal.db.entity.*
import com.donntu.teachjournal.utils.ParseXML
import java.util.*


var select: String? =null

class MainActivity : AppCompatActivity()//, AdapterView.OnItemSelectedListener
{
    //удалил комментарий
    var path: Uri? = null
    var gvMain: GridView? = null
    var adapterr: ArrayAdapter<String>? = null
    var languages = arrayOf("Группа","Добавить", "Показать все")
    var list_of_items = arrayOf("Дисциплина", "Добавить", "Показать все")
    var list_of_items2 = arrayOf("Вид занятия", "Добавить", "Показать все")
    //var select=null

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "Да", Toast.LENGTH_SHORT).show()
    }
    val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "Нет", Toast.LENGTH_SHORT).show()
    }
    val neutralButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "Может быть", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    //var sp: Spinner? = null
    //var textView_msg: TextView? = null
    var ssubject: Long = 0L
    val db by lazy { DBJournalHelper.getDatabase(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //ExporterImporterDB().copyFile(db, this, "TeachJournal.db")

        val spinner = findViewById<Spinner>(R.id.spinner)
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // TODO Auto-generated method stub
                when(position){
                    1 -> {
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.removeAllViews();
                        showdialog(R.layout.addsubject, 1)
                    }
                    2 -> {
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.removeAllViews();
                        //view?.let { basicAlert(it) }
                        fillGridView()
                        when(ssubject){
                            0L -> showToast(message = "Журнал не выбран!")
                            else -> {
                                var flow = db.flowStudentsDAO().getFlowStudents()
                                var gh = db.studyClassDAO().getStudyClass()
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })

        val spinner2 = findViewById<Spinner>(R.id.spinner2)
        spinner2.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // TODO Auto-generated method stub
                when(position){
                    1 -> {
                        showdialog(R.layout.addtype,2)
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.removeAllViews();
                    }
                    2 -> {//view?.let { basicAlert(it) }
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.removeAllViews();
                        var type = db.subjectDAO().getSubject()
                        var num = db.subjectDAO().getSubject().count()
                        when(num) {
                            0 -> showToast(message = "Нет дисциплин!")
                            else -> {
                                var button4_Id: Int = 111
                                var arr: MutableList<String> = mutableListOf()
                                for (i in 0..num - 1) {
                                    arr += type[i].abbr
                                    val button_dynamic = Button(this@MainActivity)
                                    button_dynamic.layoutParams = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    button_dynamic.text = arr[i]
                                    var b = "button$i"
                                    button_dynamic.setId(button4_Id)
                                    button4_Id++
                                    button_dynamic.setBackgroundResource(R.drawable.rec)
                                    button_dynamic.setOnClickListener(object : View.OnClickListener {
                                        override fun onClick(view: View?) {
                                            ssubject = type[i].id!!
                                            showToast(message = "Кнопка нажата! ${ssubject}")
                                        }
                                    });
                                    //showToast(message = "Название: ${b}")
                                    ll.addView(button_dynamic)
                                    //ll.removeAllViews();
                                }
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })

        val spinner3 = findViewById<Spinner>(R.id.spinner3)
        spinner3.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // TODO Auto-generated method stub
                when(position){
                    1 -> {
                        showdialog(R.layout.addtype,3)
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.removeAllViews();
                    }
                    2 -> {//view?.let { basicAlert(it) }
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.removeAllViews();
                        var type = db.taskDAO().getTaskType()
                        var num = db.taskDAO().getTaskType().count()
                        var button4_Id: Int = 1111
                        var arr: MutableList<String> = mutableListOf()
                        for(i in 0..num-1){
                            arr += type[i].abbr
                            val button_dynamic = Button(this@MainActivity)
                            button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            button_dynamic.text = arr[i]
                            button_dynamic.setId(button4_Id)
                            button4_Id++
                            button_dynamic.setBackgroundResource(R.drawable.rec)
                            //button_dynamic.setOnClickListener{button_dynamic.setBackgroundColor(Color.argb(0,221,160,221))}
                            //showToast(message = "Название: ${b}")
                            ll.addView(button_dynamic)
                            //linearLayout.removeAllViews();
                        }
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })

        // Create an ArrayAdapter using a simple spinner layout and languages array
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items2)
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinner.adapter = adapter
        spinner2.adapter = adapter2
        spinner3.adapter = adapter3

        //basicAlert
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
        /*val recView = findViewById<RecyclerView>(R.id.list)
        recView.layoutManager = LinearLayoutManager(this)
        recView.adapter = StInGrAdapter(all)*/
        when(ssubject) {
            0L -> showToast(message = "Не выбрана дисциплина!")
            else -> {
                if (path.toString().endsWith("xls")) {
                    //pb.visibility = View.VISIBLE
                    ParseXML(context = this).readFromExcelFile(db, path!!, ssubject);
                    //pb.visibility = View.INVISIBLE
                }
                fillGridView ()
            }
        }
    }

    inline fun <reified T> toArray(list: List<*>): Array<T> {
        return (list as List<T>).toTypedArray()
    }

    private fun fillGridView(){
        //var st = db.studentDAO().getStudentGroup()
        var count = db.studentDAO().getStudentGroup(ssubject).count()
        //var g = db.studyGroupDAO().getStudyGroupinSub(ssubject)
        //var num = db.studyGroupDAO().getStudyGroupinSub(ssubject).count()
        var arr: MutableList<String> = mutableListOf()
        var gr: MutableList<Long> = mutableListOf()
        gvMain = findViewById<View>(R.id.gridView1) as GridView
        //gvMain!!.setAdapter(null)
        gvMain!!.adapter = null
        when(ssubject) {
            0L -> showToast(message = "Не выбран журнал!")
            else -> {
                if (count == 0) {
                    showToast(message = "Нет студентов!")
                } else {
                    var st = db.studentDAO().getStudentGroup(ssubject)
                    //var count = db.studentDAO().getStudentGroup().count()
                    var g = db.studyGroupDAO().getStudyGroupinSub(ssubject)
                    var num = db.studyGroupDAO().getStudyGroupinSub(ssubject).count()
                    var data = listOf<String>("f", "g", "l")
                    //var ffl = db.flowStudentsDAO().getSubjectById(ssubject)
                    //var term = db.flowStudentsDAO().getSubjectById(ssubject).count()
                    arr += "Название"
                    var o = st[0].id_group
                    showToast(message = "Выбор! ${count}")
                    showToast(message = "Выбор! ${num}")
                    for (j in 0..num - 1) {
                        if (g[j].id == o) {
                            arr += g[j].title
                            showToast(message = "Выбор - ${g[j].id}")
                        }
                    }
                    for (i in 0..count - 1) {
                        if (o == st[i].id_group) {
                            arr += st[i].toString()
                        } else {
                            o = st[i].id_group
                            for (j in 0..num - 1) {
                                if (g[j].id == o) {
                                    arr += g[j].title
                                }
                            }
                            arr += st[i].toString()
                        }
                    }
                    adapterr = ArrayAdapter<String>(this, R.layout.item, R.id.tvText, arr)
                    gvMain = findViewById<View>(R.id.gridView1) as GridView
                    gvMain!!.setAdapter(adapterr)
                    adjustGridView()
                }
            }
        }
    }

    private fun adjustGridView() {
        gvMain?.setNumColumns(GridView.AUTO_FIT);
        gvMain?.setNumColumns(1);
        gvMain?.setVerticalSpacing(10);
        gvMain?.setHorizontalSpacing(10);
    }

    override fun onStop() {
        super.onStop()
        db.close()
    }
    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }

    fun basicAlert(view: View){
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("Добавить вид занятия")
            setMessage("We have a message")
            setPositiveButton("Сохранить", DialogInterface.OnClickListener(function = positiveButtonClick))
            setNegativeButton("Отмена", negativeButtonClick)
            //setNeutralButton("Maybe", neutralButtonClick)
            show()
        }
    }
    fun showdialog(x: Int, y: Int){
        val builder = AlertDialog.Builder(this)
        val mDialogView = LayoutInflater.from(this).inflate(x, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        //showToast(message = "Сокращенное: ${x}")
        when(y) {
            1-> {
                val n: TextView = mDialogView.findViewById(R.id.dialogname)
                val sur: TextView = mDialogView.findViewById(R.id.dialoglastname)
                val mid: TextView = mDialogView.findViewById(R.id.dialogmiddleName)//dialog
                val grou: TextView = mDialogView.findViewById(R.id.dialog)
                val btDone: Button = mDialogView.findViewById(R.id.dialogSave)
                val btCancl: Button = mDialogView.findViewById(R.id.dialogCancel)//dialogfrom
                val findbtn: Button = mDialogView.findViewById(R.id.dialogfrom)
                val xmlbtn: Button = mDialogView.findViewById(R.id.dialogxml)//dialogSave
                //login button click of custom layout
                var students = mutableListOf<Student>()

                btDone.setOnClickListener {
                    //dismiss dialog
                    mAlertDialog.dismiss()
                    val name = n.text.toString()
                    val surname = sur.text.toString()
                    val middlename = mid.text.toString()
                    val groupe = grou.text.toString()
                    when (ssubject) {
                        0L -> showToast(message = "Не выбран журнал!")
                        else -> {
                            if (name.trim().isNotEmpty() && surname.trim()
                                    .isNotEmpty() && middlename.trim().isNotEmpty() && groupe.trim()
                                    .isNotEmpty()
                            ) {
                                val stgroup = StudyGroup(title = groupe, abbr = groupe)
                                val idExist = db.studyGroupDAO().isStudyGroupExist(stgroup.abbr)
                                val idgroup = when (idExist) {
                                    0L -> {
                                        db.studyGroupDAO().insertStudyGroup(stgroup)
                                    }
                                    else -> {
                                        idExist
                                    }
                                }
                                showToast(message = "Есть! ${idgroup}, ${ssubject}")
                                var d = db.flowStudentsDAO().insertFlowStudents(FlowStudents(id_journal = ssubject, id_group = idgroup))
                                showToast(message = "Добавили в flow!")
                                showToast(message = "Есть! ${idgroup}")
                                val student = Student(
                                    family = surname,
                                    name = name,
                                    patronymic = middlename,
                                    id_group = idgroup
                                )
                                val hasStudent = db.studentDAO().isStudentExistInGroup(
                                    idgroup = idgroup,
                                    family = surname,
                                    name = name,
                                    patronymic = middlename
                                )
                                if (hasStudent == 0L) {
                                    students.add(student)
                                }
                                val idstudents = db.studentDAO().insertStudents(students)
                                showToast(message = "Имя: ${name}, фамилия: ${surname}, отчество: ${middlename}, группа: ${groupe}")
                            } else showToast(message = "Заполните все поля!")
                        }
                    }
                }
                //cancel button click of custom layout
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }
                findbtn.setOnClickListener {
                    openFileDialog(mDialogView)
                    //ReadXml(mDialogView)
                    mAlertDialog.dismiss()
                }
                xmlbtn.setOnClickListener {
                    ReadXml(mDialogView)
                    mAlertDialog.dismiss()
                }
            }
            2-> {
                val w: TextView = mDialogView.findViewById(R.id.editword)
                val ww: TextView = mDialogView.findViewById(R.id.editfullword)
                val save: Button = mDialogView.findViewById(R.id.button)
                val btCancl: Button = mDialogView.findViewById(R.id.dialogCancel)
                save.setOnClickListener {
                    mAlertDialog.dismiss()
                    val word = w.text.toString()
                    val fullword = ww.text.toString()
                    if(word.trim().isNotEmpty() && fullword.trim().isNotEmpty()) {
                        var sub = Subject(title = fullword, abbr = word)
                        val idExist = db.subjectDAO().isSubjectExist(fullword, word)
                        val idgroup = when (idExist) {
                            0L -> db.subjectDAO().insertSubject(sub)
                            else -> {
                                idExist
                                //showToast(message = "Сокращенное: ${idExist}")
                            }
                        }
                        var j = Journal(id_subject = idgroup, note = "2021 год")
                        db.journalDAO().insertJournal(j)
                        showToast(message = "Сокращенное: ${word}, полное название: ${fullword}")
                    }
                    else showToast(message = "Заполните все поля!")
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
            3->{
                val w: TextView = mDialogView.findViewById(R.id.editword)
                val ww: TextView = mDialogView.findViewById(R.id.editfullword)
                val save: Button = mDialogView.findViewById(R.id.button)
                val btCancl: Button = mDialogView.findViewById(R.id.dialogCancel)
                save.setOnClickListener {
                    mAlertDialog.dismiss()
                    val word = w.text.toString()
                    val fullword = ww.text.toString()
                    if(word.trim().isNotEmpty() && fullword.trim().isNotEmpty()) {
                        val ex = db.taskDAO().isTaskTypeExist("Лабораторная работа", "ЛР")
                        when (ex) {
                            0L -> DBJournalHelper.addDAO()
                            else -> showToast(message = "Есть!")
                        }
                        val type = TaskType(title = fullword, abbr = word)
                        val idExist = db.taskDAO().isTaskTypeExist(fullword, word)
                        val idgroup = when (idExist) {
                            0L -> db.taskDAO().insertTaskType(type)
                            else -> {
                                idExist
                            }
                        }
                        showToast(message = "Сокращенное: ${word}, полное название: ${fullword}")
                    }
                    else showToast(message = "Заполните все поля!")
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                    //var ll : LinearLayout = mDialogView.findViewById(R.id.layout2)
                }
            }
        }
    }

    private fun showToast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }
}