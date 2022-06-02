package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Subject")
class Subject  (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var title: String,
    var abbr: String
): Serializable