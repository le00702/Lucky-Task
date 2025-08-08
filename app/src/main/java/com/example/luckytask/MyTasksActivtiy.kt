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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.data.TaskFilter
import com.example.luckytask.data.applyFilters
import com.example.luckytask.model.PrivateTasksViewModel
import com.example.luckytask.model.PrivateTasksViewModelFactory
import com.example.luckytask.sensor.ShakeListener
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.Dice
import com.example.luckytask.ui.theme.elements.TaskCard
import com.example.luckytask.ui.theme.elements.TaskFilterBar
import com.example.luckytask.ui.theme.elements.EditableTaskCard

/*** Pass the name of the activity to display it correctly on the hamburger menu ***/
private val ACTIVITY_NAME = "MyTasksActivity"

class MyTasksActivity : ComponentActivity() {
    private lateinit var shakeListener: ShakeListener
    private lateinit var privateTaskViewModel: PrivateTasksViewModel
    private val TAG = "[SENSOR]"

    /*** Use this variable to keep track of animation ***/
    private var triggerAnimation = mutableStateOf(false)

    /*** Use this variable to decide on text when drawing a task/
     *   rolling the dice ***/
    private var drawText = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*** Get viewModel for private tasks based on local DB ***/
        val app = applicationContext as PrivateTasksApp
        privateTaskViewModel =
            PrivateTasksViewModelFactory(app.database.privateTasksDAO()).create(PrivateTasksViewModel::class.java)

        /*** Pass the code for onShake() for shakeListener --> trigger animation ***/
        shakeListener = ShakeListener(this) {
            Log.d(TAG, "Shake detected!")
            triggerAnimation.value = true

            /*** Fetch a random task from the still inactive ones/TODOs ***/
            val drawnTask = privateTaskViewModel.drawRandomTask()

            /*** Assign text for task drawing based on whether
             *   --> there are tasks to draw: then, update the drawn task and set it to active
             *   --> or not: then, display a message stating this
             ***/
            if(drawnTask == null ){
                drawText.value = "There are no TODOs to draw from!"
            }else{
                drawText.value = "You have drawn the following TODO: ${drawnTask.title}"
                val updatedTask = drawnTask.copy(
                    isActive = true
                )
                privateTaskViewModel.updateTask(updatedTask)
                Log.d("ACTIVE TASK", "Task ${updatedTask.title} is active: ${updatedTask.isActive}")
            }
            Toast.makeText(this, drawText.value, Toast.LENGTH_SHORT).show()
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
                        triggerAnimation,
                        privateTaskViewModel,
                        drawText
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
fun TasksScreen(modifier: Modifier = Modifier, triggerAnimation: MutableState<Boolean>, privateTasksViewModel: PrivateTasksViewModel, drawText:  MutableState<String>) {

    val HEADER_SIZE = 30.sp
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(0) }
    val privateTasks = privateTasksViewModel.tasks.collectAsState().value


    /*** Fetch active local tasks ***/
    val activeTasks = privateTasks.filter { it.isActive }

    /*** Fetch inactive local tasks ***/
    val inactiveTasks = privateTasks.filter { !it.isActive }


    // Filter State
    var currentFilter by remember { mutableStateOf(TaskFilter()) }

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
        /*item {
            TaskFilterBar(
                currentFilter = currentFilter,
                onFilterChange = { currentFilter = it }
            )
        }*/

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
            /*** If there ARE active tasks, display them all
             *   --> when double clicked mark as completed***/
            items(activeTasks) { task ->
                TaskCard(
                    task = task,
                    isMine = true,
                    setDone = {
                        val updatedTask = task.copy(
                            isCompleted = true
                        )
                        privateTasksViewModel.updateTask(updatedTask)
                    },
                    deleteTask = {
                        privateTasksViewModel.deleteTask(task)                    }
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