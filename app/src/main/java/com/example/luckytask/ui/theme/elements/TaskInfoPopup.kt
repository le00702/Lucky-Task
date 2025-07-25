package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.luckytask.R

@Composable
fun TaskInfoPopup(title: String, text: String, onDismissRequest: () -> Unit, parentColor: Color) {
    /*** Use custom popup
     *   @param title: The title of the task
     *   @param text: The info text to display
     *   @param onDismissRequest: What happens on dismissal (clicking outside/back...)
     *   @param parentColor: The color of the parent element/task
     * ***/

    Popup(
        onDismissRequest = onDismissRequest,
        alignment = Alignment.Center
    ) {
        /*** Display info icon, task title, task description in a column
         *   --> based on the task color, choose background color ***/
        Column(
            modifier = Modifier
                /*** Add space between border of popup and screen edge ***/
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(
                    getPopUpColor(parentColor), RoundedCornerShape(12.dp)
                )
                /*** Add space between text and border of popup ***/
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*** Add the info icon on top but much smaller and center it ***/
            Icon(
                painter = painterResource(R.drawable.info),
                contentDescription = "Info",
                tint = colorResource(R.color.task_text_color),
                modifier = Modifier
                    .size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                color = colorResource(R.color.task_text_color),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = colorResource(R.color.task_text_color),
                textAlign = TextAlign.Center
            )
        }
    }
}


/*** Take the parent color and adjust its RGB values for the info popup
 *   --> DO NOT CHANGE OPACITY
 *   --> make the color lighter by multiplying by factor ***/
private fun getPopUpColor(color: Color): Color {
    val factor = 1.4f
    return Color(
        red = color.red * factor,
        green = color.green * factor,
        blue = color.blue * factor,
        alpha = color.alpha
    )
}