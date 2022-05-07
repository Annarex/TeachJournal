package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskType")
class TaskType (
    @PrimaryKey(autoGenerate = true)
    val id_task_type: Int,
    val title: String,
    val abbr: String
)