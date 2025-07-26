package com.example.luckytask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.data.TaskFilter
import com.example.luckytask.data.TaskItem
import com.example.luckytask.data.applyFilters
import com.example.luckytask.sensor.ShakeListener
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.Dice
import com.example.luckytask.ui.theme.elements.Task
import com.example.luckytask.ui.theme.elements.TaskCard
import com.example.luckytask.ui.theme.elements.TaskFilterBar
import java.time.LocalDate

/*** Pass the name of the activity to display it correctly on the hamburger menu ***/
private val ACTIVITY_NAME = "MyTasksActivity"

class MyTasksActivity : ComponentActivity() {
    private lateinit var shakeListener: ShakeListener
    private val TAG = "[SENSOR]"

    /*** Use this variable to keep track of animation ***/
    private var triggerAnimation = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*** Pass the code for onShake() for shakeListener --> trigger animation ***/
        shakeListener = ShakeListener(this) {
            Log.d(TAG, "Shake detected!")
            triggerAnimation.value = true
        }

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                AppWithDrawer(
                    currentActivityName = ACTIVITY_NAME,
                    topBarTitle = stringResource(R.string.title_my_todos)
                ) {
                    TasksScreen(
                        modifier = Modifier.padding(20.dp),
                        triggerAnimation
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeListener.start()
    }

    override fun onPause() {
        super.onPause()
        shakeListener.stop()
    }
}

@Composable
fun TasksScreen(modifier: Modifier = Modifier, triggerAnimation: MutableState<Boolean>) {

    val HEADER_SIZE = 30.sp
    val context = LocalContext.current

    /*** Use this active-task-list for mocking purposes for now ***/
    var activeTasks = listOf<String>()

    /*** ENABLE WHEN CHECKING FOR ACTIVE TASKS DISPLAY ***/
    activeTasks = listOf<String>("Task 1", "Task 2", "Task 3")

    // Mock-Data
    val mockTaskItems = remember {
        listOf(
            TaskItem("1", "Clean Kitchen", "Wash dishes", "Me", LocalDate.now(), true),
            TaskItem("2", "Buy Groceries", "", null, LocalDate.now().plusDays(1)),
            TaskItem("3", "Study", "", "Me", LocalDate.now().plusDays(7)),
            TaskItem("4", "Meeting", "", "John", LocalDate.now(), true),
        )
    }

    // Filter State
    var currentFilter by remember { mutableStateOf(TaskFilter()) }
    val filteredTasks = remember(mockTaskItems, currentFilter) {
        mockTaskItems.applyFilters(currentFilter)
    }

    /*** Organize elements in column ***/
    LazyColumn(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = "My Active Tasks",
                fontSize = HEADER_SIZE
            )
        }

        // Filter Bar
        item {
            TaskFilterBar(
                currentFilter = currentFilter,
                onFilterChange = { currentFilter = it }
            )
        }

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
            items(activeTasks.size) { index ->
                Task(
                    title = activeTasks[index],
                    modifier = Modifier,
                    active = true,
                )
            }
        }

        // Show filtered tasks
        items(filteredTasks) { taskItem ->
            TaskCard(
                task = taskItem,
                modifier = Modifier
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            Dice(
                modifier = modifier,

                /*** Pass value of triggerAnimation to Dice for the actual animation ***/
                triggerAnimation = triggerAnimation,
                isMock = false
            )
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
                modifier = Modifier,
                context,
                ACTIVITY_NAME,
                stringResource(R.string.title_my_todos)
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