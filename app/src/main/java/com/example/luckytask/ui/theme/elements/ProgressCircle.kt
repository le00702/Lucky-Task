package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.size
import androidx. compose. ui. graphics. Color
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressCircle(done: Int, total: Int, color: Color) {
    CircularProgressIndicator(
        progress = {
            (done.toFloat() / total.toFloat())
        },
        modifier = Modifier.size(200.dp),
        color = color
    )
}