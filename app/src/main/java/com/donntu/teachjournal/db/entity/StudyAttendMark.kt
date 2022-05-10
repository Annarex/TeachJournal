package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
        onDelete = ForeignKey.CASCADE))
)

class StudyAttendMark (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var id_study_class: Int,
    var id_student: Int,
    var id_study_mark_type: Int,
    var mark: String
): Serializable