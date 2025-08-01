package com.example.luckytask.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/*** Extract most important info in abstract class for tasks ***/
abstract class TaskItem(
    open val title: String,
    open val description: String = "",
    open val dueDate: LocalDate? = null,
    open val isActive: Boolean = false, // true = drawn/active task, false = TODO
    open val isCompleted: Boolean = false,
)

/*** Extract separate data class/entity for private tasks -->
 *   pass only title to constructor as it does not have a default value
 *   auto-generate id for each private task ***/
@Entity(tableName = "private_tasks")
data class PrivateTaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    override val title: String,
    override val description: String = "",
    override val dueDate: LocalDate? = null,
    override val isActive: Boolean = false, // true = drawn/active task, false = TODO
    override val isCompleted: Boolean = false,
): TaskItem(title)

/*** Extract separate data class/entity for group tasks -->
 *   pass only title to constructor as it does not have a default value ***/
@Entity(tableName = "group_tasks")
data class GroupTaskItem(
    @PrimaryKey val id: Int,
    override val title: String,
    override val description: String = "",
    val assignee: String? = null, // null = unassigned, "Me" = current user, or roommate name
    override val dueDate: LocalDate? = null,
    override val isActive: Boolean = false, // true = drawn/active task, false = TODO
    override val isCompleted: Boolean = false,
): TaskItem(title)