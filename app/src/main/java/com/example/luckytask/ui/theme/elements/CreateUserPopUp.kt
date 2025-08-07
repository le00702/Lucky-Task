package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewUserMenu(
    modifier:Modifier = Modifier,
    setUser:(userName: String) -> Unit,
    setVisibility:(Boolean) -> Unit

){
    var input by remember {mutableStateOf("")}

    val nameRegex = Regex("^[a-zA-Z0-9 _-]{1,16}$")

    Box(modifier = modifier.fillMaxWidth().background(Color.DarkGray.copy(alpha = 0.97f), shape = RoundedCornerShape(10.dp)).padding(15.dp),
        contentAlignment = Alignment.Center){
        Column(modifier = modifier, verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
            Text(modifier = Modifier.padding(5.dp),text = "Set Username", fontSize = 30.sp)

            TextField(
                value = input,
                onValueChange = { input = it},
                label ={Text("User Name")},
                placeholder = {Text("John Doe")},
            )
            Spacer(modifier.padding(15.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)){
                Button(enabled = (!input.isEmpty() && input.matches(nameRegex)),
                    onClick = {
                        setUser(input)
                        input = ""
                        setVisibility(false)
                    }){
                    Text("Ok")
                }
                Spacer(modifier.padding(10.dp))
                Button(onClick = {
                    setVisibility(false)
                    input = ""
                }
                ){
                    Text("Cancel")
                }
            }
        }
    }
}