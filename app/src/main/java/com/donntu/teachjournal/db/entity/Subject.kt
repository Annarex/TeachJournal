package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Subject")
class Subject  (
    @PrimaryKey(autoGenerate = true)
    val id_subject: Int,
    var title: String,
    var abbr: String
)