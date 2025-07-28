package com.example.luckytask.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "task_item")
data class TaskItem(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String = "",
    val assignee: String? = null, // null = unassigned, "Me" = current user, or roommate name
    val dueDate: LocalDate? = null,
    val isActive: Boolean = false, // true = drawn/active task, false = TODO
    val isCompleted: Boolean = false,
    val isGroupTask: Boolean = false
)