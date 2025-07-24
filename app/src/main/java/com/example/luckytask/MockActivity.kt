package com.example.luckytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.ProgressCircle
import com.example.luckytask.ui.theme.elements.Task

/*** Pass the name of the activity to display it correctly on the hamburger menu ***/
private val ACTIVITY_NAME = "MockActivity"

class MockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                AppWithDrawer(
                    currentActivityName = ACTIVITY_NAME,
                    topBarTitle = stringResource(R.string.title_mock)
                ) {
                    MockApp(modifier = Modifier.padding(20.dp))
                }
            }
        }
    }
}

@Composable
fun MockApp(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    /*** Organize elements in column ***/
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Task(
            "This is a task",
            modifier = Modifier,
            context = context
        )
        AddTaskButton(
            modifier = Modifier,
            context,
            ACTIVITY_NAME,
            stringResource(R.string.title_mock)
        )
        ProgressCircle(7, 10, colorResource(R.color.purple_500))
        Spacer(modifier = Modifier.padding(10.dp))
        ProgressCircle(6, 27, colorResource(R.color.teal_700))
    }
}