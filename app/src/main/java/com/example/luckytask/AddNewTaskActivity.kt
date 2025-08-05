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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.data.TaskItem
import com.example.luckytask.data.TaskRepository
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskInput
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.DatePickerField
import java.time.LocalDate
import java.util.UUID

class AddNewTaskActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                val parentActivity = intent.getStringExtra("parentActivity").toString()
                val topBarTitle = intent.getStringExtra("topBarTitle").toString()

                AppWithDrawer(
                    currentActivityName = parentActivity,
                    topBarTitle = topBarTitle
                ) {
                    AddNewTaskScreen(
                        modifier = Modifier.padding(20.dp),
                        parentActivity = parentActivity,
                        onTaskSaved = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewTaskScreen(
    modifier: Modifier = Modifier,
    parentActivity: String,
    onTaskSaved: () -> Unit
) {
    val HEADER_SIZE = 30.sp
    val context = LocalContext.current
    val taskRepository = remember { TaskRepository.getInstance() }

    /*** Use '=' to assign as MutableState instead of String (by using 'by') ***/
    var title = remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }
    var dueDate = remember { mutableStateOf<LocalDate?>(null) }
    var assignToMe = remember { mutableStateOf(true) }

    // Determine if this is a group task based on parent activity
    val isGroupTask = parentActivity == "GroupTasksActivity"

    /*** Organize elements in column ***/
    // Form validation
    val formIsComplete = title.value.isNotBlank() && description.value.isNotBlank()

    // Error handling state
    var isLoading = remember { mutableStateOf(false) }
    var errorMessage = remember { mutableStateOf<String?>(null) }

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
            AddTaskInput(
                title,
                "Task Title",
                "Enter a task title",
                isTitle = true
            )
        }

        item {
            AddTaskInput(
                description,
                "Task Description",
                "Enter a task description"
            )
        }

        item {
            DatePickerField(
                selectedDate = dueDate,
                label = "Due Date (Optional)",
                placeholder = "Select a due date",
                isRequired = false
            )
        }

        errorMessage.value?.let { error ->
            item {
                Text(
                    text = error,
                    color = colorResource(R.color.error_color),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    // Error handling with try-catch
                    isLoading.value = true
                    errorMessage.value = null

                    try {
                        val newTask = TaskItem(
                            id = UUID.randomUUID().toString(),
                            title = title.value.trim(),
                            description = description.value.trim(),
                            assignee = if (isGroupTask) null else "Me",
                            dueDate = dueDate.value,
                            isActive = false,
                            isCompleted = false,
                            isGroupTask = isGroupTask
                        )

                        val success = taskRepository.addTask(newTask)

                        if (success) {
                            Toast.makeText(
                                context,
                                "Task '${newTask.title}' added successfully!",
                                Toast.LENGTH_SHORT
                            ).show()

                            Log.d("AddNewTaskActivity", "Task added: ${newTask.title}")
                            onTaskSaved()
                        } else {
                            errorMessage.value = "Failed to add task. Please try again."
                            Log.e("AddNewTaskActivity", "Failed to add task")
                        }
                    } catch (e: Exception) {
                        errorMessage.value = "An error occurred: ${e.message}"
                        Log.e("AddNewTaskActivity", "Error adding task", e)
                    } finally {
                        isLoading.value = false
                    }
                },
                enabled = formIsComplete && !isLoading.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if(formIsComplete && !isLoading.value)
                            colorResource(R.color.add_task_color)
                        else
                            colorResource(R.color.task_text_color),
                    contentColor = Color.White
                ),
            ) {
                Text(
                    text = if (isLoading.value) "Saving..." else "Save Task",
                    fontSize = 25.sp
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = onTaskSaved,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
            ) {
                Text("Cancel", fontSize = 20.sp)
            }
        }
    }
}