package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Student")
class Student(
    @PrimaryKey(autoGenerate = true)
    val id_student: Int,
    val family: String,
    val name: String,
    val patronymic: String,
    val id_group: Int
)