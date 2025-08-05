package com.example.luckytask.ui.theme.elements

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.EditTaskActivity
import com.example.luckytask.R
import com.example.luckytask.data.TaskItem
import com.example.luckytask.data.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditableTaskCard(
    task: TaskItem,
    modifier: Modifier = Modifier,
    onTaskUpdated: () -> Unit = {}
) {
    val context = LocalContext.current
    val taskRepository = remember { TaskRepository.getInstance() }
    val scope = rememberCoroutineScope()

    // State for error handling
    var isUpdating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Animation for completed state
    val alpha by animateFloatAsState(
        targetValue = if (task.isCompleted) 0.6f else 1f,
        label = "alpha"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            task.isCompleted -> Color.Gray.copy(alpha = 0.3f)
            task.assignee != null && task.assignee != "Me" -> colorResource(R.color.roommate_task_color)
            task.isActive -> colorResource(R.color.active_task_color)
            else -> colorResource(R.color.app_color)
        },
        label = "backgroundColor"
    )

    val isOverdue = task.dueDate?.isBefore(LocalDate.now()) == true && !task.isCompleted
    var showInfo by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = if (task.isCompleted) 2.dp else 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Checkbox for completion status
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { isChecked ->
                            if (!isUpdating) {
                                isUpdating = true
                                errorMessage = null

                                scope.launch {
                                    try {
                                        val success = taskRepository.toggleTaskCompletion(task.id)

                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                if (isChecked) "Task completed!" else "Task reopened",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            Log.d("EditableTaskCard", "Task ${task.id} completion toggled")

                                            // Small delay for visual feedback
                                            delay(100)
                                            onTaskUpdated()
                                        } else {
                                            errorMessage = "Failed to update task"
                                            Log.e("EditableTaskCard", "Failed to toggle task completion")
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                        Log.e("EditableTaskCard", "Error toggling task completion", e)

                                        Toast.makeText(
                                            context,
                                            "Failed to update task: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally {
                                        isUpdating = false
                                    }
                                }
                            }
                        },
                        enabled = !isUpdating,
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.add_task_color),
                            uncheckedColor = colorResource(R.color.task_text_color)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.task_text_color),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )

                        if (task.description.isNotEmpty()) {
                            Text(
                                text = task.description,
                                fontSize = 14.sp,
                                color = colorResource(R.color.task_text_color).copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp),
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }

                // Status badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (task.isCompleted) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Green.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Color.Green,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "DONE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green
                                )
                            }
                        }
                    } else if (task.isActive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colorResource(R.color.add_task_color)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Active",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Task details and action buttons
            if (task.assignee != null || task.dueDate != null || errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    // Error message if any
                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = colorResource(R.color.error_color),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        task.assignee?.let { assignee ->
                            TaskDetailChip(
                                icon = Icons.Default.Person,
                                text = assignee,
                                isHighlighted = assignee != "Me"
                            )
                        }

                        task.dueDate?.let { dueDate ->
                            TaskDetailChip(
                                icon = Icons.Default.Schedule,
                                text = formatDueDate(dueDate),
                                isHighlighted = isOverdue,
                                isError = isOverdue
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Action buttons
                        Row {
                            // Active/Deactivate button for non-completed tasks
                            if (!task.isCompleted) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            try {
                                                val success = if (task.isActive) {
                                                    taskRepository.deactivateTask(task.id)
                                                } else {
                                                    taskRepository.activateTask(task.id)
                                                }

                                                if (success) {
                                                    Toast.makeText(
                                                        context,
                                                        if (!task.isActive) "Task activated!" else "Task deactivated",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    onTaskUpdated()
                                                }
                                            } catch (e: Exception) {
                                                Log.e("EditableTaskCard", "Error toggling active state", e)
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(32.dp),
                                    enabled = !isUpdating
                                ) {
                                    Icon(
                                        imageVector = if (task.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (task.isActive) "Deactivate" else "Activate",
                                        tint = colorResource(R.color.task_text_color),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Edit Button
                            IconButton(
                                onClick = {
                                    try {
                                        startEditActivity(context, task.id)
                                    } catch (e: Exception) {
                                        Log.e("EditableTaskCard", "Error starting edit activity", e)
                                        Toast.makeText(
                                            context,
                                            "Failed to open edit screen",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.size(32.dp),
                                enabled = !isUpdating
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Task",
                                    tint = colorResource(R.color.task_text_color),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // Info Button
                            IconButton(
                                onClick = { showInfo = !showInfo },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.info),
                                    contentDescription = "Task Info",
                                    tint = colorResource(R.color.task_text_color),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Active/Deactivate button
                    if (!task.isCompleted) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    try {
                                        val success = if (task.isActive) {
                                            taskRepository.deactivateTask(task.id)
                                        } else {
                                            taskRepository.activateTask(task.id)
                                        }

                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                if (!task.isActive) "Task activated!" else "Task deactivated",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onTaskUpdated()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("EditableTaskCard", "Error toggling active state", e)
                                    }
                                }
                            },
                            modifier = Modifier.size(32.dp),
                            enabled = !isUpdating
                        ) {
                            Icon(
                                imageVector = if (task.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (task.isActive) "Deactivate" else "Activate",
                                tint = colorResource(R.color.task_text_color),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Edit Button
                    IconButton(
                        onClick = {
                            try {
                                startEditActivity(context, task.id)
                            } catch (e: Exception) {
                                Log.e("EditableTaskCard", "Error starting edit activity", e)
                                Toast.makeText(
                                    context,
                                    "Failed to open edit screen",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier.size(32.dp),
                        enabled = !isUpdating
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = colorResource(R.color.task_text_color),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Info Button
                    IconButton(
                        onClick = { showInfo = !showInfo },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.info),
                            contentDescription = "Task Info",
                            tint = colorResource(R.color.task_text_color),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Info Popup
            if (showInfo) {
                TaskInfoPopup(
                    title = task.title,
                    text = buildString {
                        appendLine(task.description.ifEmpty { "No description available" })
                        appendLine()
                        if (task.assignee != null) appendLine("Assigned to: ${task.assignee}")
                        if (task.dueDate != null) appendLine("Due: ${formatDueDate(task.dueDate)}")
                        appendLine("Status: ${
                            when {
                                task.isCompleted -> "Completed ✓"
                                task.isActive -> "Active"
                                else -> "TODO"
                            }
                        }")
                        if (task.isGroupTask) appendLine("Type: Group Task")
                    },
                    onDismissRequest = { showInfo = false },
                    parentColor = backgroundColor
                )
            }
        }
    }
}

private fun startEditActivity(context: Context, taskId: String) {
    val intent = Intent(context, EditTaskActivity::class.java).apply {
        putExtra("taskId", taskId)
        putExtra("parentActivity", "MyTasksActivity")
        putExtra("topBarTitle", "Edit Task")
    }
    context.startActivity(intent)
}

// Helper function for date formatting
private fun formatDueDate(dueDate: LocalDate): String {
    val today = LocalDate.now()
    return when {
        dueDate == today -> "Today"
        dueDate == today.plusDays(1) -> "Tomorrow"
        dueDate == today.minusDays(1) -> "Yesterday"
        dueDate.isBefore(today) -> "Overdue (${dueDate.format(DateTimeFormatter.ofPattern("MMM dd"))})"
        dueDate.isAfter(today) && dueDate.isBefore(today.plusDays(7)) ->
            dueDate.format(DateTimeFormatter.ofPattern("EEEE"))
        else -> dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
}