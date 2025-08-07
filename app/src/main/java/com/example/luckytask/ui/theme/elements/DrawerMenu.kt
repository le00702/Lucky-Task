package com.example.luckytask.ui.theme.elements

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.MainActivity
import com.example.luckytask.MockActivity
import com.example.luckytask.MockDiceActivity
import com.example.luckytask.MyTasksActivity
import com.example.luckytask.GroupTasksActivity
import com.example.luckytask.R

data class MenuItem(
    val title: String,
    val icon: Any,
    val activityClass: Class<*>
)

@Composable
fun DrawerContent(
    currentActivity: String,
    onCloseDrawer: () -> Unit
) {
    val context = LocalContext.current

    val menuItems = listOf(
        MenuItem("Home Screen", Icons.Default.Home, MainActivity::class.java),
        MenuItem(stringResource(R.string.title_mock), R.drawable.info, MockActivity::class.java),
        MenuItem("Dice Game", R.drawable.dice_3, MockDiceActivity::class.java),
        MenuItem(stringResource(R.string.title_my_todos), Icons.Default.Person, MyTasksActivity::class.java),
        MenuItem(stringResource(R.string.title_group_todos), R.drawable.user_account, GroupTasksActivity::class.java),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.app_color))
            .padding(16.dp)
    ) {
        // Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            color = colorResource(R.color.add_task_color),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Lucky Task",
                fontSize = 24.sp,
                color = colorResource(R.color.white),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Menu Items
        LazyColumn {
            items(menuItems) { item ->
                DrawerMenuItem(
                    item = item,
                    isSelected = currentActivity == item.activityClass.simpleName,
                    onClick = {
                        navigateToActivity(context, item.activityClass)
                        onCloseDrawer()
                    }
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    item: MenuItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        colorResource(R.color.add_task_color)
    } else {
        colorResource(R.color.white)
    }

    val textColor = if (isSelected) {
        colorResource(R.color.white)
    } else {
        colorResource(R.color.black)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (val icon = item.icon) {
                is ImageVector -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = item.title,
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                is Int -> {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = item.title,
                        tint = textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = item.title,
                color = textColor,
                fontSize = 18.sp
            )
        }
    }
}

private fun navigateToActivity(context: Context, activityClass: Class<*>) {
    if (context.javaClass != activityClass) {
        val intent = Intent(context, activityClass)
        context.startActivity(intent)
    }
}