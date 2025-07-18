package com.example.luckytask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.sensor.ShakeListener
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.Dice
import com.example.luckytask.ui.theme.elements.LuckyTaskTopAppBar
import com.example.luckytask.ui.theme.elements.Task

class TasksActivity : ComponentActivity() {
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
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    LuckyTaskTopAppBar(
                        stringResource(id = R.string.app_name)
                    )
                }) { innerPadding ->
                    TasksScreen(
                        modifier = Modifier.padding(innerPadding),
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

    /*** Organize elements in column ***/
    LazyColumn(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        item {
            Text(
                text = "My Active Tasks",
                fontSize = HEADER_SIZE
            )
        }

        item {
            Task(
                "This is a task",
                modifier = Modifier
            )
        }

        item {
            Dice(
                modifier = modifier,

                /*** Pass value of triggerAnimation to Dice for the actual animation ***/
                triggerAnimation = triggerAnimation
            )
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