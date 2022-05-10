package com.donntu.teachjournal.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Student", foreignKeys = arrayOf(
    ForeignKey(entity = StudyGroup::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_group"),
        onDelete = ForeignKey.CASCADE))
)
class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val family: String,
    val name: String,
    val patronymic: String,
    val id_group: Int
): Serializable