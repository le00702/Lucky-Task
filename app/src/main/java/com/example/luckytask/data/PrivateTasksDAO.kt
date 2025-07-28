package com.example.luckytask.data

import androidx.room.*

/*** Data Access Object for private tasks ***/
@Dao
interface PrivateTasksDAO {
    @Query("SELECT * FROM private_tasks")
    suspend fun getAllPrivateTasks(): List<PrivateTaskItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrivateTask(task: PrivateTaskItem)

    @Update
    suspend fun updatePrivateTask(task: PrivateTaskItem)

    @Delete
    suspend fun deletePrivateTask(task: PrivateTaskItem)

    @Query("DELETE FROM private_tasks")
    suspend fun deleteAllPrivateTasks()
}