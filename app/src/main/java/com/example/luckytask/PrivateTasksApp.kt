package com.example.luckytask

import android.app.Application
import androidx.room.Room
import com.example.luckytask.data.PrivateTasksDB

/*** Create DB for private tasks in app ***/
class PrivateTasksApp : Application() {
    lateinit var database: PrivateTasksDB private set
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder (applicationContext, PrivateTasksDB::class.java, "rates").build()
    }
}