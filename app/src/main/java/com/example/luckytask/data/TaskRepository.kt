package com.example.luckytask.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class TaskRepository {
    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    init {
        // Expanded Mock-Data
        _tasks.value = listOf(

            // Private Tasks
            TaskItem("1", "Clean Kitchen", "Wash dishes and clean counters", "Me", LocalDate.now(), true, false, false),
            TaskItem("2", "Study for Exam", "Review chapters 1-5", "Me", LocalDate.now().plusDays(7), false, false, false),
            TaskItem("3", "Buy Groceries", "Milk, bread, eggs", null, LocalDate.now().plusDays(1), false, false, false),
            TaskItem("4", "Workout", "30 min cardio", "Me", LocalDate.now(), true, false, false),
            TaskItem("5", "Read Book", "Finish chapter 3", "Me", LocalDate.now().minusDays(2), false, true, false),

            // Group Tasks
            TaskItem("6", "Clean Living Room", "Vacuum and dust", "John", LocalDate.now(), true, false, true),
            TaskItem("7", "Buy Toilet Paper", "For shared bathroom", null, LocalDate.now().plusDays(2), false, false, true),
            TaskItem("8", "Take Out Trash", "Weekly garbage day", "Sarah", LocalDate.now().minusDays(1), false, true, true),
            TaskItem("9", "Pay Internet Bill", "Monthly payment", "Mike", LocalDate.now().plusDays(5), false, false, true),
            TaskItem("10", "Organize Movie Night", "Pick movie and snacks", "Me", LocalDate.now(), true, false, true),
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

    fun toggleTaskCompletion(taskId: String): Boolean {
        val task = getTaskById(taskId)
        return if (task != null) {
            updateTask(task.copy(isCompleted = !task.isCompleted))
        } else {
            false
        }
    }

    fun activateTask(taskId: String): Boolean {
        val task = getTaskById(taskId)
        return if (task != null && !task.isActive) {
            updateTask(task.copy(isActive = true))
        } else {
            false
        }
    }

    fun deactivateTask(taskId: String): Boolean {
        val task = getTaskById(taskId)
        return if (task != null && task.isActive) {
            updateTask(task.copy(isActive = false))
        } else {
            false
        }
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