package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddTaskInput(labelText: String, placeholderText: String) {
    val LABEL_FONT_SIZE = 25.sp
    val INPUT_FONT_SIZE = 20.sp

    /*** Use this for grouping label + text input element for adding new tasks ***/
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = labelText,
            fontSize = LABEL_FONT_SIZE,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = "",
            placeholder = {
                Text(
                    placeholderText,
                    fontSize = INPUT_FONT_SIZE
                )
            },
            onValueChange = {},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}