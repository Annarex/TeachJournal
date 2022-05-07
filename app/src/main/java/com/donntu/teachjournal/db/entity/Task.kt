package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Task")
class Task (
    @PrimaryKey(autoGenerate = true)
    val id_task: Int,
    val id_journal: Int,
    val id_cur_num_task: Int,
    val id_task_type: Int
)