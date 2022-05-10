package com.donntu.teachjournal.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.donntu.teachjournal.db.dao.*
import com.donntu.teachjournal.db.entity.*
import com.donntu.teachjournal.db.utils.Converters


@Database(entities = arrayOf(
    AttendMarkType::class, FlowStudents::class, Journal::class, Student::class,
    StudyAttendMark::class, StudyClass::class, StudyClassType::class, StudyGroup::class,
    StudyTaskMark::class, Subject::class, Task::class, TaskMarkType::class, TaskType::class),
    version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DBJournalHelper: RoomDatabase() {
    abstract fun flowStudentsDAO(): FlowStudentsDAO
    abstract fun journalDAO(): JournalDAO
    abstract fun studentDAO(): StudentDAO
    abstract fun studyAttendMarkDAO(): StudyAttendMarkDAO
    abstract fun studyClassDAO(): StudyClassDAO
    abstract fun studyGroupDAO(): StudyGroupDAO
    abstract fun studyTaskMarkDAO(): StudyTaskMarkAndTypeDAO
    abstract fun subjectDAO(): SubjectDAO
    abstract fun taskDAO(): TaskAndTypeDAO

    companion object {
        @Volatile
        private var INSTANCE: DBJournalHelper? = null

        fun getDatabase(context: Context): DBJournalHelper {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            return INSTANCE
                ?: synchronized(this) {
                    val instance = Room.databaseBuilder(context.applicationContext,
                        DBJournalHelper::class.java,"TeachJournal.db")
                        .allowMainThreadQueries()
                        .build()
                    INSTANCE = instance
                    if(INSTANCE!!.taskDAO().getTaskType().count()==0){
                        addDAO()
                    }

                    return instance
                }
        }

        fun addDAO(){
            var dao1 = INSTANCE?.studyAttendMarkDAO()
            dao1?.insertAttendMarkType(AttendMarkType(title = "Знак"))
            dao1?.insertAttendMarkType(AttendMarkType(title = "Оценка"))
            dao1?.insertAttendMarkType(AttendMarkType(title = "Пропуск"))

            var dao2 = INSTANCE?.studyClassDAO()
            dao2?.insertStudyClassType(StudyClassType(title = "Лекция",abbr="лек"))
            dao2?.insertStudyClassType(StudyClassType(title = "Практика",abbr="пр"))
            dao2?.insertStudyClassType(StudyClassType(title = "Лабораторная",abbr="лаб"))
            dao2?.insertStudyClassType(StudyClassType(title = "Семинар",abbr="сем"))
            dao2?.insertStudyClassType(StudyClassType(title = "Конференция",abbr="конф"))

            val dao3 = INSTANCE?.studyTaskMarkDAO()
            dao3?.insertTaskMarkType(TaskMarkType(title = "Знак"))
            dao3?.insertTaskMarkType(TaskMarkType(title = "Оценка"))

            val dao4 = INSTANCE?.taskDAO()
            dao4?.insertTaskType(TaskType(title = "Лабораторная работа",abbr="ЛР"))
            dao4?.insertTaskType(TaskType(title = "Практическая работа",abbr="ПР"))
            dao4?.insertTaskType(TaskType(title = "Курсовая работа",abbr="КР"))
            dao4?.insertTaskType(TaskType(title = "Курсовой проект",abbr="КП"))
            dao4?.insertTaskType(TaskType(title = "Индивидуальное задание",abbr="ИЗ"))
            dao4?.insertTaskType(TaskType(title = "Самостоятельная работа",abbr="СР"))
        }
    }
}