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

package com.kolisnyk.serhii.kn313.option1.tracking.addedittask

import android.content.Context
import android.databinding.ObservableBoolean
import android.databinding.ObservableField

import com.kolisnyk.serhii.kn313.option1.tracking.R
import com.kolisnyk.serhii.kn313.option1.tracking.data.Task
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksDataSource
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksRepository

class AddEditTaskViewModel
internal constructor(context: Context, private val mTasksRepository: TasksRepository)
    : TasksDataSource.GetTaskCallback {

    val title = ObservableField<String>()

    val description = ObservableField<String>()

    val dataLoading = ObservableBoolean(false)

    val snackbarText = ObservableField<String>()

    private val mContext: Context  // To avoid leaks, this must be an Application Context.

    private var mTaskId: String? = null

    private var isNewTask: Boolean = false

    private var mIsDataLoaded = false

    private var mAddEditTaskNavigator: AddEditTaskNavigator? = null

    init {
        mContext = context.applicationContext // Force use of Application Context.
    }

    fun onActivityCreated(navigator: AddEditTaskNavigator) {
        mAddEditTaskNavigator = navigator
    }

    fun onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mAddEditTaskNavigator = null
    }

    fun start(taskId: String?) {
        if (dataLoading.get()) {
            // Already loading, ignore.
            return
        }
        mTaskId = taskId
        if (taskId == null) {
            // No need to populate, it's a new task
            isNewTask = true
            return
        }
        if (mIsDataLoaded) {
            // No need to populate, already have data.
            return
        }
        isNewTask = false
        dataLoading.set(true)
        mTasksRepository.getTask(taskId, this)
    }

    override fun onTaskLoaded(task: Task) {
        title.set(task.title)
        description.set(task.description)
        dataLoading.set(false)
        mIsDataLoaded = true

        // Note that there's no need to notify that the values changed because we're using
        // ObservableFields.
    }

    override fun onDataNotAvailable() {
        dataLoading.set(false)
    }

    // Called when clicking on fab.
    fun saveTask() {
        if (isNewTask) {
            createTask(title.get(), description.get())
        } else {
            updateTask(title.get(), description.get())
        }
    }

    fun getSnackbarTextString(): String {
        return snackbarText.get()
    }

    private fun createTask(title: String, description: String) {
        val newTask = Task(title, description)
        if (newTask.isEmpty) {
            snackbarText.set(mContext.getString(R.string.empty_task_message))
        } else {
            mTasksRepository.saveTask(newTask)
            navigateOnTaskSaved()
        }
    }

    private fun updateTask(title: String, description: String) {
        if (isNewTask) {
            throw RuntimeException("updateTask() was called but task is new.")
        }
        mTasksRepository.saveTask(Task(title, description, mTaskId))
        navigateOnTaskSaved() // After an edit, go back to the list.
    }

    private fun navigateOnTaskSaved() {
        if (mAddEditTaskNavigator != null) {
            mAddEditTaskNavigator!!.onTaskSaved()
        }
    }
}
