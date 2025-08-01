package com.example.luckytask.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/*** Data Access Object for private tasks ***/
@Dao
interface PrivateTasksDAO {
    @Query("SELECT * FROM private_tasks")
    /***
     * Flow is a type that can emit multiple values sequentially
     * (stream of data that can be computed asynchronously)
     * as opposed to suspend functions that return only a single value)
     * --> Use Flow to receive live updates from a database
     * Source: https://developer.android.com/kotlin/flow
     *
     * This is needed here as to display newly added tasks in the list of inactive tasks ***/
    fun getAllPrivateTasks(): Flow<List<PrivateTaskItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateTask(task: PrivateTaskItem)

    @Update
    suspend fun updatePrivateTask(task: PrivateTaskItem)

    @Delete
    suspend fun deletePrivateTask(task: PrivateTaskItem)
}