package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskMarkType")
class TaskMarkType (
    @PrimaryKey(autoGenerate = true)
    val id_task_mark_type: Int,
    val title: String
)