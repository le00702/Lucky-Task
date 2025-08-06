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

    fun getTaskById(taskId: Int): PrivateTaskItem? {
        var task: PrivateTaskItem? = null
        viewModelScope.launch {
            task = privateTasksDAO.getTaskById(taskId)
        }
        return task
    }
}
