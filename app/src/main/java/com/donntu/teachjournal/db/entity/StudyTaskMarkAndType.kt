package com.donntu.teachjournal.db.entity

import androidx.room.*
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
        onDelete = ForeignKey.CASCADE)),
    indices = [Index(value = ["id_task"]),Index(value = ["id_student"]),Index(value = ["id_task_mark_type"]),Index(value = ["id_task","id_student"], unique = true)]
)
class StudyTaskMark (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val id_task:Long,
    val id_student: Long,
    var id_task_mark_type: Long,
    var mark: String
): Serializable

@Entity(tableName = "TaskMarkType")
class TaskMarkType (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String
): Serializable