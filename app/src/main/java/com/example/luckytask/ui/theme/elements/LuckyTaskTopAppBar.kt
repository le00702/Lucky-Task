package com.example.luckytask.ui.theme.elements

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp
import com.example.luckytask.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyTaskTopAppBar(
    /*** Use the title for 'naming' the top bar ***/
    title: String,
    color: Color = colorResource(R.color.lilac),
) {
    TopAppBar(
        title = { Text(
            text = title,
            fontSize = 35.sp
        ) },
        colors = TopAppBarColors(
            containerColor = color,
            scrolledContainerColor = color,
            navigationIconContentColor = color,
            titleContentColor = Color.Black,
            actionIconContentColor = color
        )
    )
}