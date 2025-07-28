package com.example.luckytask.data

import androidx.room.*

/*** Data Access Object for private tasks ***/
@Dao
interface PrivateTasksDAO {
    @Query("SELECT * FROM private_tasks")
    suspend fun getAllTasks(): List<PrivateTaskItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: PrivateTaskItem)

    @Delete
    suspend fun deleteTask(task: PrivateTaskItem)

    @Query("DELETE FROM private_tasks")
    suspend fun deleteAll()
}