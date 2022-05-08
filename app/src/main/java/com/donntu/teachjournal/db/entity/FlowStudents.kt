package com.donntu.teachjournal.db.entity

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "FlowStudents", foreignKeys = arrayOf(
    ForeignKey(entity = Journal::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_journal"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = StudyGroup::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_journal"),
        onDelete = ForeignKey.CASCADE)),
    indices = [Index(value = ["id_group"]),Index(value = ["id_journal"])]
)
class FlowStudents (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val id_journal: Long,
    val id_group: Long
): Serializable