package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Journal
import com.donntu.teachjournal.db.entity.StudyClass
import com.donntu.teachjournal.db.entity.StudyClassType
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable
import java.sql.Date

@Dao
interface StudyClassDAO {
    @Query("SELECT * FROM StudyClass")
    fun getStudyClass(): List<StudyClass>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudyClass(amp: StudyClass):Long

    @Delete
    fun deleteStudyClass(amp: StudyClass)

    @Update
    fun updateStudyClass(amp: StudyClass)

    @Query("SELECT * FROM StudyClassType")
    fun getStudyClassType(): List<StudyClassType>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudyClassType(amp: StudyClassType):Long

    @Delete
    fun deleteStudyClassType(amp: StudyClassType)

    @Update
    fun updateStudyClassType(amp: StudyClassType)
}