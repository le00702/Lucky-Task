package com.example.luckytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.LuckyTaskTopAppBar
import com.example.luckytask.ui.theme.elements.ProgressCircle
import com.example.luckytask.ui.theme.elements.Task

class MainActivity : ComponentActivity() {
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
                    LuckyTaskApp(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LuckyTaskApp(name: String, modifier: Modifier = Modifier) {
    /*Text(
        text = "Hello $name!",
        modifier = modifier
    )*/

    /*** Organize elements in column ***/
    Column(modifier = modifier.padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Task(
            "This is a task",
            modifier = modifier
        )
        AddTaskButton(
            modifier = modifier
        )
        ProgressCircle(7, 10, colorResource(R.color.purple_500))
    }
}

@Preview(showBackground = true)
@Composable
fun LuckyTaskAppPreview() {
    LuckyTaskTheme {
        LuckyTaskApp("Android")
    }
}