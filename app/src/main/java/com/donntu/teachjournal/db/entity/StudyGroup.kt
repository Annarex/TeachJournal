package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StudyGroup")
class StudyGroup(
    @PrimaryKey(autoGenerate = true)
    val id_group: Int,
    var title : String,
    var abbr : String

)