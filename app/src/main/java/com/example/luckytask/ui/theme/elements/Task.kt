package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.R

@Composable
fun Task(
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        color = colorResource(R.color.app_color),
        shape = RoundedCornerShape(10.dp)
    ) {
        /*** Align all elements in a row, to organize them horizontally ***/
        Row(
            modifier = Modifier
                .fillMaxWidth()
                /*** Add some padding, so the text is not that close to the border ***/
                .padding(horizontal = 15.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 25.sp,
                color = colorResource(R.color.task_color),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
            IconButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.info),
                    contentDescription = "Info",
                    tint = colorResource(R.color.task_color)
                )
            }
        }
    }
}