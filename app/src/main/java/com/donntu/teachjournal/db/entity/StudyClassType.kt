package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StudyClassType")
class StudyClassType(
    @PrimaryKey(autoGenerate = true)
    val id_study_class_type: Int,
    var title: String,
    var abbr: String
)