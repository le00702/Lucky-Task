package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.luckytask.R

@Composable
fun TaskInfoPopup(title: String, text: String, onDismissRequest: () -> Unit) {
    /*** Use custom popup
     *   @param title: The title of the task
     *   @param text: The info text to display
     *   @param onDismissRequest: What happens on dismissal (clicking outside/back...)
     * ***/
    Popup(
        onDismissRequest = onDismissRequest,
        alignment = Alignment.Center
    ) {
        Column (
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                color = colorResource(R.color.task_text_color),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = colorResource(R.color.task_text_color)
            )
        }
    }
}