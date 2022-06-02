package com.donntu.teachjournal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.core.view.setPadding
import com.donntu.teachjournal.db.DBJournalHelper
import com.donntu.teachjournal.db.entity.*
import com.donntu.teachjournal.db.dao.StudyClassWithInfo
import com.donntu.teachjournal.db.dao.StudyTaskWithInfo
import com.donntu.teachjournal.db.utils.ExporterImporterDB
import com.donntu.teachjournal.utils.ParseXML
import java.sql.Date
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity()
{
    private var path: Uri? = null
    val listSpinnerGroup = arrayOf("Группа","Добавить", "Показать все")
    val listSpinnerSubject = arrayOf("Дисциплина", "Добавить дисциплину", "Создать журнал", "Показать все")
    val listSpinnerClassesType = arrayOf("Вид занятия", "Добавить", "Показать все")
    var idJournal: Long = 0L
    private var markInstrumentStudyClass = 0L
    private var symbolPass = 0
    private var markInstrumentStudyTask = 0L
    var spinnerGroup:Spinner? =null
    var spinnerSubject:Spinner? =null
    var spinnerClassType:Spinner? =null
    private var writeMode:Boolean? = true


    val db by lazy { DBJournalHelper.getDatabase(this) }

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
                //val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
                //startActivityForResult(Intent.createChooser(intent, "Выбор пути экспорта"), 666)
                true
            }
            R.id.itemImportDB ->{
                ExporterImporterDB().importDB(db, this, db_name = "TeachJournal.db")
                true
            }
            R.id.dropDB ->{
                db.clearAllTables()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerSpinner()
    }

    private fun registerSpinner(){
        spinnerGroup = findViewById(R.id.spinner)
        spinnerGroup?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinnerGroup?.setSelection(0)
                when(position){
                    1 -> {
                        clearLayout()
                        showdialog(R.layout.addsubject, 1)
                    }
                    2 -> {
                        showListGroupAndStudents(idJournal)
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }

        spinnerSubject = findViewById(R.id.spinner2)
        spinnerSubject?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinnerSubject?.setSelection(0)
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
                        showListSubjectAndJournals()
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }

        spinnerClassType = findViewById(R.id.spinner3)
        spinnerClassType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinnerClassType?.setSelection(0)
                when(position){
                    1 -> {
                        showdialog(R.layout.addtype,3)
                        clearLayout()
                    }
                    2 -> {
                        showListClassesType()
                    }
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
                // TODO Auto-generated method stub
            }
        }
        // Create an ArrayAdapter using a simple spinner layout and languages array
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listSpinnerGroup)
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, listSpinnerSubject)
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, listSpinnerClassesType)
        // Set layout to use when the list of choices appear
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        spinnerGroup?.adapter = adapter
        spinnerSubject?.adapter = adapter2
        spinnerClassType?.adapter = adapter3

    }

    private fun showJournal(id_journal: Long) {
        writeMode = false
        markInstrumentStudyClass = 0L
        markInstrumentStudyTask = 0L
        when(id_journal){
            0L -> showToast(message = "Журнал не выбран!")
            else -> {
                clearLayout()
                showTable(id_journal)
            }
        }
    }
    private fun showListGroupAndStudents(idJournal: Long) {
        clearLayout()
        val ll = findViewById<LinearLayout>(R.id.layout2)
        ll.visibility = View.VISIBLE
        val group = db.studyGroupDAO().getStudyGroup()
        when(group.count()) {
            0 -> {}
            else -> {
                val arr: MutableList<String> = mutableListOf()
                group.forEachIndexed{ i, gr->
                    arr += gr.abbr
                    val btn = Button(this@MainActivity)
                    btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT )
                    btn.text =  gr.abbr
                    btn.setBackgroundResource(R.drawable.rec)
                    btn.setOnClickListener {
                        val lay = findViewById<LinearLayout>(R.id.layout3)
                        lay.removeAllViews()
                        val students = db.studentDAO().getStudentsByIdGroup(gr.id!!)
                        when (students.count()) {
                            0 -> showToast(message = "В группе ${gr.abbr} еще нет студентов!")
                            else -> {
                                val arrayAdapter: ArrayAdapter<*>
                                val mDialogView = LayoutInflater.from(this@MainActivity)
                                    .inflate(R.layout.list, null)
                                val listView = mDialogView.findViewById<ListView>(R.id.list)

                                val st: MutableList<String> = mutableListOf()
                                val mapId = mutableMapOf<Int, Long>()
                                students.forEachIndexed { index, entity ->
                                    st += (entity.family + " " + entity.name + " " + entity.patronymic)
                                    mapId[index] = entity.id!!.toLong()
                                }
                                arrayAdapter = ArrayAdapter(this@MainActivity, R.layout.item, st)
                                listView.adapter = arrayAdapter
                                listView.onItemClickListener =
                                    object : AdapterView.OnItemClickListener {
                                        override fun onItemClick(
                                            p0: AdapterView<*>?,
                                            p1: View?,
                                            p2: Int,
                                            p3: Long
                                        ) {
                                            this@MainActivity.idJournal = mapId[p2]!!
                                            val menu = PopupMenu(this@MainActivity, p1)
                                            menu.menu.apply {
                                                add("Удалить студента").setOnMenuItemClickListener {
                                                    val builder =
                                                        AlertDialog.Builder(this@MainActivity)
                                                    with(builder)
                                                    {
                                                        setTitle("Удаление")
                                                        setMessage("Вы уверены, что хотите удалить?")
                                                        setPositiveButton("Да") { _, _ ->
                                                            db.studentDAO()
                                                                .deleteStudent(students[p2])
                                                            showListGroupAndStudents(idJournal)
                                                        }
                                                        setNegativeButton("Нет", null)
                                                        show()
                                                    }
                                                    true
                                                }
                                            }
                                            menu.show()
                                        }
                                    }
                                lay.addView(creatTextView(text = "Студенты группы: ${gr.abbr}",
                                    w = 600,
                                    bg = Color.WHITE,
                                    align = View.TEXT_ALIGNMENT_CENTER))
                                lay.addView(mDialogView)
                            }
                        }
                    }
                    btn.setOnLongClickListener {
                        val menu = PopupMenu(this@MainActivity, btn)
                        menu.menu.apply {
                            add("Удалить группу").setOnMenuItemClickListener {
                                val builder =
                                    AlertDialog.Builder(this@MainActivity)
                                with(builder)
                                {
                                    setTitle("Удаление")
                                    setMessage("Вы уверены, что хотите удалить?")
                                    setPositiveButton("Да") { _, _ ->
                                        db.studyGroupDAO().deleteStudyGroup(gr)
                                        showListGroupAndStudents(idJournal)
                                    }
                                    setNegativeButton("Нет", null)
                                    show()
                                }
                                true
                            }
                        }
                        menu.show()
                        true
                    }
                    if(i==0)
                        btn.performClick()
                    ll.addView(btn)
                }

            }
        }
    }
    private fun showListSubjectAndJournals() {
        clearLayout()
        val ll = findViewById<LinearLayout>(R.id.layout2)
        ll.visibility = View.VISIBLE
        val subject = db.subjectDAO().getSubject()
        when(subject.count()) {
            0 -> showToast(message = "Нет дисциплин!")
            else -> {
                val arr: MutableList<String> = mutableListOf()
                subject.forEachIndexed{i, s->
                    arr += s.abbr
                    val btn = Button(this@MainActivity)
                    btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT )
                    btn.text = arr[i]
                    btn.setBackgroundResource(R.drawable.rec)
                    btn.setOnClickListener {
                        val lay = findViewById<LinearLayout>(R.id.layout3)
                        lay.removeAllViews()
                        val journal = db.journalDAO().getJournalBySubjectId(s.id!!)
                        when (journal.count()) {
                            0 -> showToast(message = "Журналы по ${s.abbr} еще не созданы!")
                            else -> {
                                val arrayAdapter: ArrayAdapter<*>
                                val mDialogView = LayoutInflater.from(this@MainActivity)
                                    .inflate(R.layout.list, null)
                                val listView = mDialogView.findViewById<ListView>(R.id.list)

                                val gr: MutableList<String> = mutableListOf()
                                val mapId = mutableMapOf<Int, Long>()
                                journal.forEachIndexed { index, entity ->
                                    gr += journal[index].note
                                    mapId[index] = journal[index].id!!.toLong()
                                }
                                arrayAdapter = ArrayAdapter(this@MainActivity, R.layout.item, gr)
                                listView.adapter = arrayAdapter
                                listView.onItemClickListener =
                                    object : AdapterView.OnItemClickListener {
                                        override fun onItemClick(
                                            p0: AdapterView<*>?,
                                            p1: View?,
                                            p2: Int,
                                            p3: Long
                                        ) {
                                            idJournal = mapId[p2]!!
                                            val menu = PopupMenu(this@MainActivity, p1)
                                            menu.menu.apply {
                                                add("Открыть журнал").setOnMenuItemClickListener {
                                                    showJournal(idJournal)
                                                    true
                                                }
                                                add("Добавить в журнал группу").setOnMenuItemClickListener {
                                                    addGroupInJournal(idJournal)
                                                    true
                                                }

                                                add("Удалить журнал").setOnMenuItemClickListener {
                                                    val builder =
                                                        AlertDialog.Builder(this@MainActivity)
                                                    with(builder)
                                                    {
                                                        setTitle("Удаление")
                                                        setMessage("Вы уверены, что хотите удалить?")
                                                        setPositiveButton("Да") { _, _ ->
                                                            db.journalDAO()
                                                                .deleteJournal(journal[p2])
                                                            spinnerSubject?.setSelection(3)
                                                        }
                                                        setNegativeButton("Нет", null)
                                                        show()
                                                    }
                                                    true
                                                }
                                            }
                                            menu.show()
                                        }
                                    }
                                lay.addView(creatTextView(text = "Журналы по ${s.abbr}",
                                    w = 600,
                                    bg = Color.WHITE,
                                    align = View.TEXT_ALIGNMENT_CENTER))
                                lay.addView(mDialogView)
                            }
                        }
                    }
                    btn.setOnLongClickListener {
                        val menu = PopupMenu(this@MainActivity, btn)
                        menu.menu.apply {
                            add("Удалить предмет").setOnMenuItemClickListener {
                                val builder =
                                    AlertDialog.Builder(this@MainActivity)
                                with(builder)
                                {
                                    setTitle("Удаление")
                                    setMessage("Вы уверены, что хотите удалить?")
                                    setPositiveButton("Да") { _, _ ->
                                        db.subjectDAO().deleteSubject(s)
                                        showListSubjectAndJournals()
                                    }
                                    setNegativeButton("Нет", null)
                                    show()
                                }
                                true
                            }
                        }
                        menu.show()
                        true
                    }
                    if(i==0)
                        btn.performClick()
                    ll.addView(btn)
                }

            }
        }
    }
    private fun showListClassesType() {
        clearLayout()
        val ll = findViewById<LinearLayout>(R.id.layout2)
        ll.visibility = View.VISIBLE
        val type = db.studyClassDAO().getStudyClassType()
        val arr: MutableList<String> = mutableListOf()
        type.forEachIndexed { i, typ ->

            arr += typ.abbr
            val btn = Button(this@MainActivity)
            btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            btn.text = arr[i]
            btn.setBackgroundResource(R.drawable.rec)
            btn.setOnLongClickListener {
                val menu = PopupMenu(this@MainActivity, btn)
                menu.menu.apply {
                    add("Удалить тип занятия").setOnMenuItemClickListener {
                        val builder =
                            AlertDialog.Builder(this@MainActivity)
                        with(builder)
                        {
                            setTitle("Удаление")
                            setMessage("Вы уверены, что хотите удалить?")
                            setPositiveButton("Да") { _, _ ->
                                db.studyClassDAO().deleteStudyClassType(type[i])
                                showListClassesType()
                            }
                            setNegativeButton("Нет", null)
                            show()
                        }
                        true
                    }
                }
                menu.show()
                true
            }
            ll.addView(btn)
        }
    }
    fun creatTextView(
        text: String,
        w: Int = 100, h: Int = 100,
        bg: Int = 0,
        visible: Int = View.VISIBLE,
        align: Int = View.TEXT_ALIGNMENT_INHERIT,
        ts: Double = 14.0,
        lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT),
    ): TextView{
        val view = TextView(this)
        view.text = text
        view.width = w
        view.height = h
        view.setBackgroundColor(bg)
        view.setPadding(15)
        view.textSize = ts.toFloat()
        view.visibility = visible
        view.textAlignment = align
        lp.setMargins(1,1,1,1)
        view.layoutParams = lp
        return  view
    }
    private val gold: Int = Color.rgb(255, 215, 0)

    private fun showTable(idJournal: Long) {
        val wHead = 300
        val hHead = 220
        val wMain = 300
        val hMain = 100
        val wadd = 100
        val studyGroupsFlow = db.studyGroupDAO().getStudyGroupinSub(idJournal)
        val studentsFlow = db.studentDAO().getStudentGroupByJournal(idJournal)
        val max_size: Int = (studentsFlow.maxByOrNull{it.toString().length}).toString().length*24

        val recView = LinearLayout(this@MainActivity)
        recView.orientation = LinearLayout.VERTICAL
        recView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        recView.setPadding(15,10,15,20)
        recView.setBackgroundColor(Color.LTGRAY)

        for(gr in studyGroupsFlow){
            drawGroupLine(line=recView, group = gr, hHead = 150, bg=gold)
            val classes = db.studyClassDAO().getStudyClassByIdJournal(this.idJournal)
            val tasks = db.taskDAO().getTaskByIdJournal(this.idJournal)
            drawHeaderLine(line=recView, size_fio_column=max_size, bg=Color.rgb(244, 164, 96), h=hHead, w=wHead, wadd = wadd, classes=classes, tasks=tasks)
            var ni = 0
            studentsFlow.filter { st -> st.id_group == gr.id }.forEachIndexed { i, item->
                ni++
                val mainLine = LinearLayout(this)
                mainLine.orientation = LinearLayout.HORIZONTAL
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(3,3,3,3)
                mainLine.layoutParams = lp
                val lineColor = when(i%2){
                    0-> Color.rgb(255, 245, 238)
                    else -> Color.rgb(245, 222, 179)
                }
                mainLine.addView(creatTextView(text =ni.toString(), bg = lineColor, align = View.TEXT_ALIGNMENT_CENTER))
                mainLine.addView(creatTextView(text =item.id.toString(), bg = lineColor, align = View.TEXT_ALIGNMENT_CENTER))//id студента
                mainLine.addView(creatTextView(text =item.toString(), w =max_size, bg = lineColor))//ФИО

                val tvHours = creatTextView(text = "", align = View.TEXT_ALIGNMENT_CENTER,w = wMain, bg = lineColor)
                tvHours.setOnClickListener {
                    val classes = db.studyClassDAO().getStudyClassByIdJournal(this.idJournal).count()
                    val studentNotPass = db.studyAttendMarkDAO().getStudyAttendMarkByIdJournalAndIdStudent(
                        this.idJournal, item.id!!).
                    filter { st-> st.attendMark.id_study_mark_type != 3L}
                    tvHours.text = "${studentNotPass.count()}/$classes"
                }
                tvHours.performClick()
                //Столбцы занятий
                drawColumnsWithClass(mainLine=mainLine, size_fio_column=max_size, bg = lineColor, w=wMain, h=hMain ,student=item, classes=classes, tvHours=tvHours)
                mainLine.addView(creatTextView(text = "|", w = wadd, bg=gold, align =4))//Пустой столбец

                val tvDopusk = creatTextView(text = "Допуск", align = View.TEXT_ALIGNMENT_CENTER,w = wMain, bg = lineColor)
                tvDopusk.setOnClickListener {
                    val tasks = db.taskDAO().getTaskByIdJournal(this.idJournal).count()
                    val taskDone = db.studyTaskMarkDAO().getStudyTaskMarksByIdJournalAndIdStudent(
                        this.idJournal, item.id!!).count()
                    tvDopusk.text = if(tasks == taskDone) "Да" else "Нет"
                }
                tvDopusk.performClick()
                //Столбец с работами
                drawColumnsWithWork(mainLine=mainLine, size_fio_column=max_size, bg = lineColor, w=wMain, h=hMain,student=item,tasks=tasks,tvDopusk=tvDopusk)
                mainLine.addView(creatTextView(text = "|", w = wadd, bg=gold, align =4))//Пустой столбец
                mainLine.addView(tvHours)
                mainLine.addView(tvDopusk)
                recView.addView(mainLine)

        }
        }

        val horizScrollView = HorizontalScrollView(this@MainActivity)
        horizScrollView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        horizScrollView.requestLayout()

        val scrollView = ScrollView(this@MainActivity)
        scrollView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        scrollView.requestLayout()

        scrollView.addView(recView)
        horizScrollView.addView(scrollView)
        findViewById<LinearLayout>(R.id.layout3).addView(horizScrollView)
    }

    private fun drawHeaderLine(
        line: LinearLayout,
        size_fio_column: Int,
        bg: Int = Color.WHITE,
        h: Int = 220,
        w: Int = 300,
        wadd: Int = 100,
        classes: List<StudyClassWithInfo>,
        tasks: List<StudyTaskWithInfo>,
    ) {
        val headLine = LinearLayout(this)
        headLine.orientation = LinearLayout.HORIZONTAL
        headLine.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)

        headLine.addView(creatTextView(text ="№", h = h, bg=bg, align = View.TEXT_ALIGNMENT_CENTER))
        headLine.addView(creatTextView(text ="ID", h = h, bg=bg, align = View.TEXT_ALIGNMENT_CENTER))
        headLine.addView(creatTextView(text ="ФИО", h = h, bg=bg, w =size_fio_column, align = View.TEXT_ALIGNMENT_CENTER))
        classes.forEachIndexed{ i, cl ->
            val type = cl.type.abbr
            val detail = cl.cl
            val tvClass = creatTextView(
                text = ""+type+"\n"+detail.data+"\n"+(detail.theme?:"Темы нет"),
                bg=bg, h = h, w = w, align = View.TEXT_ALIGNMENT_CENTER)
            tvClass.setOnClickListener {
                val menu = PopupMenu(this@MainActivity, tvClass)
                menu.menu.apply {
                    add("Удалить занятие").setOnMenuItemClickListener {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        with(builder)
                        {
                            setTitle("Удаление")
                            setMessage("Вы уверены, что хотите удалить?")
                            setPositiveButton("Да") { _, _ ->
                                db.studyClassDAO().deleteStudyClass(cl.cl)
                                showJournal(idJournal)
                            }
                            setNegativeButton("Нет", null)
                            show()
                        }
                        true
                    }
                }
                menu.show()
            }
            headLine.addView(tvClass)

        }
        //Колонка добавления занятия
        val addSC = creatTextView(text = "+", h =h, w = wadd, bg=gold, align =4)
        addSC.setOnClickListener{
            addStudyClass(id_journ = idJournal)
        }
        headLine.addView(addSC)
        tasks.forEachIndexed{ i, task ->
            val type = task.type.abbr
            val num_task = task.task.id_cur_num_task
            val count_task = db.taskDAO().getLastNumberTaskByTypeInJournal(idJournal,task.task.id_task_type)
            val text =when(num_task){
                1L->if(count_task==1L) "" else "1"
                else -> num_task.toString()
            }
            val tvTasks = creatTextView(
                text = "$type $text", bg = bg,
                h = h,
                w = w, align = View.TEXT_ALIGNMENT_CENTER)
            tvTasks.setOnClickListener {
                val menu = PopupMenu(this@MainActivity, tvTasks)
                menu.menu.apply {
                    add("Удалить задание").setOnMenuItemClickListener {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        with(builder)
                        {
                            setTitle("Удаление")
                            setMessage("Вы уверены, что хотите удалить?")
                            setPositiveButton("Да") { _, _ ->
                                db.taskDAO().deleteTask(task.task)
                                showJournal(idJournal)
                            }
                            setNegativeButton("Нет", null)
                            show()
                        }
                        true
                    }
                }
                menu.show()
            }
            headLine.addView(tvTasks)
        }
        //Колонка добавления задания
        val addST = creatTextView(text = "+", h = h, w = wadd, bg = gold, align = 4)
        addST.setOnClickListener{
            addStudyTask(id_journ = idJournal)
        }
        headLine.addView(addST)
        headLine.addView(creatTextView(text = "Ак. часы", h = h, w = w, bg = bg, align = 4))
        headLine.addView(creatTextView(text = "Допуск", h = h, w = w, bg = bg, align = 4))
        line.addView(headLine)
    }

    private fun drawColumnsWithClass(
        mainLine: LinearLayout,
        size_fio_column: Int,
        bg: Int = Color.WHITE,
        h: Int = 220,
        w: Int = 300,
        student: Student,
        classes: List<StudyClassWithInfo>,
        tvHours: TextView
    ) {
        classes.forEach{ cl ->
            var sam = db.studyAttendMarkDAO().getStudyAttendMarkByIdJournalAndIdStudent(idJournal, student.id!!).
            filter { st-> st.attendMark.id_study_class == cl.cl.id!!}
            val text: String = when(sam.count()){
                0 -> ""
                else -> sam[0].attendMark.mark
            }

            val tv = creatTextView(
                text = text, align = View.TEXT_ALIGNMENT_CENTER,
                w = w, bg = bg)
            tv.setOnClickListener {
                if(writeMode!!){
                    sam = db.studyAttendMarkDAO().getStudyAttendMarkByIdJournalAndIdStudent(idJournal,
                        student.id).  filter { st-> st.attendMark.id_study_class == cl.cl.id!!}
                    when(markInstrumentStudyClass){
                        0L -> {
                            if(sam.count() != 0){
                                val menu = PopupMenu(this@MainActivity, tv)
                                menu.menu.add("Удалить отметку").setOnMenuItemClickListener {
                                    val res = db.studyAttendMarkDAO().deleteStudyAttendMark(sam[0].attendMark)
                                    tv.text =""
                                    tvHours.performClick()
                                    true
                                }
                                menu.show()
                            }
                        }
                        1L -> {
                            tv.text="."
                            if(sam.count() != 0){
                                sam[0].attendMark.id_study_mark_type = markInstrumentStudyClass
                                sam[0].attendMark.mark = "."
                                db.studyAttendMarkDAO().updateStudyAttendMark(sam[0].attendMark)
                            } else {
                                db.studyAttendMarkDAO().insertStudyAttendMark(StudyAttendMark(
                                    id_study_class = cl.cl.id!!,
                                    id_student = student.id,
                                    id_study_mark_type = markInstrumentStudyClass,
                                    mark = "."
                                ))
                            }
                        }
                        2L -> {
                            val dialog = LayoutInflater.from(this).inflate(R.layout.addmark, null)
                            val etMark: EditText = dialog.findViewById(R.id.etMark)
                            val radioGroup: RadioGroup = dialog.findViewById(R.id.radioGroup)
                            val btCancl: Button = dialog.findViewById(R.id.declineb)
                            val btDone: Button = dialog.findViewById(R.id.saveb)
                            radioGroup.setOnCheckedChangeListener{ group, checkedId ->
                                val rb = dialog.findViewById<RadioButton>(checkedId)
                                etMark.setText(rb.text)
                            }
                            radioGroup.check(R.id.rb60)

                            val mBuilder = AlertDialog.Builder(this).setView(dialog)
                            val mAlertDialog = mBuilder.show()
                            btDone.setOnClickListener {
                                mAlertDialog.dismiss()
                                tv.text=etMark.text
                                if(sam.count() != 0){
                                    sam[0].attendMark.id_study_mark_type = markInstrumentStudyClass
                                    sam[0].attendMark.mark = etMark.text.toString()
                                    db.studyAttendMarkDAO().updateStudyAttendMark(sam[0].attendMark)
                                } else {
                                    db.studyAttendMarkDAO().insertStudyAttendMark(StudyAttendMark(
                                        id_study_class = cl.cl.id!!,
                                        id_student = student.id,
                                        id_study_mark_type = markInstrumentStudyClass,
                                        mark = etMark.text.toString()
                                    ))
                                }
                            }
                            btCancl.setOnClickListener {
                                mAlertDialog.dismiss()
                            }
                        }
                        3L -> {
                            val symbol = when(symbolPass){
                                0 -> "Н"
                                else -> "Б"
                            }
                            tv.text=symbol
                            if(sam.count() != 0){
                                sam[0].attendMark.id_study_mark_type = markInstrumentStudyClass
                                sam[0].attendMark.mark = symbol
                                db.studyAttendMarkDAO().updateStudyAttendMark(sam[0].attendMark)
                            } else {
                                db.studyAttendMarkDAO().insertStudyAttendMark(StudyAttendMark(
                                    id_study_class = cl.cl.id!!,
                                    id_student = student.id,
                                    id_study_mark_type = markInstrumentStudyClass,
                                    mark = symbol
                                ))
                            }
                        }

                    }
                    tvHours.performClick()
                }
                true
            }
            tv.setOnLongClickListener {
                val menu = PopupMenu(this@MainActivity, tv)
                menu.menu.add(if(!writeMode!!) "Режим записи - Вкл" else "Режим записи - Выкл").setOnMenuItemClickListener {
                    writeMode = !writeMode!!
                    true
                }
                val types = db.studyAttendMarkDAO().getAttendMarkType()
                types.forEach{ type ->
                    val isCur = type.id!!.toLong()==markInstrumentStudyClass
                    menu.menu.add(if(!isCur) "Отметка: ${type.title}" else "Сбросить отметку").setOnMenuItemClickListener {
                        if(isCur){
                            markInstrumentStudyClass =  0
                        } else {
                            markInstrumentStudyClass = type.id
                            if(type.id==3L){
                                val submenu = PopupMenu(this@MainActivity, tv)
                                submenu.menu.add("Пропуск - Н").setOnMenuItemClickListener {
                                    symbolPass = 0
                                    true
                                }
                                submenu.menu.add("Пропуск - Б").setOnMenuItemClickListener {
                                    symbolPass = 1
                                    true
                                }
                                submenu.show()
                            }
                        }
                        true
                    }
                }
                menu.show()
                true
            }
            mainLine.addView(tv)
        }
    }

    private fun drawColumnsWithWork(
        mainLine: LinearLayout,
        size_fio_column: Int,
        bg: Int = Color.WHITE,
        w: Int,
        h: Int,
        student: Student,
        tasks: List<StudyTaskWithInfo>,
        tvDopusk: TextView
    ) {
        tasks.forEachIndexed{ i, task ->
            var stm = db.studyTaskMarkDAO().getStudyTaskMarksByIdJournalAndIdStudent(idJournal, student.id!!).
            filter { st-> st.taskMark.id_task == task.task.id!!}
            val text: String = when(stm.count()){
                0 -> ""
                else -> stm[0].taskMark.mark
            }

            val tv = creatTextView(text = text, align = View.TEXT_ALIGNMENT_CENTER, w = w, bg = bg)
            tv.setOnClickListener {
                if(writeMode!!){
                    stm = db.studyTaskMarkDAO().getStudyTaskMarksByIdJournalAndIdStudent(idJournal,
                        student.id).
                    filter { st-> st.taskMark.id_task == task.task.id!!}
                    when(markInstrumentStudyTask){
                        0L -> {
                            if(stm.count() != 0){
                                val menu = PopupMenu(this@MainActivity, tv)
                                menu.menu.add("Удалить отметку").setOnMenuItemClickListener {
                                    db.studyTaskMarkDAO().deleteStudyTaskMark(stm[0].taskMark)
                                    tv.text =""
                                    tvDopusk.performClick()
                                    true
                                }
                                menu.show()
                            }
                        }
                        1L -> {
                            tv.text="✓"
                            if(stm.count() != 0){
                                stm[0].taskMark.id_task_mark_type = markInstrumentStudyTask
                                stm[0].taskMark.mark = "✓"
                                db.studyTaskMarkDAO().updateStudyTaskMark(stm[0].taskMark)
                            } else {
                                db.studyTaskMarkDAO().insertStudyTaskMark(StudyTaskMark(
                                    id_task = task.task.id!!,
                                    id_student = student.id,
                                    id_task_mark_type = markInstrumentStudyTask,
                                    mark = "✓"
                                ))
                            }
                        }
                        2L -> {
                            val dialog = LayoutInflater.from(this).inflate(R.layout.addmark, null)
                            val etMark: EditText = dialog.findViewById(R.id.etMark)
                            val radioGroup: RadioGroup = dialog.findViewById(R.id.radioGroup)
                            val btCancl: Button = dialog.findViewById(R.id.declineb)
                            val btDone: Button = dialog.findViewById(R.id.saveb)
                            radioGroup.setOnCheckedChangeListener{ group, checkedId ->
                                val rb = dialog.findViewById<RadioButton>(checkedId)
                                etMark.setText(rb.text)
                            }
                            radioGroup.check(R.id.rb60)

                            val mBuilder = AlertDialog.Builder(this).setView(dialog)
                            val mAlertDialog = mBuilder.show()
                            btDone.setOnClickListener {
                                mAlertDialog.dismiss()
                                tv.text=etMark.text
                                if(stm.count() != 0){
                                    stm[0].taskMark.id_task_mark_type = markInstrumentStudyTask
                                    stm[0].taskMark.mark = etMark.text.toString()
                                    db.studyTaskMarkDAO().updateStudyTaskMark(stm[0].taskMark)
                                } else {
                                    db.studyTaskMarkDAO().insertStudyTaskMark(StudyTaskMark(
                                        id_task = task.task.id!!,
                                        id_student = student.id,
                                        id_task_mark_type = markInstrumentStudyTask,
                                        mark = etMark.text.toString()
                                    ))
                                }
                            }
                            btCancl.setOnClickListener {
                                mAlertDialog.dismiss()
                            }

                        }

                    }
                    tvDopusk.performClick()
                }
                true
            }
            tv.setOnLongClickListener {
                val menu = PopupMenu(this@MainActivity, tv)
                menu.menu.add(if(!writeMode!!) "Режим записи - Вкл" else "Режим записи - Выкл").setOnMenuItemClickListener {
                    writeMode = !writeMode!!
                    true
                }
                val types = db.studyTaskMarkDAO().getTaskMarkType()
                types.forEach{ type ->
                    val isCur = type.id!!.toLong()==markInstrumentStudyTask
                    menu.menu.add(if(!isCur) "Отметка: ${type.title}" else "Сбросить отметку").setOnMenuItemClickListener {
                        markInstrumentStudyTask = if(isCur) 0 else type.id
                        true
                    }
                }

                menu.show()
                true
            }
            mainLine.addView(tv)
        }
    }

    private fun drawGroupLine(
        line: LinearLayout,
        group: StudyGroup,
        bg: Int = Color.WHITE,
        hHead: Int,
    ) {
        val groupLine = LinearLayout(this)
        groupLine.orientation = LinearLayout.HORIZONTAL
        val tvGroup = creatTextView(text =""+group.abbr, bg = bg,h=hHead, ts=25.0)
        tvGroup.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        groupLine.addView(tvGroup)
        line.addView(groupLine)
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

                val students = mutableListOf<Student>()

                btDone.setOnClickListener {
                    mAlertDialog.dismiss()
                    val name = n.text.toString()
                    val surname = sur.text.toString()
                    val middlename = mid.text.toString()
                    val groupe = grou.text.toString()
                    when (idJournal) {
                        0L -> showToast(message = "Не выбран журнал!")
                        else -> {
                            if (name.trim().isNotEmpty() && surname.trim().isNotEmpty() &&
                                middlename.trim().isNotEmpty() && groupe.trim().isNotEmpty()
                            ) {
                                val stgroup = StudyGroup(title = groupe, abbr = groupe)
                                val idgroup = when (val idExist = db.studyGroupDAO().isStudyGroupExist(stgroup.abbr)) {
                                    0L -> {
                                        db.studyGroupDAO().insertStudyGroup(stgroup)
                                    }
                                    else -> {
                                        idExist
                                    }
                                }

                                val d = db.flowStudentsDAO().insertFlowStudents(FlowStudents(id_journal = idJournal, id_group = idgroup))
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
                                showToast(message = "Имя: $name, фамилия: $surname, отчество: $middlename, группа: $groupe")
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
                        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
                    }
                    mAlertDialog.dismiss()
                }
                xmlbtn.setOnClickListener {
                    readXml(mDialogView)
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
                        val sub = Subject(title = fullword, abbr = word)
                        when(db.subjectDAO().isSubjectExist(fullword, word)){
                            0L -> {
                                if(db.subjectDAO().insertSubject(sub)!=0L)
                                    showToast(message = "Сокращенное: $word, полное название: $fullword")
                            }
                            else -> {
                                showToast(message="Такая дисциплина существует")
                            }
                        }
                    }
                    else showToast(message = "Заполните все поля!")
                    spinnerSubject?.setSelection(3)
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
                        when (db.taskDAO().isTaskTypeExist("Лабораторная работа", "ЛР")) {
                            0L -> DBJournalHelper.addDAO()
                            else -> showToast(message = "Есть!")
                        }
                        val type = TaskType(title = fullword, abbr = word)
                        val idgroup = when (val idExist = db.taskDAO().isTaskTypeExist(fullword, word)) {
                            0L -> db.taskDAO().insertTaskType(type)
                            else -> {
                                idExist
                            }
                        }
                        showToast(message = "Сокращенное: $word, полное название: $fullword")
                    }
                    else showToast(message = "Заполните все поля!")
                    spinnerClassType?.setSelection(2)
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
            4-> {
                val tv: TextView = mDialogView.findViewById(R.id.noteJournal)
                val sp: Spinner = mDialogView.findViewById(R.id.spinnerSubject)
                val save: Button = mDialogView.findViewById(R.id.dialogSave)
                val btCancl: Button = mDialogView.findViewById(R.id.dialogCancel)
                val gr: MutableList<String> = mutableListOf()
                val grr = db.subjectDAO().getSubject()
                val mapId = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += grr[index].abbr
                    mapId[index] = grr[index].id!!.toLong()
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gr)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp.adapter = adapter
                sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                        spinnerClassType?.setSelection(0)
                        tv.text = ""
                        tv.hint = ""+grr[position].abbr+": + текст..."
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>?) {
                        // TODO Auto-generated method stub
                    }
                }
                save.setOnClickListener {
                    mAlertDialog.dismiss()
                    val note = tv.text.toString()
                    val selectedItem = sp.selectedItemPosition
                    val abbr = grr[selectedItem].abbr
                    val id_subject = mapId[selectedItem]!!
                    if(note.trim().isNotEmpty() && sp.count != 0) {
                        val j = Journal(id_subject = id_subject, note = "$abbr: $note")
                        val insId =db.journalDAO().insertJournal(j)
                        showToast(message = "Добавлен журнал по: $abbr с id: $insId")
                    }
                    else showToast(message = "Заполните все поля!")
                    spinnerSubject?.setSelection(3)
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
        }

    }

    private fun clearLayout(){
        val lay2 = findViewById<LinearLayout>(R.id.layout2).removeAllViews()
        findViewById<LinearLayout>(R.id.layout3).removeAllViews()
        setInvisibleView()
    }
    private fun setInvisibleView(){
        val lay2 = findViewById<LinearLayout>(R.id.layout2)
        lay2.visibility = View.INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK && data != null) {
            val filePath = data.data
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

    private fun readXml(view: View){
        if (path.toString().endsWith("xls")) {
            ParseXML(context = this).readFromExcelFile(db, path!!)
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

    private fun hasPermissionsWithStorage(): Boolean{
        return if (ActivityCompat.checkSelfPermission(this,  Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
            false
        } else {
            true
        }
    }
    
    private fun addStudyClass(id_journ: Long){
        when (id_journ) {
            0L -> showToast(message = "Не выбран журнал!")
            else -> {
                val dialog = LayoutInflater.from(this).inflate(R.layout.addstudyclass, null)
                val title: TextView = dialog.findViewById(R.id.etClassTitle)
                val sp: Spinner = dialog.findViewById(R.id.spinnerclasstype)
                val btCancl: Button = dialog.findViewById(R.id.declineb)
                val btDone: Button = dialog.findViewById(R.id.saveb)
                val mBuilder = AlertDialog.Builder(this).setView(dialog)
                val gr: MutableList<String> = mutableListOf()
                val grr = db.studyClassDAO().getStudyClassType()
                val typesID = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += entity.abbr
                    typesID[index] = entity.id!!.toLong()
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gr)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp.adapter = adapter
                val calendar = Calendar.getInstance()
                val calendarView: CalendarView = dialog.findViewById(R.id.calendarView)
                calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                    calendar.set(year,month,dayOfMonth)
                    calendarView.date = calendar.timeInMillis
                    val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
                    val da=dateFormatter.format(calendar.time)
                }

                val mAlertDialog = mBuilder.show()
                btDone.setOnClickListener {
                    mAlertDialog.dismiss()
                    val theme = title.text.toString()
                    val id_study_class_type = typesID[spinnerGroup?.selectedItemPosition]!!
                    val d = db.studyClassDAO().insertStudyClass(StudyClass(data = Date(calendar.time.time),theme=theme, id_study_class_type = id_study_class_type, id_journal = id_journ))
                    showJournal(idJournal)
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }



            }
        }
    }

    private fun addStudyTask(id_journ: Long){
        when (id_journ) {
            0L -> showToast(message = "Не выбран журнал!")
            else -> {
                val dialog = LayoutInflater.from(this).inflate(R.layout.addstudytask, null)
                val tvTitle: TextView = dialog.findViewById(R.id.tvTaskCount)
                val countTask: EditText = dialog.findViewById(R.id.etTaskCount)
                val spinnerTask: Spinner = dialog.findViewById(R.id.spinnertasktype)
                val radioGroup: RadioGroup = dialog.findViewById(R.id.radioGroup)
                val btCancl: Button = dialog.findViewById(R.id.declineb)
                val btDone: Button = dialog.findViewById(R.id.saveb)

                val gr: MutableList<String> = mutableListOf()
                val grr = db.taskDAO().getTaskType()
                val typesID = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += entity.abbr
                    typesID[index] = entity.id!!.toLong()
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gr)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTask.adapter = adapter

                radioGroup.setOnCheckedChangeListener{
                    group, checkedId ->
                    when(checkedId){
                        R.id.rb60 ->{
                            tvTitle.text="Одно задание"
                            countTask.visibility = View.INVISIBLE
                        }
                        R.id.rbManyTask ->{
                            tvTitle.text="Множество заданий"
                            countTask.visibility = View.VISIBLE
                        }
                    }
                }

                radioGroup.check(R.id.rbOneTask)

                val mBuilder = AlertDialog.Builder(this).setView(dialog)
                val mAlertDialog = mBuilder.show()
                btDone.setOnClickListener {
                    mAlertDialog.dismiss()
                    val typeTaskId = typesID[spinnerTask.selectedItemPosition]!!
                    val lastNumThisTypeTask = db.taskDAO().getLastNumberTaskByTypeInJournal(id_journal= id_journ, id_task_type = typeTaskId)
                    when(radioGroup.checkedRadioButtonId){
                        R.id.rbOneTask ->{
                            db.taskDAO().insertTask(Task(
                                id_journal = id_journ,
                                id_task_type = typeTaskId,
                                id_cur_num_task = lastNumThisTypeTask+1))
                        }
                        R.id.rbManyTask ->{
                            val cnt = when(countTask.text.toString()){
                                "" -> 1
                                else -> countTask.text.toString().toInt()
                            }
                            for (i in 1..cnt){
                                db.taskDAO().insertTask(Task(
                                    id_journal = id_journ,
                                    id_task_type = typeTaskId,
                                    id_cur_num_task = lastNumThisTypeTask+i))
                            }

                        }
                    }
                    showJournal(idJournal)
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
        }
    }

    fun addGroupInJournal(id_journ :Long){
        when (id_journ) {
            0L -> showToast(message = "Не выбран журнал!")
            else -> {
                val builder = AlertDialog.Builder(this)
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.list, null)
                val ll = mDialogView.findViewById<ListView>(R.id.list)
                val gr: MutableList<String> = mutableListOf()
                val grr = db.studyGroupDAO().getStudyGroup()
                val mapId = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += entity.abbr
                    mapId[index] = entity.id!!.toLong()
                }
                val arrayAdapter: ArrayAdapter<*>
                arrayAdapter = ArrayAdapter(this, R.layout.item, gr)
                ll.adapter = arrayAdapter
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                ll.onItemClickListener = AdapterView.OnItemClickListener { _, _, p2, _ ->
                        val pp = mapId[p2]!!
                        val journal = db.journalDAO().getJournal(id_journ)
                        val insId = db.flowStudentsDAO().insertFlowStudents(FlowStudents(id_journal = id_journ, id_group = pp))
                        showToast(message = "В журнал $id_journ: \"${journal.note}\" добавлена группа ${grr[p2].abbr}!")
                        mAlertDialog.dismiss()
                    }

            }
        }
    }
    private fun showToast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }
}