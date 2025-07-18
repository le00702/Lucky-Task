package com.example.luckytask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.LuckyTaskTopAppBar
import com.example.luckytask.ui.theme.elements.MockButton

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
        MockButton(context, MockActivity::class.java, "Go to MockActivity")
        MockButton(context, MockDiceActivity::class.java, "Go to Dice")
        MockButton(context, MyTasksActivity::class.java, "Go to my Tasks")
        MockButton(context, MyTasksActivity::class.java, "Go to Group Tasks")
    }
}

@Preview(showBackground = true)
@Composable
fun LuckyTaskAppPreview() {
    LuckyTaskTheme {
        LuckyTaskApp()
    }
}