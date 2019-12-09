package com.kolisnyk.serhii.kn313.option1.tracking.data.source.local

import android.arch.persistence.room.*
import com.kolisnyk.serhii.kn313.option1.tracking.data.Task

@Dao
interface TasksDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTask(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: String): Task

    @Query("SELECT * FROM tasks")
    fun getCompletedTasks(): List<Task>

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTasks(tasks: List<Task>)

    @Query("DELETE FROM tasks WHERE id = :id")
    fun deleteTask(id: String)

    @Query("DELETE FROM tasks")
    fun deleteAllTasks()
}