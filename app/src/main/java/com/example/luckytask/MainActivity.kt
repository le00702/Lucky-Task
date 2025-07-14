package com.example.luckytask

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.LuckyTaskTopAppBar

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
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LuckyTaskApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                val intent = Intent(context, MockActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.add_task_color), // Background color
                contentColor = colorResource(R.color.white) // Text color
            )
        ) {
            Text(
                text = "Go to MockActivity",
                fontSize = 30.sp
            )
        }
        Button(
            onClick = {
                val intent = Intent(context, MockActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.add_task_color), // Background color
                contentColor = colorResource(R.color.white) // Text color
            )
        ) {
            Text(
                text = "Go to Dice",
                fontSize = 30.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LuckyTaskAppPreview() {
    LuckyTaskTheme {
        LuckyTaskApp()
    }
}