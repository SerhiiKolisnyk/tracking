package com.kolisnyk.serhii.kn313.option1.tracking.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kolisnyk.serhii.kn313.option1.tracking.data.Task

@Database(entities = arrayOf(Task::class), version = 1)
abstract class TasksDb : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}