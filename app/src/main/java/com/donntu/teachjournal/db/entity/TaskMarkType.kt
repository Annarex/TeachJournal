package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "TaskMarkType")
class TaskMarkType (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String
): Serializable