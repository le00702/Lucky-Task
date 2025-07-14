package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressCircle(done: Int, total: Int, color: Color) {
    val progress = if (done <= total) (done.toFloat() / total.toFloat()) else  0f
    Box(
        /*** Size of the progress indicator ***/
        modifier = Modifier.size(200.dp),
        /*** Ensure the text is displayed in the center of the circle! ***/
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            /*** Color of the progress indicator ***/
            color = color,
            /*** The width of the progress indicator + track ***/
            strokeWidth = 20.dp,
            /*** This controls how big the gap between track and progress indicator is ****/
            gapSize = 0.dp,
            /*** This controls the shape of the progress indicator ends (round, square, etc.) ***/
            strokeCap = StrokeCap.Square
        )
        Text(
            text = "${(progress * 100)}%",
            color = color,
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )
    }
}