package com.kolisnyk.serhii.kn313.option1.tracking

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.ObservableField

import com.kolisnyk.serhii.kn313.option1.tracking.data.Task
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksDataSource
import com.kolisnyk.serhii.kn313.option1.tracking.data.source.TasksRepository

abstract class TaskViewModel(
        context: Context,
        private val mTasksRepository: TasksRepository)
    : BaseObservable(), TasksDataSource.GetTaskCallback {

    val snackbarText = ObservableField<String>()

    val title = ObservableField<String>()

    val description = ObservableField<String>()

    private val mTaskObservable = ObservableField<Task>()

    private val mContext: Context

    @get:Bindable
    var isDataLoading: Boolean = false
        private set

    init {
        mContext = context.applicationContext // Force use of Application Context.

        // Exposed observables depend on the mTaskObservable observable:
        mTaskObservable.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, i: Int) {
                val task = mTaskObservable.get()
                if (task != null) {
                    title.set(task.title)
                    description.set(task.description)
                } else {
                    title.set(mContext.getString(R.string.no_data))
                    description.set(mContext.getString(R.string.no_data_description))
                }
            }
        })
    }

    fun start(taskId: String?) {
        if (taskId != null) {
            isDataLoading = true
            mTasksRepository.getTask(taskId, this)
        }
    }

    fun setTask(task: Task) {
        mTaskObservable.set(task)
    }

    // "completed" is two-way bound, so in order to intercept the new value, use a @Bindable
    // annotation and process it in the setter.
    // Update the entity
    // Notify repository and user
    var completed: Boolean
        @Bindable
        get() = mTaskObservable.get().isCompleted
        set(completed) {
            if (isDataLoading) {
                return
            }
            val task = mTaskObservable.get()
            task.isCompleted = completed
            if (completed) {
                mTasksRepository.completeTask(task)
                snackbarText.set(mContext.resources.getString(R.string.task_marked_complete))
            } else {
                mTasksRepository.activateTask(task)
                snackbarText.set(mContext.resources.getString(R.string.task_marked_active))
            }
        }

    val isDataAvailable: Boolean
        @Bindable
        get() = mTaskObservable.get() != null

    // This could be an observable, but we save a call to Task.getTitleForList() if not needed.
    val titleForList: String
        @Bindable
        get() {
            mTaskObservable.get()?: return "No data"
            return mTaskObservable.get().titleForList!!
        }

    override fun onTaskLoaded(task: Task) {
        mTaskObservable.set(task)
        isDataLoading = false
        notifyChange() // For the @Bindable properties
    }

    override fun onDataNotAvailable() {
        mTaskObservable.set(null)
        isDataLoading = false
    }

    open fun deleteTask() {
        if (mTaskObservable.get() != null) {
            mTasksRepository.deleteTask(mTaskObservable.get().id)
        }
    }

    fun onRefresh() {
        if (mTaskObservable.get() != null) {
            start(mTaskObservable.get().id)
        }
    }

    fun getSnackbarTextString(): String {
        return snackbarText.get()
    }

    protected val taskId: String?
        get() = mTaskObservable.get().id
}
