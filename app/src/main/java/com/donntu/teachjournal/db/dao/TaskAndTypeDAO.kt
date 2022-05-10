package com.donntu.teachjournal.db.dao

import androidx.room.*
import com.donntu.teachjournal.db.entity.Task
import com.donntu.teachjournal.db.entity.TaskType
import io.reactivex.Completable
import io.reactivex.Maybe
import java.io.Serializable

@Dao
interface TaskAndTypeDAO {
    @Query("SELECT * FROM Task")
    fun getTask(): List<Task>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(amp: Task):Long

    @Delete
    fun deleteTask(amp: Task)

    @Update
    fun updateTask(amp: Task)

    @Query("SELECT * FROM TaskType")
    fun getTaskType(): List<TaskType>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTaskType(amp: TaskType):Long

    @Delete
    fun deleteTaskType(amp: TaskType)

    @Update
    fun updateTaskType(amp: TaskType)
}