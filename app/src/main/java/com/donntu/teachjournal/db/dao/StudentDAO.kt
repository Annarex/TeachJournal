package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Student

@Dao
interface StudentDAO {
    @Query("SELECT * FROM Student ORDER BY id_group")
    fun getStudent(): List<Student>
    @Query("SELECT * FROM Student  where id_group=:idgroup ORDER BY id_group")
    fun getStudentsByIdGroup(idgroup: Long): List<Student>

    @Query("SELECT sst.id, sst.family, sst.name, sst.patronymic, sst.id_group FROM Student as sst, StudyGroup as gg, FlowStudents as fl WHERE sst.id_group=gg.id AND gg.id=fl.id_group AND fl.id_journal = :x ORDER BY sst.id_group, fl.id_group")
    fun getStudentGroupByJournal(x: Long): List<Student>

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