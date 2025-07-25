package com.example.luckytask.ui.theme.elements

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable

@Composable
fun TaskInfoPopup(context: Context, text: String) {
    /*** Use this Toast as a preliminary function for clicking on the
     *   info icon ***/
    Toast.makeText(
        context,
        text,
        Toast.LENGTH_SHORT
    ).show()
}