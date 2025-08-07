package com.example.luckytask.ui.theme.elements

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.R
import com.example.luckytask.data.GroupTaskItem
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.data.TaskItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TaskCard(
    task: TaskItem,
    modifier: Modifier = Modifier,
    isMine: Boolean = false,
    setDone: (TaskItem) -> Unit,
    deleteTask: (TaskItem) -> Unit,
    //onInfoClick: () -> Unit = {}
) {
    val backgroundColor = when {
        /*** If it is a group task done by a roommate, color it as such ***/
        (task is GroupTaskItem) && task.assignee != null && !isMine -> colorResource(R.color.roommate_task_color)
        /*** If it is done by me, mark it as active ***/
        task.isActive -> colorResource(R.color.active_task_color)
        /*** If it is still in the pool (no one has drawn it) leave it in the standard color ***/
        else -> colorResource(R.color.app_color)
    }

    val isOverdue = task.dueDate?.isBefore(LocalDate.now()) == true && !task.isCompleted

    /*** Extract this variable for deciding whether to display the detailed info ***/
    var showInfo by remember { mutableStateOf(false) }
    val onInfoClick = {
        /*** When the detailed info is shown, revert the value
         *   --> does not need to be shown anymore (for now) ***/
        showInfo = !showInfo
    }

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
        Box (modifier = modifier.pointerInput(Unit){
            detectTapGestures(
                onDoubleTap = {
                    if(!isMine){
                        Log.i("TaskCard", "Task info clicked")
                        return@detectTapGestures
                    }

                    if(task.isActive && !task.isCompleted){
                        setDone(task)
                        Log.i("TaskCard", "Task marked as done")
                        return@detectTapGestures
                    }

                    if(task.isActive && task.isCompleted){
                        deleteTask(task)
                        Log.i("TaskCard", "Task deleted")
                        return@detectTapGestures
                    }
                }
            )
        }) {

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
                    }
                }
                if (task is GroupTaskItem) {
                    if (task.assignee != null || task.dueDate != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            /*** If it is my own task/drawn by me, no need to display a 'user' ***/
                            if (task.assignee != "Me") {
                                task.assignee?.let { assignee ->
                                    TaskDetailChip(
                                        icon = Icons.Default.Person,
                                        text = assignee,
                                        isHighlighted = true
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

                            IconButton(
                                onClick = onInfoClick,
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
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onInfoClick,
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
                /*** Display the detailed info of the task ***/
                if (showInfo) {
                    TaskInfoPopup(
                        task.title, task.description, onDismissRequest = { showInfo = false },
                        parentColor = backgroundColor
                    )
                }
            }
        }
    }
}

@Composable
fun TaskDetailChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isHighlighted: Boolean = false,
    isError: Boolean = false,
) {
    val backgroundColor = when {
        isError -> colorResource(R.color.error_color)
        isHighlighted -> colorResource(R.color.add_task_color)
        else -> colorResource(R.color.task_text_color).copy(alpha = 0.1f)
    }

    val contentColor = when {
        isError || isHighlighted -> Color.White
        else -> colorResource(R.color.task_text_color)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isHighlighted || isError) FontWeight.Medium else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}

private fun formatDueDate(dueDate: LocalDate): String {
    val today = LocalDate.now()
    return when {
        dueDate == today -> "Today"
        dueDate == today.plusDays(1) -> "Tomorrow"
        dueDate == today.minusDays(1) -> "Yesterday"
        dueDate.isBefore(today) -> "Overdue"
        dueDate.isAfter(today) && dueDate.isBefore(today.plusDays(7)) ->
            dueDate.format(DateTimeFormatter.ofPattern("EEEE"))

        else -> dueDate.format(DateTimeFormatter.ofPattern("MMM dd"))
    }
}