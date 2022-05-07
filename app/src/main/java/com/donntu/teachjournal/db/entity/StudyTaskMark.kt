package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StudyTaskMark")
class StudyTaskMark (
    @PrimaryKey(autoGenerate = true)
    val id_task_mark: Int,
    val id_task:Int,
    val id_student: Int,
    val id_task_mark_type: Int,
    val mark: String
)