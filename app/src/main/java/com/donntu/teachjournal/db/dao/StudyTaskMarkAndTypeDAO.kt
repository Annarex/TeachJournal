package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Journal
import com.donntu.teachjournal.db.entity.StudyTaskMark
import com.donntu.teachjournal.db.entity.TaskMarkType
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable

@Dao
interface StudyTaskMarkAndTypeDAO {
    @Query("SELECT * FROM StudyTaskMark")
    fun getStudyTaskMark(): List<StudyTaskMark>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudyTaskMark(amp: StudyTaskMark):Long

    @Delete
    fun deleteStudyTaskMark(amp: StudyTaskMark)

    @Update
    fun updateStudyTaskMark(amp: StudyTaskMark)
    @Query("SELECT * FROM TaskMarkType")
    fun getTaskMarkType(): List<TaskMarkType>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTaskMarkType(amp: TaskMarkType):Long

    @Delete
    fun deleteTaskMarkType(amp: TaskMarkType)

    @Update
    fun updateTaskMarkType(amp: TaskMarkType)
}