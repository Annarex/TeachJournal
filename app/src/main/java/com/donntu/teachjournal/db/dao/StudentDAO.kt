package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Student
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable

@Dao
interface StudentDAO {
    @Query("SELECT * FROM Student")
    fun getStudent(): List<Student>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudent(amp: Student): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudents(student: List<Student>): List<Long>

    @Query("SELECT id from (SELECT * from Student WHERE id_group = :idgroup and family = :family and name = :name and patronymic = :patronymic LIMIT 1)")
    fun isStudentExistInGroup(idgroup: Long, family:String, name:String, patronymic:String): Long

    @Delete
    fun deleteStudent(amp: Student)

    @Update
    fun updateStudent(amp: Student)
}