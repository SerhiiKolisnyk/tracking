package com.kolisnyk.serhii.kn313.option1.tracking.taskdetail

import android.content.Context

import com.kolisnyk.serhii.kn313.option1.tracking.TaskViewModel
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksRepository
import com.kolisnyk.serhii.kn313.option1.tracking.tasks.TasksFragment

class TaskDetailViewModel(context: Context, tasksRepository: TasksRepository) : TaskViewModel(context, tasksRepository) {

    private var mTaskDetailNavigator: TaskDetailNavigator? = null

    fun setNavigator(taskDetailNavigator: TaskDetailNavigator) {
        mTaskDetailNavigator = taskDetailNavigator
    }

    fun onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mTaskDetailNavigator = null
    }

    /**
     * Can be called by the Data Binding Library or the delete menu item.
     */
    override fun deleteTask() {
        super.deleteTask()
        if (mTaskDetailNavigator != null) {
            mTaskDetailNavigator!!.onTaskDeleted()
        }
    }

    fun startEditTask() {
        if (mTaskDetailNavigator != null) {
            mTaskDetailNavigator!!.onStartEditTask()
        }
    }
}
