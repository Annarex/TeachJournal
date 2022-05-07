package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AttendMarkType")
class AttendMarkType (
    @PrimaryKey(autoGenerate = true)
    val id_study_mark_type: Int,
    val title: String
)