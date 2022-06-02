package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Journal

@Dao
interface JournalDAO {
    @Query("SELECT * FROM Journal")
    fun getJournals(): List<Journal>

    @Query("SELECT * FROM Journal where id = :id")
    fun getJournal(id: Long): Journal

    @Query("SELECT * FROM Journal where id_subject = :id")
    fun getJournalBySubjectId(id:Long): List<Journal>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJournal(amp: Journal):Long

    @Delete
    fun deleteJournal(amp: Journal)

    @Update
    fun updateJournal(amp: Journal)
}