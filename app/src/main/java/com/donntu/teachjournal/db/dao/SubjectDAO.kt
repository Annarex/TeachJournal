package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Subject

@Dao
interface SubjectDAO {
    @Query("SELECT * FROM Subject")
    fun getSubject(): List<Subject>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSubject(amp: Subject):Long

    @Query("SELECT id from(SELECT * from Subject WHERE title LIKE :tit and abbr LIKE :name LIMIT 1)")
    fun isSubjectExist(tit: String, name:String): Long

    @Delete
    fun deleteSubject(amp: Subject)

    @Update
    fun updateSubject(amp: Subject)
}