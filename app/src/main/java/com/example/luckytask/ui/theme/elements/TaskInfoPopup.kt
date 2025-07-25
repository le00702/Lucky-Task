package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.luckytask.R

@Composable
fun TaskInfoPopup(text: String, onDismissRequest: () -> Unit) {
    /*** Use custom popup
     *   @param onDismissRequest: what happens on dismissal (clicking outside/back...)
     *   @param text: The info text to display
     * ***/
    Popup(
        onDismissRequest = onDismissRequest,
        alignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = text,
                color = colorResource(R.color.task_text_color)
            )
        }
    }
}