package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "FlowStudents", foreignKeys = arrayOf(
    ForeignKey(entity = Journal::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_journal"),
        onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = StudyGroup::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_journal"),
        onDelete = ForeignKey.CASCADE))
)
class FlowStudents (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val id_journal: Int,
    val id_group: Int
): Serializable