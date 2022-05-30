package com.donntu.teachjournal

import android.Manifest
import android.R.attr
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.core.view.setPadding
import com.donntu.teachjournal.db.DBJournalHelper
import com.donntu.teachjournal.db.entity.*
import com.donntu.teachjournal.db.utils.ExporterImporterDB
import com.donntu.teachjournal.utils.ParseXML


var select: String? =null

class MainActivity : AppCompatActivity()//, AdapterView.OnItemSelectedListener
{
    //удалил комментарий
    var path: Uri? = null
    var gvMain: GridView? = null
    var adapterr: ArrayAdapter<String>? = null
    var languages = arrayOf("Группа","Добавить", "Показать все")
    var list_of_items = arrayOf("Дисциплина", "Добавить дисциплину", "Создать журнал", "Показать все")
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (!hasPermissionsWithStorage())
            return false
        return when (item.itemId) {
            R.id.itemExportDB -> {
                val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
                startActivityForResult(Intent.createChooser(intent, "Выбор пути экспорта"), 666)
                true
            }
            R.id.itemImportDB ->{
                ExporterImporterDB().importDB(db, this, db_name = "TeachJournal.db")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    var id_journal: Long = 0L
    companion object {
        var spinner:Spinner? =null
        var spinner2:Spinner? =null
        var spinner3:Spinner? =null
    }
    val db by lazy { DBJournalHelper.getDatabase(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerSpinner()
        var arrayAdapter: ArrayAdapter<*>
        var addgroup = findViewById<Button>(R.id.dialogaddgroup)
        addgroup.setOnClickListener {
            when (id_journal) {
                0L -> showToast(message = "Не выбрана дисциплина!")
                else -> {
                    val builder = AlertDialog.Builder(this)
                    val mDialogView = LayoutInflater.from(this).inflate(R.layout.listofgroup, null)
                    var ll = mDialogView.findViewById<ListView>(R.id.list)
                    var gr: MutableList<String> = mutableListOf()
                    var grr = db.studyGroupDAO().getStudyGroup()
                    var mapId = mutableMapOf<Int, Long>()
                    grr.forEachIndexed{index, entity->
                        gr += grr[index].abbr
                        mapId[index] = grr[index].id!!.toLong()
                    }
                    arrayAdapter = ArrayAdapter(this, R.layout.item, gr)
                    ll.adapter = arrayAdapter
                    val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                    val mAlertDialog = mBuilder.show()
                    ll.onItemClickListener = object : AdapterView.OnItemClickListener {
                        override fun onItemClick(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long,
                        ) {
                            val pp = mapId[p2]!!
                            val journal = db.journalDAO().getJournal(id_journal)
                            val insId = db.flowStudentsDAO().insertFlowStudents(FlowStudents(id_journal = id_journal, id_group = pp))
                            showToast(message = "В журнал ${id_journal}: \"${journal.note}\" добавлена группа ${grr[p2].abbr}!")
                            mAlertDialog.dismiss()
                        }
                    }

                }
            }
        }
    }

    fun registerSpinner(){
        spinner = findViewById<Spinner>(R.id.spinner)

        spinner?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinner?.setSelection(0);
                when(position){
                    1 -> {
                        clearLayout()
                        showdialog(R.layout.addsubject, 1)
                    }
                    2 -> {
                        clearLayout()
                        when(id_journal){
                            0L -> showToast(message = "Журнал не выбран!")
                            else -> {
                                showTable(id_journal)
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })

        spinner2 = findViewById<Spinner>(R.id.spinner2)
        spinner2?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinner2?.setSelection(0);
                when(position){
                    1 -> {
                        showdialog(R.layout.addtype,2)
                    }
                    2 -> {
                        when(db.subjectDAO().getSubject().count()){
                            0 -> showToast(message = "Нет дисциплин!")
                            else ->{
                                showdialog(R.layout.create_journal,4)
                            }
                        }
                    }
                    3 -> {
                        clearLayout()
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.visibility = View.VISIBLE
                        var subject = db.subjectDAO().getSubject()
                        when(subject.count()) {
                            0 -> showToast(message = "Нет дисциплин!")
                            else -> {
                                var button4_Id: Int = 111
                                var arr: MutableList<String> = mutableListOf()
                                subject.forEachIndexed{i, s->
                                    arr += s.abbr
                                    val button_dynamic = Button(this@MainActivity)
                                    button_dynamic.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT )
                                    button_dynamic.text = arr[i]
                                    var b = "button$i"
                                    button_dynamic.setId(button4_Id)
                                    button4_Id++
                                    button_dynamic.setBackgroundResource(R.drawable.rec)
                                    button_dynamic.setOnClickListener(object : View.OnClickListener {
                                        override fun onClick(view: View?) {
                                            val lay =findViewById<LinearLayout>(R.id.layout3)
                                            lay.removeAllViews()
                                            var grr = db.journalDAO().getJournalBySubjectId(s.id!!)
                                            when(grr.count()){
                                                0 -> showToast(message = "Журналы по ${s.abbr} еще не созданы!")
                                                else ->{
                                                    var arrayAdapter: ArrayAdapter<*>
                                                    val mDialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.listofgroup, null)
                                                    var listView = mDialogView.findViewById<ListView>(R.id.list)

                                                    var gr: MutableList<String> = mutableListOf()
                                                    var mapId = mutableMapOf<Int, Long>()
                                                    grr.forEachIndexed{index, entity->
                                                        gr += grr[index].note
                                                        mapId[index] = grr[index].id!!.toLong()
                                                    }
                                                    arrayAdapter = ArrayAdapter(this@MainActivity, R.layout.item, gr)
                                                    listView.adapter = arrayAdapter
                                                    listView.onItemClickListener = object : AdapterView.OnItemClickListener {
                                                        override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                                            findViewById<Button>(R.id.dialogaddgroup).visibility = View.VISIBLE
                                                            id_journal = mapId[p2]!!
                                                            showToast(message = "Выбран журнал ${id_journal} по ${s.abbr}")
                                                        }
                                                    };
                                                    listView.onItemLongClickListener = object: AdapterView.OnItemLongClickListener{
                                                        override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long):Boolean {
                                                            id_journal = mapId[p2]!!
                                                            spinner?.setSelection(2)
                                                            return true
                                                        }
                                                    }
                                                    lay.addView(creatTextView(text ="Журналы по ${s.abbr}",w=600,bg=Color.WHITE, align = View.TEXT_ALIGNMENT_CENTER))
                                                    lay.addView(mDialogView)
                                                }
                                            }
                                        }
                                    })
                                    ll.addView(button_dynamic)
                                }
//
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        })

        spinner3 = findViewById<Spinner>(R.id.spinner3)
        spinner3?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinner3?.setSelection(0)
                when(position){
                    1 -> {
                        showdialog(R.layout.addtype,3)
                        clearLayout()
                    }
                    2 -> {//view?.let { basicAlert(it) }
                        clearLayout()
                        val ll = findViewById<LinearLayout>(R.id.layout2)
                        ll.visibility = View.VISIBLE
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
        spinner?.adapter = adapter
        spinner2?.adapter = adapter2
        spinner3?.adapter = adapter3

    }
    fun clearLayout(){
        var lay2 = findViewById<LinearLayout>(R.id.layout2)
        lay2.removeAllViews()
        findViewById<LinearLayout>(R.id.layout3).removeAllViews()
        setInvisibleView()
    }
    fun setInvisibleView(){
        var lay2 = findViewById<LinearLayout>(R.id.layout2)
        lay2.visibility = View.INVISIBLE
        findViewById<LinearLayout>(R.id.linearLayoutButtons).forEach { view ->
            view.visibility = View.INVISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK && data != null) {
            val filePath = data?.data
            path = filePath
            when (requestCode) {
                //Чтение xls
                777 -> {
                    if(path.toString().endsWith("xls"))
                        showdialog(R.layout.addsubject, 1)
                }
                //путь для экспорта
                666 -> {
                    ExporterImporterDB().exportDB(db, this, uri = path!!, db_name = "TeachJournal.db")
                }
            }
        }
    }

    fun ReadXml(view: View){
        if (path.toString().endsWith("xls")) {
            ParseXML(context = this).readFromExcelFile(db, path!!);
        }

    }

    inline fun <reified T> toArray(list: List<*>): Array<T> {
        return (list as List<T>).toTypedArray()
    }
    fun creatTextView(
        text: String,
        w: Int = 100, h: Int = 100,
        bg: Int = Color.rgb(250, 218, 221),
        visible: Int = View.VISIBLE,
        align: Int = View.TEXT_ALIGNMENT_INHERIT,
        ts: Double = 14.0,
    ): TextView{
        var view = TextView(this)  as TextView
        view.text = text
        view.width = w
        view.height = h
        view.setBackgroundColor(bg)
        view.setPadding(15)
        view.textSize = ts.toFloat()
        view.visibility = visible
        view.textAlignment = align
        return  view
    }
    fun showTable(idJournal: Long) {
        var count_class = 4
        var count_work = 5
        var wHead = 300
        var hHead = 150
        var wMain = 300
        var hMain = 100
        var wadd = 60
        var studyGroupsFlow = db.studyGroupDAO().getStudyGroupinSub(idJournal)
        var studentsFlow = db.studentDAO().getStudentGroupByJournal(idJournal)
        val max_size: Int = (studentsFlow.maxByOrNull{it.toString().length}).toString().length

        val recView = LinearLayout(this@MainActivity)
        recView.orientation = LinearLayout.VERTICAL
        recView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        for(gr in studyGroupsFlow){
            val groupLine = LinearLayout(this)
            groupLine.orientation = LinearLayout.HORIZONTAL
            val tvGroup = creatTextView(text =""+gr.abbr, bg =Color.LTGRAY,h=hHead, ts=25.0)
            tvGroup.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            groupLine.addView(tvGroup)
            recView.addView(groupLine)
            //Header
            val headLine = LinearLayout(this)
            headLine.orientation = LinearLayout.HORIZONTAL
            headLine.addView(creatTextView(text ="ID", h =hHead, align = View.TEXT_ALIGNMENT_CENTER))
            headLine.addView(creatTextView(text ="ФИО", h =hHead, w =max_size*23, align = View.TEXT_ALIGNMENT_CENTER))
            for(i in 0..count_class) {
                headLine.addView(creatTextView(
                    text = "Занятие\n"+i.toString(),
                    h =hHead, w = wHead, align = View.TEXT_ALIGNMENT_CENTER))
            }
            //Пустая колонка
            headLine.addView(creatTextView(text = "+", h =hHead, w = wadd, align =4))
            for(i in 0..count_work) {
                headLine.addView(creatTextView(
                    text = "ЛР\n"+i.toString(),
                    h =hHead,
                    w = wHead, align = View.TEXT_ALIGNMENT_CENTER))
            }
            //Пустая колонка
            headLine.addView(creatTextView(text = "+", h =hHead, w = wadd, align =4))
            recView.addView(headLine)


            //Основная информация
        for (item in studentsFlow.filter { st -> st.id_group == gr.id }) {
            val mainLine = LinearLayout(this)
            mainLine.orientation = LinearLayout.HORIZONTAL
            mainLine.addView(creatTextView(text =item.id.toString(), bg = Color.GRAY, align = View.TEXT_ALIGNMENT_CENTER))
            mainLine.addView(creatTextView(text =item.toString(), w =max_size*23, bg = Color.RED))
            //Описание занятий
            for(i in 0..count_class) {
                val tv = creatTextView(
                    text = i.toString(), align = View.TEXT_ALIGNMENT_CENTER,
                    w = wMain, bg = Color.rgb((0..255).random(),(0..255).random(),(0..255).random()))
                tv.setOnClickListener {
                    tv.setBackgroundColor(Color.RED)
                    true
                }
                tv.setOnLongClickListener {
                    tv.setBackgroundColor(Color.WHITE)
                    true
                }
                mainLine.addView(tv)
            }
            mainLine.addView(creatTextView(text = "|", w = wadd, align =4))
            //Описание заданий
            for(i in 0..count_work) {
                val tv = creatTextView(
                    text = i.toString(), align = View.TEXT_ALIGNMENT_CENTER,
                    w = wMain, bg = Color.rgb((0..255).random(),(0..255).random(),(0..255).random()))
                tv.setOnClickListener {
                    tv.setBackgroundColor(Color.GREEN)
                    true
                }
                tv.setOnLongClickListener {
                    tv.setBackgroundColor(Color.YELLOW)
                    true
                }
                mainLine.addView(tv)
            }
            mainLine.addView(creatTextView(text = "|", w = wadd, align =4))
            recView.addView(mainLine)
        }
        }

        val horizScrollView = HorizontalScrollView(this@MainActivity)
        horizScrollView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        horizScrollView.requestLayout();

        val scrollView = ScrollView(this@MainActivity)
        scrollView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        scrollView.requestLayout();

        scrollView.addView(recView)
        horizScrollView.addView(scrollView)
        findViewById<LinearLayout>(R.id.layout3).addView(horizScrollView)
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
        val mDialogView = LayoutInflater.from(this).inflate(x, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

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
                    when (id_journal) {
                        0L -> showToast(message = "Не выбран журнал!")
                        else -> {
                            if (name.trim().isNotEmpty() && surname.trim().isNotEmpty() &&
                                middlename.trim().isNotEmpty() && groupe.trim().isNotEmpty()
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

                                showToast(message = "Есть! ${idgroup}, ${id_journal}")
                                var d = db.flowStudentsDAO().insertFlowStudents(FlowStudents(id_journal = id_journal, id_group = idgroup))
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
                    if (hasPermissionsWithStorage()){
                        val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
                        //val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).putExtra(DocumentsContract.EXTRA_INITIAL_URI, DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Android"))
                        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
                    }
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
                        when(idExist){
                            0L -> {
                                if(db.subjectDAO().insertSubject(sub)!=0L)
                                    showToast(message = "Сокращенное: ${word}, полное название: ${fullword}")
                            }
                            else -> {
                                showToast(message="Такая дисциплина существует")
                            }
                        }
                    }
                    else showToast(message = "Заполните все поля!")
                    spinner2?.setSelection(3)
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
                    spinner3?.setSelection(2)
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                    //var ll : LinearLayout = mDialogView.findViewById(R.id.layout2)
                }
            }
            4-> {
                val tv: TextView = mDialogView.findViewById(R.id.noteJournal)
                val sp: Spinner = mDialogView.findViewById(R.id.spinnerSubject)
                val save: Button = mDialogView.findViewById(R.id.dialogSave)
                val btCancl: Button = mDialogView.findViewById(R.id.dialogCancel)
                var gr: MutableList<String> = mutableListOf()
                var grr = db.subjectDAO().getSubject()
                var mapId = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += grr[index].abbr
                    mapId[index] = grr[index].id!!.toLong()
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gr)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp.adapter = adapter
                sp?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        spinner3?.setSelection(0)
                        tv.text = ""
                        tv.hint = ""+grr[position].abbr+": + текст..."
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>?) {
                        // TODO Auto-generated method stub
                    }
                })
                save.setOnClickListener {
                    mAlertDialog.dismiss()
                    val note = tv.text.toString()
                    var selectedItem = sp.selectedItemPosition
                    val abbr = grr[selectedItem].abbr
                    val id_subject = mapId[selectedItem]!!
                    if(note.trim().isNotEmpty() && sp.count != 0) {
                        var j = Journal(id_subject = id_subject, note = "${abbr}: ${note}")
                        var insId =db.journalDAO().insertJournal(j)
                        showToast(message = "Добавлен журнал по: ${abbr} с id: ${insId}")
                    }
                    else showToast(message = "Заполните все поля!")
                    spinner2?.setSelection(3)
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
        }
    }

    fun hasPermissionsWithStorage(): Boolean{
        if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
            return false
        } else {
            return true
        }
    }
    private fun showToast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }
}