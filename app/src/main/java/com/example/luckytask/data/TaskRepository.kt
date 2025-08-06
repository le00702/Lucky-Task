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
            PrivateTaskItem(
                1,
                "Clean Kitchen",
                "Wash dishes and clean counters",
                LocalDate.now(),
                true,
                false
            ),
            PrivateTaskItem(
                2,
                "Study for Exam",
                "Review chapters 1-5",
                LocalDate.now().plusDays(7),
                false,
                false
            ),
            PrivateTaskItem(
                3,
                "Buy Groceries",
                "Milk, bread, eggs",
                LocalDate.now().plusDays(1),
                false,
                false
            ),
            PrivateTaskItem(4, "Workout", "30 min cardio", LocalDate.now(), true, false),
            PrivateTaskItem(
                5,
                "Read Book",
                "Finish chapter 3",
                LocalDate.now().minusDays(2),
                false,
                true
            ),

            // Group Tasks
            GroupTaskItem(
                6,
                "Clean Living Room",
                "Vacuum and dust",
                "John",
                LocalDate.now(),
                true,
                false
            ),
            GroupTaskItem(
                7,
                "Buy Toilet Paper",
                "For shared bathroom",
                null,
                LocalDate.now().plusDays(2),
                false,
                false
            ),
            GroupTaskItem(
                8,
                "Take Out Trash",
                "Weekly garbage day",
                null,
                LocalDate.now().minusDays(1),
                false,
                true
            ),
            GroupTaskItem(
                9,
                "Pay Internet Bill",
                "Monthly payment",
                "Mike",
                LocalDate.now().plusDays(5),
                false,
                false
            ),
            GroupTaskItem(
                10,
                "Organize Movie Night",
                "Pick movie and snacks",
                "Me",
                LocalDate.now(),
                true,
                false
            )
        )
    }

    fun getTaskById(id: Int): TaskItem? {
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

    fun deleteTask(taskId: Int): Boolean {
        val currentTasks = _tasks.value.toMutableList()
        val removed = currentTasks.removeIf { it.id == taskId }
        if (removed) {
            _tasks.value = currentTasks
        }
        return removed
    }

    fun toggleTaskCompletion(taskId: Int): Boolean {
        val task = getTaskById(taskId)
        return if (task != null) {
            val updatedTask = when (task) {
                is PrivateTaskItem -> task.copy(isCompleted = !task.isCompleted)
                is GroupTaskItem -> task.copy(isCompleted = !task.isCompleted)
                else -> return false
            }
            updateTask(updatedTask)
        } else {
            false
        }
    }

    fun activateTask(taskId: Int): Boolean {
        val task = getTaskById(taskId)
        return if (task != null && !task.isActive) {
            val updatedTask = when (task) {
                is PrivateTaskItem -> task.copy(isActive = true)
                is GroupTaskItem -> task.copy(isActive = true)
                else -> return false
            }
            updateTask(updatedTask)
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