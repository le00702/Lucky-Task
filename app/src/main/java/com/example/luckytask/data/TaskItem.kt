package com.example.luckytask.data

import java.time.LocalDate

data class TaskItem(
    val id: String,
    val title: String,
    val description: String = "",
    val assignee: String? = null, // null = unassigned, "Me" = current user, or roommate name
    val dueDate: LocalDate? = null,
    val isActive: Boolean = false, // true = drawn/active task, false = TODO
    val isCompleted: Boolean = false,
    val isGroupTask: Boolean = false
)