package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.sql.Date

@Entity(tableName = "StudyClass", foreignKeys = arrayOf(
    ForeignKey(entity = StudyClassType::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_study_class_type"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Journal::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_journal"),
        onDelete = ForeignKey.CASCADE))
)
class StudyClass(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val data: Date,
    val theme: String,
    val id_study_class_type: Int,
    val id_journal: Int
): Serializable