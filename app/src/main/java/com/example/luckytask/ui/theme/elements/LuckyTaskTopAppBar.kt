package com.example.luckytask.ui.theme.elements

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyTaskTopAppBar(
    /*** Use the title for 'naming' the top bar ***/
    title: String,
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarColors(
            containerColor = Color.LightGray,
            scrolledContainerColor = Color.LightGray,
            navigationIconContentColor = Color.LightGray,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.LightGray
        )
    )
}