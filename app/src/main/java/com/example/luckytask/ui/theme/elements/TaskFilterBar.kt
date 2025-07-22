package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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

@Composable
fun TaskFilterBar(
    currentFilter: TaskFilter,
    onFilterChange: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }
    val hasActiveFilters = currentFilter != TaskFilter()

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
                        text = if (hasActiveFilters) "Filters Active" else "Filter Tasks",
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    if (hasActiveFilters) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { onFilterChange(TaskFilter()) }) {
                                Text("Clear All", fontSize = 12.sp)
                            }
                        }
                    }

                    Text(
                        text = "Assignee",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssigneeFilter.values().forEach { filter ->
                            FilterChip(
                                selected = currentFilter.assignee == filter,
                                onClick = { onFilterChange(currentFilter.copy(assignee = filter)) },
                                label = { Text(filter.displayName, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Status",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ActiveStatusFilter.values().forEach { filter ->
                            FilterChip(
                                selected = currentFilter.activeStatus == filter,
                                onClick = { onFilterChange(currentFilter.copy(activeStatus = filter)) },
                                label = { Text(filter.displayName, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }
    }
}