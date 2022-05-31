package com.donntu.teachjournal.db.entity_with_relate

import androidx.room.Embedded
import androidx.room.Relation
import com.donntu.teachjournal.db.entity.*

data class StudentsInGroup(
    @Embedded val group: StudyGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "id_group"
    )
    val students: List<Student>
)

data class StudyClassWithInfo(
    @Embedded val cl: StudyClass,
    @Relation(
        parentColumn = "id_study_class_type",
        entityColumn = "id"
    )
    val type: StudyClassType
)

data class StudyAttendMarkWithInfo(
    @Embedded val attendMark: StudyAttendMark,
    @Relation(
        parentColumn = "id_study_mark_type",
        entityColumn = "id"
    )
    val type: AttendMarkType
)
data class StudyTaskWithInfo(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id_task_type",
        entityColumn = "id"
    )
    val type: TaskType
)

data class StudyTaskMarkWithInfo(
    @Embedded val taskMark: StudyTaskMark,
    @Relation(
        parentColumn = "id_task_mark_type",
        entityColumn = "id"
    )
    val type: TaskMarkType
)

