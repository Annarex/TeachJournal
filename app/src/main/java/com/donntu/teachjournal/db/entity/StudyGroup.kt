package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "StudyGroup", indices = [Index(value = ["title","abbr"], unique = true)])
class StudyGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var title : String,
    var abbr : String
): Serializable