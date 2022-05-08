package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Journal", foreignKeys = arrayOf(
    ForeignKey(entity = Subject::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_subject"),
        onDelete = ForeignKey.CASCADE))
)
class Journal (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val id_subject: Int,
    val note: String
): Serializable