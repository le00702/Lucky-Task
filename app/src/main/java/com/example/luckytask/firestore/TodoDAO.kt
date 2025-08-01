package com.example.luckytask.firestore

import java.time.LocalDate

/**
 * Same Fields as TaskItems, must have default values for remote fetching
 */
data class TodoDAO(
    var id: String? = null,
    var title: String = "",
    var description: String = "",
    var assignee: String? = null,
    var dueDate: LocalDate? = null,
    var isActive: Boolean = false,
    var isCompleted: Boolean = false,
    var isGroupTask: Boolean = false
)