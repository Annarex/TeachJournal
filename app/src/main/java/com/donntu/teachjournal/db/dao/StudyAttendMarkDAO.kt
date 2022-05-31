package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.AttendMarkType
import com.donntu.teachjournal.db.entity.Journal
import com.donntu.teachjournal.db.entity.StudyAttendMark
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable

@Dao
interface StudyAttendMarkDAO {
    @Query("SELECT * FROM StudyAttendMark")
    fun getStudyAttendMark(): List<StudyAttendMark>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudyAttendMark(amp: StudyAttendMark):Long

    @Delete
    fun deleteStudyAttendMark(amp: StudyAttendMark)

    @Update
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