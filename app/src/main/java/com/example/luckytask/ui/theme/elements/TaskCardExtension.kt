package com.example.luckytask.ui.theme.elements

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.EditTaskActivity
import com.example.luckytask.R
import com.example.luckytask.data.GroupTaskItem
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.data.TaskItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditableTaskCard(
    task: TaskItem,
    modifier: Modifier = Modifier,
    onTaskUpdated: () -> Unit = {}, // Callback for UI refresh
    isGroupTask: Boolean
) {
    val context = LocalContext.current

    val backgroundColor = when {
        task is GroupTaskItem && task.assignee != null -> colorResource(R.color.roommate_task_color)
        task.isActive -> colorResource(R.color.active_task_color)
        else -> colorResource(R.color.app_color)
    }

    val isOverdue = task.dueDate?.isBefore(LocalDate.now()) == true && !task.isCompleted
    var showInfo by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.task_text_color),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    /*if (task.description.isNotEmpty()) {
                        Text(
                            text = task.description,
                            fontSize = 14.sp,
                            color = colorResource(R.color.task_text_color).copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }*/
                }

                if (task.isActive) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = colorResource(R.color.add_task_color)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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

            if ((task is PrivateTaskItem || task is GroupTaskItem && task.assignee != null) || task.dueDate != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(task is GroupTaskItem) {
                        task.assignee?.let { assignee ->
                            TaskDetailChip(
                                icon = Icons.Default.Person,
                                text = assignee,
                                isHighlighted = assignee != "Me"
                            )
                        }
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

                    // Edit Button
                    IconButton(
                        onClick = { startEditActivity(context, task, isGroupTask) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = colorResource(R.color.task_text_color),
                            modifier = Modifier.size(18.dp)
                        )
                    }

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
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Edit Button
                    IconButton(
                        onClick = { startEditActivity(context, task, isGroupTask) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = colorResource(R.color.task_text_color),
                            modifier = Modifier.size(18.dp)
                        )
                    }

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

            if (showInfo) {
                TaskInfoPopup(
                    task.title,
                    task.description.ifEmpty { "No description available" },
                    onDismissRequest = { showInfo = false },
                    parentColor = backgroundColor
                )
            }
        }
    }
}

/*** Pass isGroupTask to choose parent activity ***/
private fun startEditActivity(context: Context, task:TaskItem, isGroupTask: Boolean) {
    val intent = Intent(context, EditTaskActivity::class.java).apply {
        val parentActivity = if(isGroupTask) "MyGroupTasksActivity" else "MyTasksActivity"
        putExtra("taskId", task.id)
        putExtra("parentActivity", parentActivity)
        putExtra("topBarTitle", "Edit Task")
        putExtra("remoteID", (task as? GroupTaskItem)?.remoteId?:"")
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