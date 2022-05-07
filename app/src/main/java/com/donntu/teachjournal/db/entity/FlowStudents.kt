package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FlowStudents")
class FlowStudents (
    @PrimaryKey(autoGenerate = true)
    val id_record: Int,
    val id_journal: Int,
    val id_group: Int
)