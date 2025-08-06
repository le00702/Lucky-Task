package com.example.luckytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckytask.data.PrivateTasksDB
import com.example.luckytask.model.PrivateTasksViewModel
import com.example.luckytask.model.PrivateTasksViewModelFactory
import androidx.compose.ui.unit.dp
import com.example.luckytask.data.TaskRepository
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.MockButton
import com.example.luckytask.ui.theme.elements.SimpleActiveTasksDisplay
import com.example.luckytask.ui.theme.elements.SimpleProgressCircle
import com.example.luckytask.ui.theme.elements.TaskStatsCalculator
import androidx.compose.foundation.lazy.LazyColumn

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                AppWithDrawer(
                    currentActivityName = "MainActivity",
                    topBarTitle = "Lucky Task"
                ) {
                    LuckyTaskApp()
                }
            }
        }
    }
}

@Composable
fun LuckyTaskApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val taskRepository = remember { TaskRepository.getInstance() }
    val tasks by taskRepository.tasks.collectAsState()
    val (privateCompleted, privateTotal) = TaskStatsCalculator.getPrivateTaskStats(tasks)
    val (groupCompleted, groupTotal) = TaskStatsCalculator.getGroupTaskStats(tasks)
    val activeTasks = TaskStatsCalculator.getActiveTasks(tasks)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SimpleProgressCircle(
                    title = "Private Tasks",
                    completed = privateCompleted,
                    total = privateTotal,
                    color = colorResource(R.color.add_task_color)
                )

                SimpleProgressCircle(
                    title = "Group Tasks",
                    completed = groupCompleted,
                    total = groupTotal,
                    color = colorResource(R.color.roommate_task_color)
                )

                SimpleActiveTasksDisplay(activeTasks = activeTasks)
            }
        }

        /*
        // Navigation Buttons
        item {
            MockButton(context, MockActivity::class.java, "Go to MockActivity")
        }
        item {
            MockButton(context, MockDiceActivity::class.java, "Go to Dice")
        }
        item {
            MockButton(context, MyTasksActivity::class.java, "Go to my Tasks")
        }
        item {
            MockButton(context, GroupTasksActivity::class.java, "Go to Group Tasks")
        }
        */
    }
}

@Preview(showBackground = true)
@Composable
fun LuckyTaskAppPreview() {
    LuckyTaskTheme {
        LuckyTaskApp()
    }
}