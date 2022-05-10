package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Journal
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface JournalDAO {
    @Query("SELECT * FROM Journal")
    fun getJournal(): List<Journal>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertJournal(amp: Journal):Long

    @Delete
    fun deleteJournal(amp: Journal)

    @Update
    fun updateJournal(amp: Journal)
}