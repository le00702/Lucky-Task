package com.example.luckytask

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckytask.data.GroupTaskItem
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.data.TaskRepository
import com.example.luckytask.model.PrivateTasksViewModel
import com.example.luckytask.model.PrivateTasksViewModelFactory
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskInput
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import kotlinx.coroutines.launch

class EditTaskActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val taskId = intent.getIntExtra("taskId", -1)
        val parentActivity = intent.getStringExtra("parentActivity") ?: "MyTasksActivity"
        val topBarTitle = intent.getStringExtra("topBarTitle") ?: "Edit Task"

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                AppWithDrawer(
                    currentActivityName = parentActivity,
                    topBarTitle = topBarTitle
                ) {
                    EditTaskScreen(
                        taskId = taskId,
                        modifier = Modifier.padding(20.dp),
                        onFinish = { finish() },
                        isGroupTask = parentActivity != "MyTasksActivity"
                    )
                }
            }
        }
    }
}

/*** Pass isGroupTask to differentiate what to load in edit screen ***/
@Composable
fun EditTaskScreen(
    taskId: Int,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit = {},
    isGroupTask: Boolean,
) {
    val HEADER_SIZE = 30.sp
    val taskRepository = remember { TaskRepository.getInstance() }

    /*** Provides a coroutine scope tied to the current Composable
     *   --> later used for local DB access ***/
    val coroutineScope = rememberCoroutineScope()

    var title = remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }

    val context = LocalContext.current
    val app = context.applicationContext as PrivateTasksApp
    val privateTasksViewModel: PrivateTasksViewModel =
        viewModel(factory = PrivateTasksViewModelFactory(app.database.privateTasksDAO()))

    // Load Task
    LaunchedEffect(taskId) {
        /*** For now, load group tasks from mock repo ***/
        if (isGroupTask) {
            val task = taskRepository.getTaskById(taskId)
            if (task != null) {
                title.value = task.title
                description.value = task.description
            }
        } else {
            /*** For private tasks display initial text when editing
             *   --> display old title
             *   --> display old description ***/
            val task = privateTasksViewModel.getTaskById(taskId)
            if (task != null) {
                title.value = task.title
                description.value = task.description
            }
            val allTasks = privateTasksViewModel.tasks
            allTasks.collect { taskList ->
                taskList.forEach { task ->
                    Log.d(
                        "DB",
                        "Task id=${task.id}, title=${task.title}"
                    )
                }
            }
        }
    }

    // reactive
    val formIsComplete by remember {
        derivedStateOf { title.value.isNotBlank() }
    }

    LazyColumn(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = "Edit Task",
                fontSize = HEADER_SIZE
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            AddTaskInput(title, "Task Title", "Enter a task title", isTitle = true)
        }

        item {
            AddTaskInput(description, "Task Description", "Enter a task description")
        }

        item {
            Button(
                onClick = {
                    if (isGroupTask) {
                        Toast.makeText(context, "GROUP TASK: edit", Toast.LENGTH_SHORT).show()
                        val task = taskRepository.getTaskById(taskId)
                        if (task != null) {
                            val updatedTask = when (task) {
                                is GroupTaskItem -> task.copy(
                                    title = title.value,
                                    description = description.value
                                )

                                else -> {
                                    throw IllegalArgumentException("Unsupported task type")
                                }
                            }
                            taskRepository.updateTask(updatedTask)
                            onFinish()
                        }
                    } else {
                        Toast.makeText(context, "PRIVATE TASK: edit", Toast.LENGTH_SHORT).show()
                        /*** This allows to safely call suspend functions from the current context ***/
                        coroutineScope.launch {
                            val task = privateTasksViewModel.getTaskById(taskId)
                            if (task != null) {
                                val updatedTask = task.copy(
                                    title = title.value,
                                    description = description.value
                                )
                                privateTasksViewModel.updateTask(updatedTask)
                                onFinish()
                            }
                        }
                    }
                },
                enabled = formIsComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (formIsComplete) colorResource(R.color.add_task_color)
                        else colorResource(R.color.task_text_color),
                    contentColor = Color.White
                ),
            ) {
                Text("Save Changes", fontSize = 25.sp)
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Button(
                onClick = {
                    taskRepository.deleteTask(taskId)
                    onFinish()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.error_color),
                    contentColor = Color.White
                ),
            ) {
                Text("Delete Task", fontSize = 25.sp)
            }
        }
    }
}