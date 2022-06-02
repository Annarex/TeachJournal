package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.AttendMarkType
import com.donntu.teachjournal.db.entity.Journal
import com.donntu.teachjournal.db.entity.StudyAttendMark
import com.donntu.teachjournal.db.entity_with_relate.StudyAttendMarkWithInfo
import com.donntu.teachjournal.db.entity_with_relate.StudyClassWithInfo
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable

@Dao
interface StudyAttendMarkDAO {
    @Query("SELECT * FROM StudyAttendMark")
    fun getStudyAttendMark(): List<StudyAttendMarkWithInfo>

    @Query("SELECT *, sam.id as id FROM StudyAttendMark as sam, StudyClass as sc where sc.id = sam.id_study_class and sc.id_journal=:id_journal and sam.id_student =:id_student")
    fun getStudyAttendMarkByIdJournalAndIdStudent(id_journal:Long, id_student: Long): List<StudyAttendMarkWithInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudyAttendMark(amp: StudyAttendMark):Long

    @Delete
    fun deleteStudyAttendMark(amp: StudyAttendMark)

    @Query("Delete FROM StudyAttendMark where id = :id")
    fun deleteStudyAttendMark(id: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateStudyAttendMark(amp: StudyAttendMark)

    @Query("SELECT * FROM AttendMarkType")
    fun getAttendMarkType(): List<AttendMarkType>

    @Insert(onConflict = OnConflictStrategy.IGNORE)//В этом режиме будет оставлена старая запись и операция вставки не будет выполнена.
    fun insertAttendMarkType(amp: AttendMarkType):Long

    @Delete
    fun deleteAttendMarkType(amp: AttendMarkType)

    @Update
    fun updateAttendMarkType(amp: AttendMarkType)
}