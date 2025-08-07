package com.example.luckytask.firestore

import com.example.luckytask.data.TaskItem
import java.time.LocalDate

/**
 * Same Fields as TaskItems, must have default values for remote fetching
 */
data class TaskDAO(
    override val id: String? = null,
    override val title: String,
    override val description: String = "",
    override val dueDate: LocalDate? = null,
    override val isActive: Boolean = false, // true = drawn/active task, false = TODO
    override val isCompleted: Boolean = false,
): TaskItem(title)