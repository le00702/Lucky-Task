package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.example.luckytask.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuckyTaskTopAppBar(
    /*** Use the title for 'naming' the top bar ***/
    title: String,
    backgroundColor: Color = colorResource(R.color.lilac),
    contentColor: Color = colorResource(R.color.header_color),

    ) {
    TopAppBar(
        title = {
            Box(
                /*** This is necessary, otherwise the 'Box' only is as big as the text ***/
                modifier = Modifier
                    .fillMaxWidth(),
                /*** Align the 'Box' content (aka the text) centrally ***/
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 35.sp
                )
            }
        },
        colors = TopAppBarColors(
            containerColor = backgroundColor,
            scrolledContainerColor = backgroundColor,
            navigationIconContentColor = backgroundColor,
            titleContentColor = contentColor,
            actionIconContentColor = backgroundColor
        )
    )
}