package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Task
import com.donntu.teachjournal.db.entity.TaskType
import com.donntu.teachjournal.db.entity_with_relate.StudyTaskWithInfo
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable

@Dao
interface TaskAndTypeDAO {
    @Query("SELECT * FROM Task")
    fun getTasks(): List<StudyTaskWithInfo>

    @Query("SELECT * FROM Task where id_journal = :id ORDER by id_task_type, id_cur_num_task")
    fun getTaskByIdJournal(id:Long): List<StudyTaskWithInfo>

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