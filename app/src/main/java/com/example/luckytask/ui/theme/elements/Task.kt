package com.example.luckytask.ui.theme.elements

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.R

@Composable
        /***
         * @param title: The short description of the task --> longer description will be provided via info icon
         * @param active: For now, describes active state as Boolean --> does not matter whether
         * its a private or group task
         * @param roommate: For now, refers to whether this task was drawn by any roommate ***/
fun Task(
    title: String,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    roommate: Boolean = false,
    context: Context,
) {
    /*** Extract this variable for deciding whether to display the detailed info ***/
    var showInfo by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        /*** Choose color based on whether this is an active task, a (not yet drawn)
         *   To-Do, or a task that was drawn by a roommate ***/
        color =
            if (roommate) colorResource(R.color.roommate_task_color)
            else if (active) colorResource(R.color.active_task_color)
            else colorResource(R.color.app_color),
        shape = RoundedCornerShape(12.dp)
    ) {
        /*** Align all elements in a row, to organize them horizontally ***/
        Row(
            modifier = Modifier
                .fillMaxWidth()
                /*** Add some padding, so the text is not that close to the border ***/
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                color = colorResource(R.color.task_text_color),
                maxLines = 1,
                /*** If text is too long, shorten it by using '...' at the end ***/
                overflow = TextOverflow.Ellipsis,
                /*** Use weight to allow the text element to take up part of the row
                 *   --> but not so much, that the icon below does not fit anymore
                 *   --> Icon uses 25.dp, therefore text can have the rest (1 "unit")
                 *   --> units are distributed amongst child elements of this row ***/
                modifier = Modifier
                    .weight(1f)
            )
            Box() {
                IconButton(onClick = {
                    /*** When the detailed info is shown, revert the value
                     *   --> does not need to be shown anymore (for now) ***/
                    showInfo = !showInfo
                }, Modifier.size(25.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.info),
                        contentDescription = "Info",
                        tint = colorResource(R.color.task_text_color)
                    )
                }
            }

            /*** Display the detailed info of the task ***/
            if (showInfo) {
                TaskInfoPopup(
                    title, "Info icon clicked!", onDismissRequest = { showInfo = false },
                )
            }
        }
    }
}