package com.donntu.teachjournal.db.entity

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "Journal", foreignKeys = [ForeignKey(entity = Subject::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("id_subject"),
    onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["id_subject"])]
)
class Journal (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val id_subject: Long,
    val note: String
): Serializable