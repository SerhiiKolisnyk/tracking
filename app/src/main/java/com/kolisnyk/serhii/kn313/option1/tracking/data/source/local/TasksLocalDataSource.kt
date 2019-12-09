package com.kolisnyk.serhii.kn313.option1.tracking.data.source.local

import android.arch.persistence.room.Room
import android.content.Context
import com.kolisnyk.serhii.kn313.option1.tracking.data.Task
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksDataSource
import com.google.common.base.Preconditions.checkNotNull


class TasksLocalDataSource// Prevent direct instantiation.
private constructor(context: Context) : TasksDataSource {

    private val mTasksDao: TasksDao

    init {
        checkNotNull(context)
        val db = Room.databaseBuilder(context, TasksDb::class.java, "tasks.db").allowMainThreadQueries().build()
        mTasksDao = db.tasksDao()
    }

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        val tasks = mTasksDao.getAllTasks()

        if (tasks.isEmpty()) {
            // This will be called if the table is new or just empty.
            callback.onDataNotAvailable()
        } else {
            callback.onTasksLoaded(tasks)
        }

    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {

        val task: Task? = mTasksDao.getTask(taskId)

        task?: callback.onDataNotAvailable()

        callback.onTaskLoaded(task!!)
    }

    override fun saveTask(task: Task) {
        checkNotNull(task)
        mTasksDao.saveTask(task)
    }

    override fun completeTask(task: Task) {
        task.isCompleted = true;
        mTasksDao.updateTask(task)
    }

    override fun completeTask(taskId: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun activateTask(task: Task) {

    }

    override fun activateTask(taskId: String) {
        // Not required for the local data source because the {@link TasksRepository} handles
        // converting from a {@code taskId} to a {@link task} using its cached data.
    }

    override fun clearCompletedTasks() {
        val tasks = mTasksDao.getCompletedTasks()
        mTasksDao.deleteTasks(tasks)
    }

    override fun refreshTasks() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    override fun deleteAllTasks() {
        mTasksDao.deleteAllTasks()
    }

    override fun deleteTask(taskId: String) {
        mTasksDao.deleteTask(taskId)
    }

    companion object {

        private var INSTANCE: TasksLocalDataSource? = null

        fun getInstance(context: Context): TasksLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = TasksLocalDataSource(context)
            }
            return INSTANCE as TasksLocalDataSource
        }
    }
}
