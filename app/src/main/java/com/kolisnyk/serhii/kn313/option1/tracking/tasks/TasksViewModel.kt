package com.kolisnyk.serhii.kn313.option1.tracking.tasks

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat

import com.kolisnyk.serhii.kn313.option1.tracking.BR
import com.kolisnyk.serhii.kn313.option1.tracking.R
import com.kolisnyk.serhii.kn313.option1.tracking.addedittask.AddEditTaskActivity
import com.kolisnyk.serhii.kn313.option1.tracking.data.Task
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksDataSource
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksRepository
import com.kolisnyk.serhii.kn313.option1.tracking.taskdetail.TaskDetailActivity
import com.kolisnyk.serhii.kn313.option1.tracking.util.EspressoIdlingResource

import java.util.ArrayList

class TasksViewModel(
        private val mTasksRepository: TasksRepository,
        context: Context) : BaseObservable() {

    // These observable fields will update Views automatically
    val items: ObservableList<Task> = ObservableArrayList()

    val dataLoading = ObservableBoolean(false)

    val currentFilteringLabel = ObservableField<String>()

    val noTasksLabel = ObservableField<String>()

    val noTaskIconRes = ObservableField<Drawable>()

    val tasksAddViewVisible = ObservableBoolean()

    val snackbarText = ObservableField<String>()

    private var mCurrentFiltering = TasksFilterType.ALL_TASKS

    private val mIsDataLoadingError = ObservableBoolean(false)

    private val mContext: Context // To avoid leaks, this must be an Application Context.

    private var mNavigator: TasksNavigator? = null

    init {
        mContext = context.applicationContext // Force use of Application Context.

        // Set initial state
        setFiltering(TasksFilterType.ALL_TASKS)
    }

    fun setNavigator(navigator: TasksNavigator) {
        mNavigator = navigator
    }

    fun onActivityDestroyed() {
        // Clear references to avoid potential memory leaks.
        mNavigator = null
    }

    fun start() {
        loadTasks(false)
    }

    val isEmpty: Boolean
        @Bindable
        get() = items.isEmpty()

    fun loadTasks(forceUpdate: Boolean) {
        loadTasks(forceUpdate, true)
    }

    /**
     * Sets the current task filtering type.

     * @param requestType Can be [TasksFilterType.ALL_TASKS],
     * *                    [TasksFilterType.COMPLETED_TASKS], or
     * *                    [TasksFilterType.ACTIVE_TASKS]
     */
    fun setFiltering(requestType: TasksFilterType) {
        mCurrentFiltering = requestType

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                currentFilteringLabel.set(mContext.getString(R.string.label_all))
                noTasksLabel.set(mContext.resources.getString(R.string.no_tasks_all))
                noTaskIconRes.set(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_assignment_turned_in_24dp))
                tasksAddViewVisible.set(true)
            }
            TasksFilterType.ACTIVE_TASKS -> {
                currentFilteringLabel.set(mContext.getString(R.string.label_active))
                noTasksLabel.set(mContext.resources.getString(R.string.no_tasks_active))
                noTaskIconRes.set(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_check_circle_24dp))
                tasksAddViewVisible.set(false)
            }
            TasksFilterType.COMPLETED_TASKS -> {
                currentFilteringLabel.set(mContext.getString(R.string.label_completed))
                noTasksLabel.set(mContext.resources.getString(R.string.no_tasks_completed))
                noTaskIconRes.set(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_verified_user_24dp))
                tasksAddViewVisible.set(false)
            }
        }
    }

    fun clearCompletedTasks() {
        mTasksRepository.clearCompletedTasks()
        snackbarText.set(mContext.getString(R.string.completed_tasks_cleared))
        loadTasks(false, false)
    }

    fun getSnackbarTextString(): String {
        return snackbarText.get()
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    fun addNewTask() {
        if (mNavigator != null) {
            mNavigator!!.addNewTask()
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        if (AddEditTaskActivity.REQUEST_CODE == requestCode) {
            when (resultCode) {
                TaskDetailActivity.EDIT_RESULT_OK -> snackbarText.set(
                        mContext.getString(R.string.successfully_saved_task_message))
                AddEditTaskActivity.ADD_EDIT_RESULT_OK -> snackbarText.set(
                        mContext.getString(R.string.successfully_added_task_message))
                TaskDetailActivity.DELETE_RESULT_OK -> snackbarText.set(
                        mContext.getString(R.string.successfully_deleted_task_message))
            }
        }
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [TasksDataSource]
     * *
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            dataLoading.set(true)
        }
        if (forceUpdate) {

            mTasksRepository.refreshTasks()
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment() // App is busy until further notice

        mTasksRepository.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = ArrayList<Task>()

                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.idlingResource.isIdleNow) {
                    EspressoIdlingResource.decrement() // Set app as idle.
                }

                // We filter the tasks based on the requestType
                for (task in tasks) {
                    when (mCurrentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                        TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                        // -> tasksToShow.add(task)
                    }
                }
                if (showLoadingUI) {
                    dataLoading.set(false)
                }
                mIsDataLoadingError.set(false)

                items.clear()
                items.addAll(tasksToShow)
                notifyPropertyChanged(BR.empty) // It's a @Bindable so update manually
            }

            override fun onDataNotAvailable() {
                mIsDataLoadingError.set(true)
            }
        })
    }

}
