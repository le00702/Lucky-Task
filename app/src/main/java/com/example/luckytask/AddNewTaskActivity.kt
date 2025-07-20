package com.example.luckytask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.LuckyTaskTopAppBar
import com.example.luckytask.ui.theme.elements.Task

class AddNewTaskActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    LuckyTaskTopAppBar(
                        stringResource(id = R.string.app_name)
                    )
                }) { innerPadding ->
                    AddNewTaskScreen(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun AddNewTaskScreen(modifier: Modifier = Modifier) {

    val HEADER_SIZE = 30.sp

    /*** Use this active-task-list for mocking purposes for now ***/
    var activeTasks = listOf<String>()

    /*** ENABLE WHEN CHECKING FOR ACTIVE TASKS DISPLAY ***/
    activeTasks = listOf<String>("Task 1", "Task 2", "Task 3")

    /*** Organize elements in column ***/
    LazyColumn(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {

        item {
            Text(
                text = "My Active Tasks",
                fontSize = HEADER_SIZE
            )
        }

        /*** If there are no active tasks, display the following message
         *   --> user is asked to roll dice
         *   --> align text centered ***/

        /*** If there are no active tasks, display the following message
         *   --> user is asked to roll dice
         *   --> align text centered ***/
        if (activeTasks.isEmpty()) {
            item {
                Text(
                    "You currently have no active tasks. Roll the dice to start a task!",
                    color = colorResource(R.color.task_text_color),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            /*** If there ARE active tasks, display them all ***/
            /*** If there ARE active tasks, display them all ***/
            items(activeTasks.size) { index ->
                Task(
                    title = activeTasks[index],
                    modifier = Modifier,
                    active = true
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            Spacer(modifier = Modifier.height(50.dp))
        }

        item {
            Text(
                text = "My TODO List",
                fontSize = HEADER_SIZE
            )
        }

        item {
            AddTaskButton(
                modifier = Modifier
            )
        }

        item {
            Task(
                "This is a TODO item TEST LONG LINE",
                modifier = Modifier
            )
        }
    }
}
