package com.example.luckytask.ui.theme.elements

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.luckytask.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyTaskTopAppBar(
    /*** Use the title for 'naming' the top bar ***/
    title: String,
    color: Color = colorResource(R.color.lilac)
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarColors(
            containerColor = color,
            scrolledContainerColor = color,
            navigationIconContentColor = color,
            titleContentColor = Color.Black,
            actionIconContentColor = color
        )
    )
}