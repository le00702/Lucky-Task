package com.example.luckytask.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.data.PrivateTasksDAO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PrivateTasksViewModel(private val privateTasksDAO: PrivateTasksDAO): ViewModel() {
    /*** Maintain a list of private tasks ***/
    private val _tasks = MutableStateFlow<List<PrivateTaskItem>>(emptyList())
    val tasks: StateFlow<List<PrivateTaskItem>> = _tasks

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = privateTasksDAO.getAllPrivateTasks()
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
}
