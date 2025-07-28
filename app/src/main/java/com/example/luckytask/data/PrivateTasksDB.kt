package com.example.luckytask.data

import androidx.room.Database
import androidx.room.RoomDatabase

/*** Use Room DB for storing private tasks locally ***/
@Database(entities = [PrivateTaskItem::class], version = 1)
abstract class PrivateTasksDB : RoomDatabase() {
    abstract fun privateTasksDAO(): PrivateTasksDAO
}