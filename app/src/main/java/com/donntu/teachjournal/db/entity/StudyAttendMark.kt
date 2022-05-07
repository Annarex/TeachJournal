package com.donntu.teachjournal.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StudyAttendMark")
class StudyAttendMark (
    @PrimaryKey(autoGenerate = true)
    var id_study_mark: Int,
    var id_study_class: Int,
    var id_student: Int,
    var id_study_mark_type: Int,
    var mark: String
)