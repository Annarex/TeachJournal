package com.donntu.teachjournal.db.entity

import androidx.room.*
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
        onDelete = ForeignKey.CASCADE)),
    indices = [Index(value = ["id_study_class_type"]),Index(value = ["id_journal"])]
)

class StudyClass(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val data: Date? = null,
    val theme: String? = null,
    val id_study_class_type: Long,
    val id_journal: Long
): Serializable

@Entity(tableName = "StudyClassType")
class StudyClassType(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var title: String,
    var abbr: String
): Serializable