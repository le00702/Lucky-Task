package com.example.luckytask.ui.theme.elements

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.AddNewTaskActivity
import com.example.luckytask.R

@Composable
fun AddTaskButton(
    modifier: Modifier = Modifier,
    context: Context,
    /*** Add these to remember which activity we came from and what to display
     *   @param: parentActivity --> which activity we came from
     *   @param: topBarTitle --> which title to display on the top bar
     *   @param: isGroupTask --> pass this info for the "add new task" form ***/
    parentActivity: String,
    topBarTitle: String,
    isGroupTask: Boolean = false,
) {
    Surface(
        modifier = modifier
            .padding(8.dp),
        color = colorResource(R.color.add_task_color),
        shape = RoundedCornerShape(30.dp),
        onClick = {
            val intent = Intent(context, AddNewTaskActivity::class.java)
            intent.putExtra("parentActivity", parentActivity)
            intent.putExtra("topBarTitle", topBarTitle)
            intent.putExtra("isGroupTask", isGroupTask)
            context.startActivity(intent)
        }
    ) {
        /*** Align all elements in a row, to organize them horizontally ***/
        Row(
            modifier = Modifier
                /*** Add some padding, so the text is not that close to the border ***/
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            /*** Fix icon size to avoid resizing issues ***/
            IconButton(onClick = {}, Modifier.size(30.dp)) {
                Icon(
                    painter = painterResource(R.drawable.plus),
                    contentDescription = "Add new task",
                    tint = colorResource(R.color.white)
                )
            }
            /*** Add this to slightly increase space between text and plus-icon ***/
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "New Task",
                fontSize = 25.sp,
                color = colorResource(R.color.white)
            )
        }
    }
}