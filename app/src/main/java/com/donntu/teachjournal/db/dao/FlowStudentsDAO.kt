package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.FlowStudents
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface FlowStudentsDAO {
    @Query("SELECT * FROM FlowStudents")
    fun getFlowStudents(): List<FlowStudents>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFlowStudents(amp: FlowStudents):Long

    @Delete
    fun deleteFlowStudents(amp: FlowStudents)

    @Update
    fun updateFlowStudents(amp: FlowStudents)
}