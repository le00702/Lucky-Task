package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.luckytask.firestore.GroupDAO
import com.example.luckytask.firestore.TodoDAO
import com.example.luckytask.firestore.UserDAO
import com.example.luckytask.firestore.roundedShape

@Composable
fun NewGroupMenu(
    modifier:Modifier = Modifier,
    setVisibility:(vis:Boolean) -> Unit,
    addGroup:(groupName: String) -> Unit
    ){
    var name by remember{mutableStateOf("")}
    Box(modifier = modifier.fillMaxWidth().fillMaxHeight(fraction = 0.5f).background(Color.DarkGray.copy(alpha = 0.8f), shape = roundedShape(10.dp)),
        contentAlignment = Alignment.Center){
        Column(modifier = modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
            Text(modifier = Modifier.padding(5.dp),text = "New Group", fontSize = 30.sp)
            Spacer(modifier.padding(20.dp))
            TextField(value = name, onValueChange = {name = it}, label ={Text("Group Name")} )
            Spacer(modifier.padding(15.dp))
            Spacer(modifier.padding(30.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)){
                Button(enabled = (!name.isEmpty()),onClick = {
                    addGroup(name)
                    name = ""
                    setVisibility(false)}){
                    Text("Create")
                }
                Button(onClick = {setVisibility(false)}){
                    name = ""
                    Text("Cancel")
                }
            }
        }
    }


}