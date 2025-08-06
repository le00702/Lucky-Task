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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.data.TaskFilter
import com.example.luckytask.data.applyFilters
import com.example.luckytask.model.PrivateTasksViewModel
import com.example.luckytask.model.PrivateTasksViewModelFactory
import com.example.luckytask.sensor.ShakeListener
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.Dice
import com.example.luckytask.ui.theme.elements.Task
import com.example.luckytask.ui.theme.elements.TaskCard
import com.example.luckytask.ui.theme.elements.TaskFilterBar
import java.time.LocalDate
import com.example.luckytask.data.TaskRepository
import com.example.luckytask.ui.theme.elements.EditableTaskCard

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
    val taskRepository = remember { TaskRepository.getInstance() }
    //val tasks by taskRepository.tasks.collectAsState()
    var refreshTrigger by remember { mutableStateOf(0) }

    /*** Get viewModel for private tasks + application context + DB ***/
    val app = context.applicationContext as PrivateTasksApp
    val privateTaskViewModel: PrivateTasksViewModel =
        viewModel(factory = PrivateTasksViewModelFactory(app.database.privateTasksDAO()))
    val privateTasks by privateTaskViewModel.tasks.collectAsState()

    /*** Use mock task for displaying purposes only ***/
    val mockActiveTask = listOf(
        PrivateTaskItem(
            id = -1,
            title = "Mock Active Task",
            description = "This is an active mock task",
            dueDate = LocalDate.now(),
            isActive = true
        )
    )

    /*val mockInactiveTask = listOf(
        PrivateTaskItem(
            id = -2,
            title = "Mock Inactive Task",
            description = "This is an inactive mock task",
            dueDate = LocalDate.now(),
            isActive = false
        )
    )*/
    /*** Use this active-task-list for mocking purposes for now ***/
    var activeTasks = mockActiveTask


    val inactiveTasks = privateTasks.filter { !it.isActive }
    //val tasks = mockInactiveTask + realInactiveTasks


    // Filter State
    var currentFilter by remember { mutableStateOf(TaskFilter()) }
    /*val filteredTasks = remember(inactiveTasks, currentFilter) {
       inactiveTasks.applyFilters(currentFilter)*/
    val filteredTasks = remember(inactiveTasks, currentFilter, refreshTrigger) {
        inactiveTasks.applyFilters(currentFilter)
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
            items(activeTasks) { task ->
                TaskCard(
                    task = task
                )
            }
        }

        // Show filtered tasks with edit functionality
        items(filteredTasks) { taskItem ->
            EditableTaskCard(
                task = taskItem,
                modifier = Modifier,
                onTaskUpdated = { refreshTrigger++ }, // UI refresh for changes
                false
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
                stringResource(R.string.title_my_todos),
                isGroupTask = false
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

        /*** If there are no inactive tasks(=TODOs), display the following message
         *   --> ask user to add a new task they want to do ***/
        if (inactiveTasks.isEmpty()) {
            item {
                Text(
                    "You currently have no TODOs. Click the 'New Task' button to add one!",
                    color = colorResource(R.color.task_text_color),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Show editable inactive tasks from local DB
            items(inactiveTasks) { taskItem ->
                EditableTaskCard(
                    task = taskItem,
                    modifier = Modifier,
                    isGroupTask = false
                )
            }
        }
    }
}