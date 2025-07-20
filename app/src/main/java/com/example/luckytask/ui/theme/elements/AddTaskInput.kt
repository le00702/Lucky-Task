package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
/*** Pass 'input' as MutableState, so we can modify the value here
 *   for updating the text --> not possible when passed as String only,
 *   as it cannot be reassigned (=val) ***/
fun AddTaskInput(input: MutableState<String>, labelText: String, placeholderText: String) {
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
            value = input.value,
            onValueChange = { input.value = it },
            textStyle = TextStyle(fontSize = INPUT_FONT_SIZE),
            placeholder = {
                Text(
                    placeholderText,
                    fontSize = INPUT_FONT_SIZE
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}