package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "StudyClass")
class StudyClass(
    @PrimaryKey(autoGenerate = true)
    val id_study_class: Int,
    val data: Date,
    val theme: String,
    val id_study_class_type: Int,
    val id_journal: Int
)