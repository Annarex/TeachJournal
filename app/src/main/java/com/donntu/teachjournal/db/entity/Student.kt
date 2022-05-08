package com.donntu.teachjournal.db.entity

import androidx.room.*
import java.io.Serializable

@Entity(tableName = "Student", foreignKeys = arrayOf(
    ForeignKey(entity = StudyGroup::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_group"),
        onDelete = ForeignKey.CASCADE)),
    indices = [Index(value = ["id_group"]),Index(value = ["family","name","patronymic","id_group"], unique = true)]
)
class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val family: String,
    val name: String,
    val patronymic: String,
    val id_group: Long
): Serializable{
    override fun toString(): String {
        return family+ " "+ name[0].toUpperCase()+". "+ patronymic[0].toUpperCase()+". "
    }
}