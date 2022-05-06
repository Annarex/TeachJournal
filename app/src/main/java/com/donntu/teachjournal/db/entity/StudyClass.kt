package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "StudyClass")
class StudyClass{
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val data: String,
    val theme: String,
    val id_study_class_type: String,
}