package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.size
import androidx. compose. ui. graphics. Color
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun ProgressCircle(done: Int, total: Int, color: Color) {
    CircularProgressIndicator(
        progress = {
            (done.toFloat() / total.toFloat())
        },
        /*** Size of the progress indicator ***/
        modifier = Modifier.size(200.dp),
        /*** Color of the progress indicator***/
        color = color,
        /*** The width of the progress indicator + track ***/
        strokeWidth = 20.dp,
        /*** This controls how big the gap between track and progress indicator is ****/
        gapSize = 0.dp,
        /*** This controls the shape of the progress indicator ends (round, square, etc.) ***/
        strokeCap = StrokeCap.Square
    )
}