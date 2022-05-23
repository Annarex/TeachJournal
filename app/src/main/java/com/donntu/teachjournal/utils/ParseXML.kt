package com.donntu.teachjournal.utils
import android.R.attr.data
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract.Attendees.query
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.donntu.teachjournal.db.DBJournalHelper
import com.donntu.teachjournal.db.entity.FlowStudents
import com.donntu.teachjournal.db.entity.Student
import com.donntu.teachjournal.db.entity.StudyGroup
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream


class ParseXML {
    var context:Context? = null
    constructor(context: Context){
        this.context = context
    }
    fun readFromExcelFile(db: DBJournalHelper, uri: Uri, ii: Long) {
        try {
            val inputStream = context?.getContentResolver()?.openInputStream(uri)
            val xlWb = WorkbookFactory.create(inputStream)
            var index = 9
            var cn_new_records = 0
            //Получаем первую страницу:
            val xlWs = xlWb.getSheetAt(0)
            while (xlWs.getRow(index) ?: false != xlWs.getRow(index + 1) ?: false) {
                if (xlWs.getRow(index) == null) index++
                var group: String = xlWs.getRow(index)?.getCell(0).toString()
                group = group.removePrefix("Группа: ")
                val stgroup = StudyGroup(title = group, abbr = group)
                val idExist = db.studyGroupDAO().isStudyGroupExist(stgroup.abbr)
                val idgroup = when (idExist) {
                    0L -> db.studyGroupDAO().insertStudyGroup(stgroup)
                    else -> idExist
                }
                db.flowStudentsDAO().insertFlowStudents(FlowStudents(id_journal = ii, id_group = idgroup))
                if (idExist == 0L) cn_new_records++

                index += 2
                var students = mutableListOf<Student>()
                if (xlWs.getRow(index)?.getCell(1) != null) {
                    while (xlWs.getRow(index)?.getCell(1) != null) {
                        val fio = xlWs.getRow(index)?.getCell(1).toString()
                        val f = fio.split(' ')[0]
                        val n = fio.split(' ')[1]
                        val p = fio.split(' ')[2]
                        val student =
                            Student(family = f, name = n, patronymic = p, id_group = idgroup)
                        val hasStudent = db.studentDAO().isStudentExistInGroup(
                            idgroup = idgroup,
                            family = f,
                            name = n,
                            patronymic = p
                        )
                        if (hasStudent == 0L) {
                            students.add(student)
                            cn_new_records++
                        }
                        index++
                    }
                } else {
                    index + 1
                }
                val idstudents = db.studentDAO().insertStudents(students)
            }

            Toast.makeText(
                context,
                "Кол-во обновленных записей: " + cn_new_records,
                Toast.LENGTH_SHORT
            ).show()

        }
        catch (e:Error){
        Log.d("Log", e.toString())
        }
    }

    }