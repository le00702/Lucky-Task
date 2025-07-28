package com.example.luckytask.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

fun List<TaskItem>.applyFilters(filter: TaskFilter): List<TaskItem> {
    return this
        .filter { task -> if(task is GroupTaskItem) task.matchesAssigneeFilter(filter.assignee) else false }
        .filter { task -> task.matchesDueDateFilter(filter.dueDate) }
        .filter { task -> task.matchesActiveStatusFilter(filter.activeStatus) }
}

private fun GroupTaskItem.matchesAssigneeFilter(filter: AssigneeFilter): Boolean {
    return when (filter) {
        AssigneeFilter.ALL -> true
        AssigneeFilter.ME -> assignee == "Me" || assignee == null
        AssigneeFilter.UNASSIGNED -> assignee == null
        AssigneeFilter.ROOMMATES -> assignee != null && assignee != "Me"
    }
}

private fun TaskItem.matchesDueDateFilter(filter: DueDateFilter): Boolean {
    val today = LocalDate.now()
    val dueDate = dueDate

    return when (filter) {
        DueDateFilter.ALL -> true
        DueDateFilter.TODAY -> dueDate == today
        DueDateFilter.THIS_WEEK -> {
            dueDate != null &&
                    dueDate >= today &&
                    dueDate <= today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
        }
        DueDateFilter.OVERDUE -> dueDate != null && dueDate < today
        DueDateFilter.NO_DUE_DATE -> dueDate == null
    }
}

private fun TaskItem.matchesActiveStatusFilter(filter: ActiveStatusFilter): Boolean {
    return when (filter) {
        ActiveStatusFilter.ALL -> true
        ActiveStatusFilter.ACTIVE -> isActive && !isCompleted
        ActiveStatusFilter.TODO -> !isActive && !isCompleted
        ActiveStatusFilter.COMPLETED -> isCompleted
    }
}