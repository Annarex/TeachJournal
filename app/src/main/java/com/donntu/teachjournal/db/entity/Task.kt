package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Task", foreignKeys = arrayOf(
    ForeignKey(entity = Journal::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_journal"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = TaskType::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_task_type"),
        onDelete = ForeignKey.CASCADE))
)
class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val id_journal: Int,
    val id_cur_num_task: Int,
    val id_task_type: Int
): Serializable