package com.example.luckytask

import android.app.Activity
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.model.PrivateTasksViewModel
import com.example.luckytask.model.PrivateTasksViewModelFactory
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskInput
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddNewTaskActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                val parentActivity = intent.getStringExtra("parentActivity").toString()
                val topBarTitle = intent.getStringExtra("topBarTitle").toString()
                val isGroupTask = intent.getBooleanExtra("isGroupTask", false)

                AppWithDrawer(
                    currentActivityName = parentActivity,
                    topBarTitle = topBarTitle
                ) {
                    AddNewTaskScreen(
                        modifier = Modifier.padding(20.dp),
                        isGroupTask = isGroupTask
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewTaskScreen(modifier: Modifier = Modifier, isGroupTask: Boolean) {

    val HEADER_SIZE = 30.sp

    /*** Use '=' to assign as MutableState instead of String (by using 'by') ***/
    var title = remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }
    var formIsComplete = title.value.isNotBlank() && description.value.isNotBlank()

    /*** Use context + DB for inserting a new task ***/
    val context = LocalContext.current
    val app = context.applicationContext as PrivateTasksApp
    val privateTasksViewModel: PrivateTasksViewModel =
        viewModel(factory = PrivateTasksViewModelFactory(app.database.privateTasksDAO()))

    var id by remember { mutableStateOf(0) }

    /*** Extract method to differentiate between private and group tasks ***/
    val onClick: () -> Unit = {
        if(!isGroupTask) {
            /*** Call this function in Coroutine scope to not block the
             *   main thread/UI --> Also show Toast for now ***/
            id += 1
            Toast.makeText(context, "Add Task $id clicked!", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                addTask(id, title.value, description.value, privateTasksViewModel)
            }
        } else {
            /*** For group tasks use Toast as placeholder for now ***/
            Toast.makeText(context, "[GROUP TASK]: clicked!", Toast.LENGTH_SHORT).show()
        }

        /*** End the current activity and return to the previous one ***/
        (context as Activity).finish()
    }

    /*** Organize elements in column ***/
    LazyColumn(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {

        item {
            Text(
                text = "Add a New Task",
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
                onClick = onClick,
                enabled = formIsComplete,
                /*** Set the color to the same as the 'add-task' button (if enabled)
                 *   --> else make it grey ***/
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (formIsComplete) colorResource(R.color.add_task_color)
                        else colorResource(R.color.task_text_color),
                    contentColor = Color.White
                ),
            ) {
                Text("Save Task", fontSize = 25.sp)
            }
        }
    }
}

/*** Use this function for adding new tasks ***/
private fun addTask(id: Int, title: String, description: String, privateTasksViewModel: PrivateTasksViewModel) {
    val newTask = PrivateTaskItem(
        id = id,
        title = title,
        description = description,
        dueDate = LocalDate.now().plusDays(1),
        isActive = false,
        isCompleted = false
    )
    privateTasksViewModel.addTask(newTask)
}
