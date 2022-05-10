package com.donntu.teachjournal.db.entity_with_relate

import androidx.room.Embedded
import androidx.room.Relation
import com.donntu.teachjournal.db.entity.Student
import com.donntu.teachjournal.db.entity.StudyGroup

data class StudentsInGroup(
    @Embedded val group: StudyGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_group"
    )
    val students: List<Student>
)
data class Journal(
    @Embedded val group: StudyGroup,
    val students: List<Student>
)