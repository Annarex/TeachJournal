package com.donntu.teachjournal

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.setPadding
import com.donntu.teachjournal.db.DBJournalHelper
import com.donntu.teachjournal.db.entity.*
import com.donntu.teachjournal.db.entity_with_relate.StudyAttendMarkWithInfo
import com.donntu.teachjournal.db.entity_with_relate.StudyClassWithInfo
import com.donntu.teachjournal.db.entity_with_relate.StudyTaskMarkWithInfo
import com.donntu.teachjournal.db.entity_with_relate.StudyTaskWithInfo
import com.donntu.teachjournal.db.utils.ExporterImporterDB
import com.donntu.teachjournal.utils.ParseXML
import java.sql.Date
import java.text.DateFormat
import java.util.*


var select: String? =null

class MainActivity : AppCompatActivity()
{
    var path: Uri? = null
    var languages = arrayOf("Группа","Добавить", "Показать все")
    var list_of_items = arrayOf("Дисциплина", "Добавить дисциплину", "Создать журнал", "Показать все")
    var list_of_items2 = arrayOf("Вид занятия", "Добавить", "Показать все")
    var id_journal: Long = 0L
    var markInstrumentStudyClass = 0L
    var symbolPass = 0
    var markInstrumentStudyTask = 0L
    var spinner:Spinner? =null
    var spinner2:Spinner? =null
    var spinner3:Spinner? =null
    var writeMode:Boolean? = true


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
                val intent = Intent().setAction(Intent.ACTION_GET_CONTENT).setType("*/*")
                //startActivityForResult(Intent.createChooser(intent, "Выбор пути экспорта"), 666)
                true
            }
            R.id.itemImportDB ->{
                ExporterImporterDB().importDB(db, this, db_name = "TeachJournal.db")
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
                        writeMode = false
                        markInstrumentStudyClass = 0L
                        markInstrumentStudyTask = 0L
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
                                            var journal = db.journalDAO().getJournalBySubjectId(s.id!!)
                                            when(journal.count()){
                                                0 -> showToast(message = "Журналы по ${s.abbr} еще не созданы!")
                                                else ->{
                                                    var arrayAdapter: ArrayAdapter<*>
                                                    val mDialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.listofgroup, null)
                                                    var listView = mDialogView.findViewById<ListView>(R.id.list)

                                                    var gr: MutableList<String> = mutableListOf()
                                                    var mapId = mutableMapOf<Int, Long>()
                                                    journal.forEachIndexed{ index, entity->
                                                        gr += journal[index].note
                                                        mapId[index] = journal[index].id!!.toLong()
                                                    }
                                                    arrayAdapter = ArrayAdapter(this@MainActivity, R.layout.item, gr)
                                                    listView.adapter = arrayAdapter
                                                    listView.onItemClickListener = object : AdapterView.OnItemClickListener {
                                                        override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                                            id_journal = mapId[p2]!!
                                                            val menu = PopupMenu(this@MainActivity, p1)
                                                            menu.menu.apply {
                                                                add("Открыть журнал").setOnMenuItemClickListener {
                                                                    spinner?.setSelection(2)
                                                                    true
                                                                }
                                                                add("Добавить в журнал группу").setOnMenuItemClickListener {
                                                                    addGroupInJournal(id_journal)
                                                                    true
                                                                }

                                                                add("Удалить журнал").setOnMenuItemClickListener {
                                                                    val builder = AlertDialog.Builder(this@MainActivity)
                                                                    with(builder)
                                                                    {
                                                                        setTitle("Удаление")
                                                                        setMessage("Вы уверены, что хотите удалить?")
                                                                        setPositiveButton("Да") { dialog, id ->
                                                                            db.journalDAO().deleteJournal(journal[p2])
                                                                            spinner2?.setSelection(3)
                                                                        }
                                                                        setNegativeButton("Нет", null)
                                                                        show()
                                                                    }
                                                                    true
                                                                }
                                                            }
                                                            menu.show()
                                                        }
                                                    };
                                                    lay.addView(creatTextView(text ="Журналы по ${s.abbr}",w=600,bg=Color.WHITE, align = View.TEXT_ALIGNMENT_CENTER))
                                                    lay.addView(mDialogView)
                                                }
                                            }
                                        }
                                    })
                                    if(i==0)
                                        button_dynamic.performClick()
                                    ll.addView(button_dynamic)
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
        var view = TextView(this)  as TextView
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
    val GOLD: Int = Color.rgb(255, 215, 0)

    fun showTable(idJournal: Long) {
        var wHead = 300
        var hHead = 220
        var wMain = 300
        var hMain = 100
        var wadd = 100
        var studyGroupsFlow = db.studyGroupDAO().getStudyGroupinSub(idJournal)
        var studentsFlow = db.studentDAO().getStudentGroupByJournal(idJournal)
        val max_size: Int = (studentsFlow.maxByOrNull{it.toString().length}).toString().length*24

        val recView = LinearLayout(this@MainActivity)
        recView.orientation = LinearLayout.VERTICAL
        recView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        recView.setPadding(15,10,15,20)
        recView.setBackgroundColor(Color.LTGRAY)

        for(gr in studyGroupsFlow){
            drawGroupLine(line=recView, group = gr, hHead = 150, bg=GOLD)
            val classes = db.studyClassDAO().getStudyClassByIdJournal(id_journal)
            val tasks = db.taskDAO().getTaskByIdJournal(id_journal)
            drawHeaderLine(line=recView, size_fio_column=max_size, bg=Color.rgb(244, 164, 96), h=hHead, w=wHead, wadd = wadd, classes=classes, tasks=tasks)
            var ni = 0
            studentsFlow.filter { st -> st.id_group == gr.id }.forEachIndexed { i, item->
                ni++
                val mainLine = LinearLayout(this)
                mainLine.orientation = LinearLayout.HORIZONTAL
                var lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(3,3,3,3)
                mainLine.layoutParams = lp
                var lineColor = when(i%2){
                    0-> Color.rgb(255, 245, 238)
                    else -> Color.rgb(245, 222, 179)
                }
                mainLine.addView(creatTextView(text =ni.toString(), bg = lineColor, align = View.TEXT_ALIGNMENT_CENTER))
                mainLine.addView(creatTextView(text =item.id.toString(), bg = lineColor, align = View.TEXT_ALIGNMENT_CENTER))//id студента
                mainLine.addView(creatTextView(text =item.toString(), w =max_size, bg = lineColor))//ФИО

                val tvHours = creatTextView(text = "", align = View.TEXT_ALIGNMENT_CENTER,w = wMain, bg = lineColor)
                tvHours.setOnClickListener {
                    val classes = db.studyClassDAO().getStudyClassByIdJournal(id_journal).count()
                    val studentNotPass = db.studyAttendMarkDAO().getStudyAttendMarkByIdJournalAndIdStudent(id_journal, item.id!!).
                    filter { st->st.attendMark.id_study_mark_type!! != 3L}
                    tvHours.text = "${studentNotPass.count()}/${classes}"
                }
                tvHours.performClick()
                //Столбцы занятий
                drawColumnsWithClass(mainLine=mainLine, size_fio_column=max_size, bg = lineColor, w=wMain, h=hMain ,student=item, classes=classes, tvHours=tvHours)
                mainLine.addView(creatTextView(text = "|", w = wadd, bg=GOLD, align =4))//Пустой столбец

                val tvDopusk = creatTextView(text = "Допуск", align = View.TEXT_ALIGNMENT_CENTER,w = wMain, bg = lineColor)
                tvDopusk.setOnClickListener {
                    val tasks = db.taskDAO().getTaskByIdJournal(id_journal).count()
                    val taskDone = db.studyTaskMarkDAO().getStudyTaskMarksByIdJournalAndIdStudent(id_journal, item.id!!).count()
                    tvDopusk.text = if(tasks == taskDone) "Да" else "Нет"
                }
                tvDopusk.performClick()
                //Столбец с работами
                drawColumnsWithWork(mainLine=mainLine, size_fio_column=max_size, bg = lineColor, w=wMain, h=hMain,student=item,tasks=tasks,tvDopusk=tvDopusk)
                mainLine.addView(creatTextView(text = "|", w = wadd, bg=GOLD, align =4))//Пустой столбец
                mainLine.addView(tvHours)
                mainLine.addView(tvDopusk)
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

    private fun drawHeaderLine(
        line: LinearLayout,
        size_fio_column: Int,
        bg: Int = Color.WHITE,
        h: Int,
        w: Int,
        wadd: Int,
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
                            setPositiveButton("Да") { dialog, id ->
                                db.studyClassDAO().deleteStudyClass(cl.cl)
                                spinner?.setSelection(2)
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
        val addSC = creatTextView(text = "+", h =h, w = wadd, bg=GOLD, align =4)!!
        addSC.setOnClickListener{view->
            addStudyClass(id_journ = id_journal)
        }
        headLine.addView(addSC)
        tasks.forEachIndexed{ i, task ->
            val type = task.type.abbr
            val num_task = task.task.id_cur_num_task
            val count_task = db.taskDAO().getLastNumberTaskByTypeInJournal(id_journal,task.task.id_task_type)
            val text =when(num_task){
                1L->if(count_task==1L) "" else "1"
                else -> num_task.toString()
            }
            val tvTasks = creatTextView(
                text = ""+type+" "+text, bg = bg,
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
                            setPositiveButton("Да") { dialog, id ->
                                db.taskDAO().deleteTask(task.task)
                                spinner?.setSelection(2)
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
        val addST = creatTextView(text = "+", h =h, w = wadd, bg=GOLD, align =4)!!
        addST.setOnClickListener{view->
            addStudyTask(id_journ = id_journal)
        }
        headLine.addView(addST)
        headLine.addView(creatTextView(text = "Ак. часы", h =h, w = w, bg=bg, align =4)!!)
        headLine.addView(creatTextView(text = "Допуск", h =h, w = w, bg=bg, align =4)!!)
        line.addView(headLine)
    }

    private fun drawColumnsWithClass(
        mainLine: LinearLayout,
        size_fio_column: Int,
        bg: Int = Color.WHITE,
        w: Int,
        h: Int,
        student: Student,
        classes: List<StudyClassWithInfo>,
        tvHours: TextView
    ) {
        classes.forEachIndexed{ i, cl ->
            var sam = db.studyAttendMarkDAO().getStudyAttendMarkByIdJournalAndIdStudent(id_journal, student.id!!).
            filter { st->st.attendMark.id_study_class!! == cl.cl.id!!}
            var text: String = when(sam.count()){
                0 -> ""
                else -> sam[0].attendMark.mark
            }

            val tv = creatTextView(
                text = text, align = View.TEXT_ALIGNMENT_CENTER,
                w = w, bg = bg)
            tv.setOnClickListener {
                if(writeMode!!){
                    sam = db.studyAttendMarkDAO().getStudyAttendMarkByIdJournalAndIdStudent(id_journal, student.id!!).  filter { st->st.attendMark.id_study_class!! == cl.cl.id!!}
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
                                var sm = sam[0].attendMark
                                sm.id_study_mark_type = markInstrumentStudyClass
                                sm.mark = "."
                                db.studyAttendMarkDAO().updateStudyAttendMark(sm)
                            } else {
                                db.studyAttendMarkDAO().insertStudyAttendMark(StudyAttendMark(
                                    id_study_class = cl.cl.id!!,
                                    id_student = student.id!!,
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
                                var rb = dialog.findViewById<RadioButton>(checkedId)
                                etMark.setText(rb.text)
                            }
                            radioGroup.check(R.id.rb60)

                            val mBuilder = AlertDialog.Builder(this).setView(dialog)
                            val mAlertDialog = mBuilder.show()
                            btDone.setOnClickListener {
                                mAlertDialog.dismiss()
                                tv.text=etMark.text
                                if(sam.count() != 0){
                                    var sm = sam[0].attendMark
                                    sm.id_study_mark_type = markInstrumentStudyClass
                                    sm.mark = etMark.text.toString()
                                    db.studyAttendMarkDAO().updateStudyAttendMark(sm)
                                } else {
                                    db.studyAttendMarkDAO().insertStudyAttendMark(StudyAttendMark(
                                        id_study_class = cl.cl.id!!,
                                        id_student = student.id!!,
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
                            var symbol = when(symbolPass){
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
                                    id_student = student.id!!,
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
                var types = db.studyAttendMarkDAO().getAttendMarkType()
                types.forEachIndexed{ i, type ->
                    val isCur = type.id!!.toLong()==markInstrumentStudyClass
                    menu.menu.add(if(!isCur) "Отметка: ${type.title}" else "Сбросить отметку").setOnMenuItemClickListener {
                        if(isCur){
                            markInstrumentStudyClass =  0
                        } else {
                            markInstrumentStudyClass =  type.id!!
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
            var stm = db.studyTaskMarkDAO().getStudyTaskMarksByIdJournalAndIdStudent(id_journal, student.id!!).
            filter { st->st.taskMark.id_task!! == task.task.id!!}
            var text: String = when(stm.count()){
                0 -> ""
                else -> stm[0].taskMark.mark
            }

            val tv = creatTextView(text = text, align = View.TEXT_ALIGNMENT_CENTER, w = w, bg = bg)
            tv.setOnClickListener {
                if(writeMode!!){
                    stm = db.studyTaskMarkDAO().getStudyTaskMarksByIdJournalAndIdStudent(id_journal, student.id!!).
                    filter { st->st.taskMark.id_task!! == task.task.id!!}
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
                                    id_student = student.id!!,
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
                                var rb = dialog.findViewById<RadioButton>(checkedId)
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
                                        id_student = student.id!!,
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
                var types = db.studyTaskMarkDAO().getTaskMarkType()
                types.forEachIndexed{ i, type ->
                    val isCur = type.id!!.toLong()==markInstrumentStudyTask
                    menu.menu.add(if(!isCur) "Отметка: ${type.title}" else "Сбросить отметку").setOnMenuItemClickListener {
                        markInstrumentStudyTask = if(isCur) 0 else type.id!!
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

    fun clearLayout(){
        var lay2 = findViewById<LinearLayout>(R.id.layout2).removeAllViews()
        findViewById<LinearLayout>(R.id.layout3).removeAllViews()
        setInvisibleView()
    }
    fun setInvisibleView(){
        var lay2 = findViewById<LinearLayout>(R.id.layout2)
        lay2.visibility = View.INVISIBLE
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

    override fun onStop() {
        super.onStop()
        db.close()
    }
    override fun onDestroy() {
        super.onDestroy()
        db.close()
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
    
    fun addStudyClass(id_journ: Long){
        when (id_journ) {
            0L -> showToast(message = "Не выбран журнал!")
            else -> {
                val dialog = LayoutInflater.from(this).inflate(R.layout.addstudyclass, null)
                val title: TextView = dialog.findViewById(R.id.etClassTitle)
                val sp: Spinner = dialog.findViewById(R.id.spinnerclasstype)
                val btCancl: Button = dialog.findViewById(R.id.declineb)
                val btDone: Button = dialog.findViewById(R.id.saveb)
                val mBuilder = AlertDialog.Builder(this).setView(dialog)
                var gr: MutableList<String> = mutableListOf()
                var grr = db.studyClassDAO().getStudyClassType()
                var typesID = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += grr[index].abbr
                    typesID[index] = grr[index].id!!.toLong()
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gr)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp.adapter = adapter
                val calendar = Calendar.getInstance()
                var calendarView: CalendarView = dialog.findViewById(R.id.calendarView)
                calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                    calendar.set(year,month,dayOfMonth)
                    calendarView.date = calendar.timeInMillis
                    val dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM)
                    val da=dateFormatter.format(calendar.time)
                }

                val mAlertDialog = mBuilder.show()
                btDone.setOnClickListener {
                    mAlertDialog.dismiss()
                    val theme = title.text.toString()
                    val id_study_class_type = typesID[spinner?.selectedItemPosition]!!
                    var d = db.studyClassDAO().insertStudyClass(StudyClass(data = Date(calendar.time.time),theme=theme, id_study_class_type = id_study_class_type, id_journal = id_journ))
                    spinner?.setSelection(2)
                }
                btCancl.setOnClickListener {
                    mAlertDialog.dismiss()
                }



            }
        }
    }

    fun addStudyTask(id_journ: Long){
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

                var gr: MutableList<String> = mutableListOf()
                var grr = db.taskDAO().getTaskType()
                var typesID = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += grr[index].abbr
                    typesID[index] = grr[index].id!!.toLong()
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gr)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTask.adapter = adapter

                radioGroup.setOnCheckedChangeListener( RadioGroup.OnCheckedChangeListener{
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
                } )

                radioGroup.check(R.id.rbOneTask)

                val mBuilder = AlertDialog.Builder(this).setView(dialog)
                val mAlertDialog = mBuilder.show()
                btDone.setOnClickListener {
                    mAlertDialog.dismiss()
                    val typeTaskId = typesID[spinnerTask?.selectedItemPosition]!!
                    val lastNumThisTypeTask = db.taskDAO().getLastNumberTaskByTypeInJournal(id_journal= id_journ, id_task_type = typeTaskId)
                    val id = radioGroup.getCheckedRadioButtonId()
                    when(id){
                        R.id.rbOneTask ->{
                            db.taskDAO().insertTask(Task(
                                id_journal = id_journ,
                                id_task_type = typeTaskId,
                                id_cur_num_task = lastNumThisTypeTask+1))
                        }
                        R.id.rbManyTask ->{
                            var cnt = when(countTask.text.toString()){
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
                    val theme = countTask.text.toString()
                    spinner?.setSelection(2)
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
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.listofgroup, null)
                var ll = mDialogView.findViewById<ListView>(R.id.list)
                var gr: MutableList<String> = mutableListOf()
                var grr = db.studyGroupDAO().getStudyGroup()
                var mapId = mutableMapOf<Int, Long>()
                grr.forEachIndexed{index, entity->
                    gr += grr[index].abbr
                    mapId[index] = grr[index].id!!.toLong()
                }
                var arrayAdapter: ArrayAdapter<*>
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
                        val journal = db.journalDAO().getJournal(id_journ)
                        val insId = db.flowStudentsDAO().insertFlowStudents(FlowStudents(id_journal = id_journ, id_group = pp))
                        showToast(message = "В журнал ${id_journ}: \"${journal.note}\" добавлена группа ${grr[p2].abbr}!")
                        mAlertDialog.dismiss()
                    }
                }

            }
        }
    }
    private fun showToast(context: Context = applicationContext, message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, message, duration).show()
    }
}