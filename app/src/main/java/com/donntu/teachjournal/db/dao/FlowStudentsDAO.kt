package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.FlowStudents

@Dao
interface FlowStudentsDAO {
    @Query("SELECT * FROM FlowStudents")
    fun getFlowStudents(): List<FlowStudents>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFlowStudents(amp: FlowStudents):Long

    @Query("SELECT * FROM FlowStudents WHERE id_group = :idd")
    fun getGroupsById(idd: Long): List<FlowStudents>

    @Query("SELECT * FROM FlowStudents as f, StudyGroup as s WHERE f.id_journal = :idd AND s.id = f.id_group")
    fun getSubjectById(idd: Long): List<FlowStudents>

    @Delete
    fun deleteFlowStudents(amp: FlowStudents)

    @Update
    fun updateFlowStudents(amp: FlowStudents)
}