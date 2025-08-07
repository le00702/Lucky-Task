package com.example.luckytask.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.data.PrivateTasksDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrivateTasksViewModel(private val privateTasksDAO: PrivateTasksDAO) : ViewModel() {
    /*** Maintain a list of private tasks --> use Flow to receive
     *   stream of tasks with live updates from DB
     *   Source: https://developer.android.com/kotlin/flow ***/
    private val _tasks = MutableStateFlow<List<PrivateTaskItem>>(emptyList())
    val tasks: StateFlow<List<PrivateTaskItem>> = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            privateTasksDAO.getAllPrivateTasks().collect { taskList ->
                _tasks.value = taskList
            }
        }
    }

    fun addTask(task: PrivateTaskItem) {
        Log.d("[TASK LOCAL]", "Add")
        viewModelScope.launch {
            privateTasksDAO.insertPrivateTask(task)
            loadTasks()
        }
    }

    fun deleteTask(task: PrivateTaskItem) {
        viewModelScope.launch {
            privateTasksDAO.deletePrivateTask(task)
            loadTasks()
        }
    }

    fun updateTask(task: PrivateTaskItem) {
        viewModelScope.launch {
            privateTasksDAO.updatePrivateTask(task)
            loadTasks()
        }
    }

    /*** As this is the only function returning something (for now)
     *   make it suspendable, so it does not return before the result is fetched
     *   from the local DB ***/
    suspend fun getTaskById(taskId: Int): PrivateTaskItem? {
        val task: PrivateTaskItem? =  privateTasksDAO.getTaskById(taskId)
        return task
    }

    /*** Draw a random task --> for now return list of indices  ***/
    fun drawRandomTask(): List<Int>{
        val drawNumbers = 1 .. _tasks.value.size
        return drawNumbers.toList()
    }
}
