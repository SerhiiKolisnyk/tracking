/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kolisnyk.serhii.kn313.option1.tracking.data.source

import com.google.common.base.Preconditions.checkNotNull

import com.kolisnyk.serhii.kn313.option1.tracking.data.Task

import java.util.ArrayList
import java.util.LinkedHashMap


class TasksRepository
private constructor(tasksRemoteDataSource: TasksDataSource,
                    tasksLocalDataSource: TasksDataSource) : TasksDataSource {

    private val mTasksRemoteDataSource: TasksDataSource
    private val mTasksLocalDataSource: TasksDataSource
    var mCachedTasks: MutableMap<String, Task>? = null
    internal var mCacheIsDirty = false

    init {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource)
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource)
    }

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        checkNotNull<TasksDataSource.LoadTasksCallback>(callback)

        // Respond immediately with cache if available and not dirty
        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(ArrayList(mCachedTasks!!.values))
            return
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callback)
        } else {
            // Query the local storage if available. If not, query the network.
            mTasksLocalDataSource.getTasks(object : TasksDataSource.LoadTasksCallback {
                override fun onTasksLoaded(tasks: List<Task>) {
                    refreshCache(tasks)
                    callback.onTasksLoaded(ArrayList(mCachedTasks!!.values))
                }

                override fun onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun saveTask(task: Task) {
        checkNotNull(task)
        mTasksRemoteDataSource.saveTask(task)
        mTasksLocalDataSource.saveTask(task)

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = LinkedHashMap<String, Task>()
        }
        mCachedTasks!!.put(task.id, task)
    }

    override fun completeTask(task: Task) {
        checkNotNull(task)
        mTasksRemoteDataSource.completeTask(task)
        mTasksLocalDataSource.completeTask(task)

        val completedTask = Task(task.title, task.description, task.id, true)

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = LinkedHashMap<String, Task>()
        }
        mCachedTasks!!.put(task.id, completedTask)
    }

    override fun completeTask(taskId: String) {
        checkNotNull(taskId)
        completeTask(getTaskWithId(taskId)!!)
    }

    override fun activateTask(task: Task) {
        checkNotNull(task)
        mTasksRemoteDataSource.activateTask(task)
        mTasksLocalDataSource.activateTask(task)

        val activeTask = Task(task.title, task.description, task.id)

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = LinkedHashMap<String, Task>()
        }
        mCachedTasks!!.put(task.id, activeTask)
    }

    override fun activateTask(taskId: String) {
        checkNotNull(taskId)
        activateTask(getTaskWithId(taskId)!!)
    }

    override fun clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks()
        mTasksLocalDataSource.clearCompletedTasks()

        // Do in memory cache update to keep the app UI up to date
        if (mCachedTasks == null) {
            mCachedTasks = LinkedHashMap<String, Task>()
        }
        val it = mCachedTasks!!.entries.iterator()
        while (it.hasNext()) {
            val entry = it.next()
            if (entry.value.isCompleted) {
                it.remove()
            }
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        checkNotNull(taskId)
        checkNotNull<TasksDataSource.GetTaskCallback>(callback)

        val cachedTask = getTaskWithId(taskId)

        // Respond immediately with cache if available
        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask)
            return
        }

        // Load from server/persisted if needed.

        // Is the task in the local data source? If not, query the network.
        mTasksLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedTasks == null) {
                    mCachedTasks = LinkedHashMap<String, Task>()
                }
                mCachedTasks!!.put(task.id, task)
                callback.onTaskLoaded(task)
            }

            override fun onDataNotAvailable() {
                mTasksRemoteDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
                    override fun onTaskLoaded(task: Task) {
                        // Do in memory cache update to keep the app UI up to date
                        if (mCachedTasks == null) {
                            mCachedTasks = LinkedHashMap<String, Task>()
                        }
                        mCachedTasks!!.put(task.id, task)
                        callback.onTaskLoaded(task)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun refreshTasks() {
        mCacheIsDirty = true
    }

    override fun deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks()
        mTasksLocalDataSource.deleteAllTasks()

        if (mCachedTasks == null) {
            mCachedTasks = LinkedHashMap<String, Task>()
        }
        mCachedTasks!!.clear()
    }

    override fun deleteTask(taskId: String) {
        mTasksRemoteDataSource.deleteTask(checkNotNull(taskId))
        mTasksLocalDataSource.deleteTask(checkNotNull(taskId))

        mCachedTasks!!.remove(taskId)
    }

    private fun getTasksFromRemoteDataSource(callback: TasksDataSource.LoadTasksCallback) {
        mTasksRemoteDataSource.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                refreshCache(tasks)
                refreshLocalDataSource(tasks)
                callback.onTasksLoaded(ArrayList(mCachedTasks!!.values))
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshCache(tasks: List<Task>) {
        if (mCachedTasks == null) {
            mCachedTasks = LinkedHashMap<String, Task>()
        }
        mCachedTasks!!.clear()
        for (task in tasks) {
            mCachedTasks!!.put(task.id, task)
        }
        mCacheIsDirty = false
    }

    private fun refreshLocalDataSource(tasks: List<Task>) {
        mTasksLocalDataSource.deleteAllTasks()
        for (task in tasks) {
            mTasksLocalDataSource.saveTask(task)
        }
    }

    private fun getTaskWithId(id: String): Task? {
        checkNotNull(id)
        if (mCachedTasks == null || mCachedTasks!!.isEmpty()) {
            return null
        } else {
            return mCachedTasks!![id]
        }
    }

    companion object {

        private var INSTANCE: TasksRepository? = null

        fun getInstance(tasksRemoteDataSource: TasksDataSource,
                        tasksLocalDataSource: TasksDataSource): TasksRepository {
            if (INSTANCE == null) {
                INSTANCE = TasksRepository(tasksRemoteDataSource, tasksLocalDataSource)
            }
            return INSTANCE as TasksRepository
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
