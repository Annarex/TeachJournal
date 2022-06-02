package com.donntu.teachjournal.db.entity

import androidx.room.*
import java.io.Serializable


@Entity(tableName = "Task", foreignKeys = [ForeignKey(entity = Journal::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("id_journal"),
    onDelete = ForeignKey.CASCADE), ForeignKey(entity = TaskType::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("id_task_type"),
    onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["id_journal"]),Index(value = ["id_cur_num_task"]),Index(value = ["id_task_type"])]
)
class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val id_journal: Long,
    val id_cur_num_task: Long,
    val id_task_type: Long
): Serializable
@Entity(tableName = "TaskType")
class TaskType (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String,
    val abbr: String
): Serializable
