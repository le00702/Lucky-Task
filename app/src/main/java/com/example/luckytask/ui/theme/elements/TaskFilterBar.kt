package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.R
import com.example.luckytask.data.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskFilterBar(
    currentFilter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    val hasActiveFilters = currentFilter != TaskFilter()

    // Count active filters
    val activeFilterCount = listOf(
        currentFilter.assignee != AssigneeFilter.ALL,
        currentFilter.dueDate != DueDateFilter.ALL,
        currentFilter.activeStatus != ActiveStatusFilter.ALL
    ).count { it }

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Button(
            onClick = { showFilters = !showFilters },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasActiveFilters)
                    colorResource(R.color.add_task_color)
                else
                    Color.Gray.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = if (hasActiveFilters) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            !hasActiveFilters -> "Filter Tasks"
                            activeFilterCount == 1 -> "1 Filter Active"
                            else -> "$activeFilterCount Filters Active"
                        },
                        color = if (hasActiveFilters) Color.White else Color.Black,
                        fontSize = 16.sp
                    )
                }

                Icon(
                    imageVector = if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = if (hasActiveFilters) Color.White else Color.Black
                )
            }
        }

        if (showFilters) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Clear All button
                    if (hasActiveFilters) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Filters",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(
                                onClick = { onFilterChange(TaskFilter()) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear All", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Assignee Filter Section
                    FilterSection(
                        title = "Assignee",
                        icon = Icons.Default.Person
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AssigneeFilter.values().forEach { filter ->
                                FilterChip(
                                    selected = currentFilter.assignee == filter,
                                    onClick = {
                                        onFilterChange(currentFilter.copy(assignee = filter))
                                    },
                                    label = {
                                        Text(
                                            filter.displayName,
                                            fontSize = 11.sp
                                        )
                                    },
                                    leadingIcon = if (currentFilter.assignee == filter) {
                                        {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Due Date Filter Section
                    FilterSection(
                        title = "Due Date",
                        icon = Icons.Default.DateRange
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DueDateFilter.values().forEach { filter ->
                                FilterChip(
                                    selected = currentFilter.dueDate == filter,
                                    onClick = {
                                        onFilterChange(currentFilter.copy(dueDate = filter))
                                    },
                                    label = {
                                        Text(
                                            filter.displayName,
                                            fontSize = 11.sp
                                        )
                                    },
                                    leadingIcon = if (currentFilter.dueDate == filter) {
                                        {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = when(filter) {
                                            DueDateFilter.OVERDUE -> colorResource(R.color.error_color)
                                            DueDateFilter.TODAY -> colorResource(R.color.active_task_color)
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        }
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Filter Section
                    FilterSection(
                        title = "Status",
                        icon = Icons.Default.Flag
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ActiveStatusFilter.values().forEach { filter ->
                                FilterChip(
                                    selected = currentFilter.activeStatus == filter,
                                    onClick = {
                                        onFilterChange(currentFilter.copy(activeStatus = filter))
                                    },
                                    label = {
                                        Text(
                                            filter.displayName,
                                            fontSize = 11.sp
                                        )
                                    },
                                    leadingIcon = when {
                                        currentFilter.activeStatus == filter -> {
                                            {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        filter == ActiveStatusFilter.COMPLETED -> {
                                            {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Completed",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        filter == ActiveStatusFilter.ACTIVE -> {
                                            {
                                                Icon(
                                                    Icons.Default.PlayArrow,
                                                    contentDescription = "Active",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        else -> null
                                    }
                                )
                            }
                        }
                    }

                    // Quick Filters
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Quick Filters",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // My Today's Tasks
                        AssistChip(
                            onClick = {
                                onFilterChange(
                                    TaskFilter(
                                        assignee = AssigneeFilter.ME,
                                        dueDate = DueDateFilter.TODAY,
                                        activeStatus = ActiveStatusFilter.ALL
                                    )
                                )
                            },
                            label = { Text("My Today", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Today,
                                    contentDescription = "Today",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )

                        // Overdue Tasks
                        AssistChip(
                            onClick = {
                                onFilterChange(
                                    TaskFilter(
                                        assignee = AssigneeFilter.ALL,
                                        dueDate = DueDateFilter.OVERDUE,
                                        activeStatus = ActiveStatusFilter.ALL
                                    )
                                )
                            },
                            label = { Text("Overdue", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = "Overdue",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = colorResource(R.color.error_color).copy(alpha = 0.1f)
                            )
                        )

                        // Active Tasks
                        AssistChip(
                            onClick = {
                                onFilterChange(
                                    TaskFilter(
                                        assignee = AssigneeFilter.ALL,
                                        dueDate = DueDateFilter.ALL,
                                        activeStatus = ActiveStatusFilter.ACTIVE
                                    )
                                )
                            },
                            label = { Text("Active Only", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Active",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        // Show active filter summary when filters are collapsed
        if (!showFilters && hasActiveFilters) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (currentFilter.assignee != AssigneeFilter.ALL) {
                    ActiveFilterChip(
                        text = currentFilter.assignee.displayName,
                        onRemove = {
                            onFilterChange(currentFilter.copy(assignee = AssigneeFilter.ALL))
                        }
                    )
                }
                if (currentFilter.dueDate != DueDateFilter.ALL) {
                    ActiveFilterChip(
                        text = currentFilter.dueDate.displayName,
                        onRemove = {
                            onFilterChange(currentFilter.copy(dueDate = DueDateFilter.ALL))
                        }
                    )
                }
                if (currentFilter.activeStatus != ActiveStatusFilter.ALL) {
                    ActiveFilterChip(
                        text = currentFilter.activeStatus.displayName,
                        onRemove = {
                            onFilterChange(currentFilter.copy(activeStatus = ActiveStatusFilter.ALL))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(18.dp),
                tint = colorResource(R.color.task_text_color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        content()
    }
}

@Composable
private fun ActiveFilterChip(
    text: String,
    onRemove: () -> Unit
) {
    AssistChip(
        onClick = onRemove,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remove",
                    modifier = Modifier.size(12.dp)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = colorResource(R.color.add_task_color).copy(alpha = 0.2f)
        ),
        modifier = Modifier.height(28.dp)
    )
}