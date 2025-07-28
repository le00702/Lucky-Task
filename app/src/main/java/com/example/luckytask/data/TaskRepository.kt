package com.example.luckytask.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class TaskRepository {
    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    init {
        // Mock-Data from MyTasksActivity
        _tasks.value = listOf(
            TaskItem("1", "Clean Kitchen", "Wash dishes", "Me", LocalDate.now(), true),
            TaskItem("2", "Buy Groceries", "", null, LocalDate.now().plusDays(1)),
            TaskItem("3", "Study", "", "Me", LocalDate.now().plusDays(7)),
            TaskItem("4", "Meeting", "", "John", LocalDate.now(), true),
        )
    }

    fun getTaskById(id: String): TaskItem? {
        return _tasks.value.find { it.id == id }
    }

    fun updateTask(updatedTask: TaskItem): Boolean {
        val currentTasks = _tasks.value.toMutableList()
        val index = currentTasks.indexOfFirst { it.id == updatedTask.id }

        return if (index != -1) {
            currentTasks[index] = updatedTask
            _tasks.value = currentTasks
            true
        } else {
            false
        }
    }

    fun addTask(task: TaskItem): Boolean {
        val currentTasks = _tasks.value.toMutableList()
        currentTasks.add(task)
        _tasks.value = currentTasks
        return true
    }

    fun deleteTask(taskId: String): Boolean {
        val currentTasks = _tasks.value.toMutableList()
        val removed = currentTasks.removeIf { it.id == taskId }
        if (removed) {
            _tasks.value = currentTasks
        }
        return removed
    }

    companion object {
        @Volatile
        private var INSTANCE: TaskRepository? = null

        fun getInstance(): TaskRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TaskRepository().also { INSTANCE = it }
            }
        }
    }
}