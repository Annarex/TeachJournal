package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Journal
import com.donntu.teachjournal.db.entity.StudyTaskMark
import com.donntu.teachjournal.db.entity.TaskMarkType
import com.donntu.teachjournal.db.entity_with_relate.StudyAttendMarkWithInfo
import com.donntu.teachjournal.db.entity_with_relate.StudyTaskMarkWithInfo
import com.donntu.teachjournal.db.entity_with_relate.StudyTaskWithInfo
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable

@Dao
interface StudyTaskMarkAndTypeDAO {
    @Query("SELECT * FROM StudyTaskMark")
    fun getStudyTaskMarks(): List<StudyTaskMarkWithInfo>

    @Query("SELECT *, stm.id as id FROM StudyTaskMark as stm, Task as t where t.id = stm.id_task and t.id_journal=:id_journal and stm.id_student =:id_student")

    fun getStudyTaskMarksByIdJournalAndIdStudent(id_journal:Long, id_student: Long): List<StudyTaskMarkWithInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudyTaskMark(amp: StudyTaskMark):Long

    @Delete
    fun deleteStudyTaskMark(amp: StudyTaskMark)

    @Query("Delete FROM StudyTaskMark where id = :id")
    fun deleteStudyTaskMark(id: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
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