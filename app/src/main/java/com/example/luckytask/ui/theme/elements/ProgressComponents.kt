package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.R
import com.example.luckytask.data.TaskItem

@Composable
fun SimpleProgressCircle(
    title: String,
    completed: Int,
    total: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.task_text_color),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProgressCircle(
            done = completed,
            total = total,
            color = color
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$completed / $total",
            fontSize = 14.sp,
            color = colorResource(R.color.task_text_color).copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SimpleActiveTasksDisplay(
    activeTasks: List<TaskItem>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = "Active Tasks",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = colorResource(R.color.task_text_color),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${activeTasks.size}",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.active_task_color)
        )

        Text(
            text = "remaining",
            fontSize = 14.sp,
            color = colorResource(R.color.task_text_color).copy(alpha = 0.7f)
        )
    }
}

// Helper functions for Task-Calculation
object TaskStatsCalculator {

    fun getPrivateTaskStats(tasks: List<TaskItem>): Pair<Int, Int> {
        val privateTasks = tasks.filter { !it.isGroupTask }
        val completed = privateTasks.count { it.isCompleted }
        val total = privateTasks.size
        return Pair(completed, total)
    }

    fun getGroupTaskStats(tasks: List<TaskItem>): Pair<Int, Int> {
        val groupTasks = tasks.filter { it.isGroupTask }
        val completed = groupTasks.count { it.isCompleted }
        val total = groupTasks.size
        return Pair(completed, total)
    }

    fun getActiveTasks(tasks: List<TaskItem>): List<TaskItem> {
        return tasks.filter { it.isActive && !it.isCompleted }
    }
}