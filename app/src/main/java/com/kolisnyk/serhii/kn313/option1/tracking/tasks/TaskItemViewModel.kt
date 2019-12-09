package com.kolisnyk.serhii.kn313.option1.tracking.tasks

import android.content.Context

import com.kolisnyk.serhii.kn313.option1.tracking.TaskViewModel
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksRepository

import java.lang.ref.WeakReference



class TaskItemViewModel(context: Context, tasksRepository: TasksRepository) : TaskViewModel(context, tasksRepository) {

    // This navigator is s wrapped in a WeakReference to avoid leaks because it has references to an
    // activity. There's no straightforward way to clear it for each item in a list adapter.
    private var mNavigator: WeakReference<TaskItemNavigator>? = null

    fun setNavigator(navigator: TaskItemNavigator) {
        mNavigator = WeakReference(navigator)
    }

    /**
     * Called by the Data Binding library when the row is clicked.
     */
    fun taskClicked() {
        val taskId = taskId ?: // Click happened before task was loaded, no-op.
                return
        if (mNavigator != null && mNavigator?.get() != null) {
            mNavigator?.get()?.openTaskDetails(taskId)
        }
    }
}
