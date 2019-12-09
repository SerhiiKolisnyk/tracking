package com.kolisnyk.serhii.kn313.option1.tracking.tasks

/**
 * Defines the navigation actions that can be called from a list item in the task list.
 */
interface TaskItemNavigator {

    fun openTaskDetails(taskId: String)
}
