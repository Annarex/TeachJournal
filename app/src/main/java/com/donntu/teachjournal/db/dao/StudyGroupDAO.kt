package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.StudyGroup

@Dao
interface StudyGroupDAO {
    @Transaction
    @Query("SELECT * FROM StudyGroup")
    fun getAllGroupsWithStudents(): List<StudentsInGroup>

    @Query("SELECT * FROM StudyGroup")
    fun getStudyGroup(): List<StudyGroup>

    @Query("SELECT id_group as id, abbr, title FROM StudyGroup as st, FlowStudents as fl WHERE st.id=fl.id_group AND fl.id_journal = :x ORDER BY st.id")

    fun getStudyGroupinSub(x: Long): List<StudyGroup>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudyGroup(amp: StudyGroup):Long

    @Query("SELECT id from(SELECT * from StudyGroup WHERE abbr LIKE :abbr LIMIT 1)")
    fun isStudyGroupExist(abbr: String): Long

    @Delete
    fun deleteStudyGroup(amp: StudyGroup)

    @Update
    fun updateStudyGroup(amp: StudyGroup)
}