package com.kolisnyk.serhii.kn313.option1.tracking

import android.databinding.BindingAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.widget.ListView
import com.kolisnyk.serhii.kn313.option1.tracking.data.Task
import com.kolisnyk.serhii.kn313.option1.tracking.tasks.TasksFragment

import com.kolisnyk.serhii.kn313.option1.tracking.tasks.TasksViewModel

@BindingAdapter("android:onRefresh")
fun setSwipeRefreshLayoutOnRefreshListener(view: ScrollChildSwipeRefreshLayout,
                                           viewModel: TasksViewModel) {
    view.setOnRefreshListener { viewModel.loadTasks(true) }
}

@BindingAdapter("app:items")
fun setItems(listView: ListView, items: List<Task>) {
    val adapter = listView.adapter as TasksFragment.TasksAdapter
    adapter.replaceData(items)
}
