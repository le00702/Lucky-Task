package com.example.luckytask.data

data class TaskFilter(
    val assignee: AssigneeFilter = AssigneeFilter.ALL,
    val dueDate: DueDateFilter = DueDateFilter.ALL,
    val activeStatus: ActiveStatusFilter = ActiveStatusFilter.ALL
)

enum class AssigneeFilter(val displayName: String) {
    ALL("All"),
    ME("Me"),
    UNASSIGNED("Unassigned"),
    ROOMMATES("Roommates")
}

enum class DueDateFilter(val displayName: String) {
    ALL("All Dates"),
    TODAY("Today"),
    THIS_WEEK("This Week"),
    OVERDUE("Overdue"),
    NO_DUE_DATE("No Due Date")
}

enum class ActiveStatusFilter(val displayName: String) {
    ALL("All"),
    ACTIVE("Active"),
    TODO("TODO"),
    COMPLETED("Completed")
}