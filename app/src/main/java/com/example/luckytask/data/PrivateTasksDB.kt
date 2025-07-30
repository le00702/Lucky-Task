package com.example.luckytask.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.luckytask.data.Converter.DateConverter

/*** Use Room DB for storing private tasks locally
 *   --> also use DateConverter for handling local dates ***/
@Database(entities = [PrivateTaskItem::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class PrivateTasksDB : RoomDatabase() {
    abstract fun privateTasksDAO(): PrivateTasksDAO
}