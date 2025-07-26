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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.sensor.ShakeListener
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.Dice
import com.example.luckytask.ui.theme.elements.Task

/*** Pass the name of the activity to display it correctly on the hamburger menu ***/
private val ACTIVITY_NAME = "GroupTasksActivity"

class GroupTasksActivity : ComponentActivity() {
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
                    topBarTitle = stringResource(R.string.title_group_todos)
                ) {
                    GroupTasksScreen(
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
fun GroupTasksScreen(modifier: Modifier = Modifier, triggerAnimation: MutableState<Boolean>) {

    val HEADER_SIZE = 30.sp
    val context = LocalContext.current

    /*** Use this active-task-list for mocking purposes for now ***/
    var activeTasks = listOf<String>()

    /*** ENABLE WHEN CHECKING FOR ACTIVE TASKS DISPLAY ***/
    activeTasks = listOf<String>("Task 1", "Task 2", "Task 3")

    /*** Use this roommate-task-list for mocking purposes for now ***/
    var roommateTasks = listOf<String>()

    /*** ENABLE WHEN CHECKING FOR ROOMMATE TASKS DISPLAY ***/
    roommateTasks = listOf<String>("RM Task 1", "RM Task 2", "RM Task 3")

    val onInfoIconClick = { Toast.makeText(context, "Clicked info!", Toast.LENGTH_SHORT).show() }


    /*** Organize elements in column ***/
    LazyColumn(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {

        item {
            Text(
                text = "My Group Tasks",
                fontSize = HEADER_SIZE
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
                text = "My Roommates' Group Tasks",
                fontSize = HEADER_SIZE,
                textAlign = TextAlign.Center,
                /*** Use this to add space between the lines ***/
                lineHeight = 30.sp
            )
        }

        /*** If there are no active tasks, display the following message
         *   --> user is asked to roll dice
         *   --> align text centered ***/
        if (roommateTasks.isEmpty()) {
            item {
                Text(
                    "Your roommates haven’t drawn a task yet.",
                    color = colorResource(R.color.task_text_color),
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            /*** If there ARE roommate tasks, display them all ***/
            items(roommateTasks.size) { index ->
                Task(
                    title = roommateTasks[index],
                    modifier = Modifier,
                    active = true,
                    roommate = true,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            Text(
                text = "Group TODO List",
                fontSize = HEADER_SIZE
            )
        }

        item {
            AddTaskButton(
                modifier = Modifier,
                context,
                ACTIVITY_NAME,
                stringResource(R.string.title_group_todos)
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