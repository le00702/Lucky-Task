package com.example.luckytask.ui.theme.elements

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.example.luckytask.MockActivity
import com.example.luckytask.R


/*** 'out' is used to accept any data type/class that extends ComponentActivity ***/
@Composable
fun MockButton(context: Context, activity: Class<out ComponentActivity>, text: String) {
    Button(
        onClick = {
            val intent = Intent(context, activity)
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.add_task_color), // Background color
            contentColor = colorResource(R.color.white) // Text color
        )
    ) {
        Text(
            text = text,
            fontSize = 30.sp
        )
    }
}