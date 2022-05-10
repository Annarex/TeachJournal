package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Subject")
class Subject  (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String,
    var abbr: String
): Serializable