package com.example.luckytask

import android.os.Bundle
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskInput
import com.example.luckytask.ui.theme.elements.AppWithDrawer

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
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewTaskScreen(modifier: Modifier = Modifier) {

    val HEADER_SIZE = 30.sp

    /*** Use '=' to assign as MutableState instead of String (by using 'by') ***/
    var title = remember { mutableStateOf("") }
    var description = remember { mutableStateOf("") }
    var formIsComplete = title.value.isNotBlank() && description.value.isNotBlank()

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
            AddTaskInput(title, "Task Title", "Enter a task title", isTitle = true,)
        }

        item {
            AddTaskInput(description, "Task Description", "Enter a task description")
        }
        item {
            Button(
                onClick = {},
                enabled = formIsComplete,
                /*** Set the color to the same as the 'add-task' button (if enabled)
                 *   --> else make it grey ***/
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if(formIsComplete) colorResource(R.color.add_task_color)
                        else colorResource(R.color.task_text_color),
                    contentColor = Color.White
                ),
            ) {
                Text("Save Task", fontSize = 25.sp)
            }
        }
    }
}