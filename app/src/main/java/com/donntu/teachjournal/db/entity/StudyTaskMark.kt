package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "StudyTaskMark", foreignKeys = arrayOf(
    ForeignKey(entity = Task::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_task"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Student::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_student"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = TaskMarkType::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_task_mark_type"),
        onDelete = ForeignKey.CASCADE))
)
class StudyTaskMark (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val id_task:Int,
    val id_student: Int,
    val id_task_mark_type: Int,
    val mark: String
): Serializable