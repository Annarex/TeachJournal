package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Task
import com.donntu.teachjournal.db.entity.TaskType

@Dao
interface TaskAndTypeDAO {
    @Query("SELECT * FROM Task")
    fun getTasks(): List<StudyTaskWithInfo>

    @Query("SELECT * FROM Task where id_journal = :id ORDER by id_task_type, id_cur_num_task")
    fun getTaskByIdJournal(id:Long): List<StudyTaskWithInfo>

    @Query("SELECT id_cur_num_task FROM Task where id_journal = :id_journal and id_task_type = :id_task_type ORDER by id_cur_num_task DESC LIMIT 1")
    fun getLastNumberTaskByTypeInJournal(id_journal:Long, id_task_type:Long): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(amp: Task):Long

    @Delete
    fun deleteTask(amp: Task)

    @Update
    fun updateTask(amp: Task)

    @Query("SELECT * FROM TaskType")
    fun getTaskType(): List<TaskType>

    @Query("SELECT id from (SELECT * from TaskType WHERE title = :titl and abbr = :abbr LIMIT 1)")
    fun isTaskTypeExist(titl:String, abbr:String): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTaskType(amp: TaskType):Long

    @Delete
    fun deleteTaskType(amp: TaskType)

    @Update
    fun updateTaskType(amp: TaskType)
}