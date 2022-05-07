package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Journal")
class Journal (
    @PrimaryKey(autoGenerate = true)
    val id_journal: Int,
    val id_subject: Int,
    val note: String
)