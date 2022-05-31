package com.donntu.teachjournal.db.entity

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "StudyAttendMark", foreignKeys = arrayOf(
    ForeignKey(entity = StudyClass::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_study_class"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Student::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_student"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = AttendMarkType::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_study_mark_type"),
        onDelete = ForeignKey.CASCADE)),
    indices = [Index(value = ["id_study_class"]),Index(value = ["id_student"]),Index(value = ["id_study_mark_type"])]
)

class StudyAttendMark (
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var id_study_class: Long,
    var id_student: Long,
    var id_study_mark_type: Long,
    var mark: String
): Serializable

@Entity
class AttendMarkType (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String
):Serializable