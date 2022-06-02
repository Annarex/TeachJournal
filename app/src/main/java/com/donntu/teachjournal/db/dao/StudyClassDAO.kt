package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.StudyClass
import com.donntu.teachjournal.db.entity.StudyClassType

@Dao
interface StudyClassDAO {
    @Query("SELECT * FROM StudyClass")
    fun getStudyClass(): List<StudyClassWithInfo>

    @Query("SELECT * FROM StudyClass where id_journal = :id GROUP by data , id_study_class_type")
    fun getStudyClassByIdJournal(id:Long): List<StudyClassWithInfo>

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