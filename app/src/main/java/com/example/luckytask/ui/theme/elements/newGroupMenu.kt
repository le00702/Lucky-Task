package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luckytask.firestore.roundedShape
import java.util.InvalidPropertiesFormatException


@Composable
fun RadioButtonSelection(modifier:Modifier = Modifier,
                         options: List<String> = listOf("Option 1", "Option 2"),
                         selected: (Int) -> Unit = {it},
                         onChange: () -> Unit  = {}){
    if(options.isEmpty() || options.size > 2) throw InvalidPropertiesFormatException("Invalid number of options")

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[0]) }

    Row(modifier.selectableGroup(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically) {
        options.forEach { text ->
            Column(
                Modifier
                    .height(56.dp)
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { onOptionSelected(text); selected(options.indexOf(text)); onChange() },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

const val CREATE_GROUP =  0
const val JOIN_GROUP = 1
/**
 * Add group to User:
 * Allow user to either create new menu or join an existing one
 * Join: enter key, check if key exists -> load group data
 * create: enter group name, generate key (in background: check if key exists -> regenerate key), create group on Could and display join key
 */
@Composable
fun NewGroupMenu(
    modifier:Modifier = Modifier,
    setVisibility:(Boolean) -> Unit,
    addGroup:(groupName: String) -> Unit,
    joinGroup:(groupKey:String) -> Unit
    ){
    var input by remember {mutableStateOf("")}
    var selection by remember { mutableIntStateOf(CREATE_GROUP) }

    if (selection != CREATE_GROUP && selection != JOIN_GROUP) throw IllegalStateException("Selection Not defined")

    val nameRegex = Regex("^[a-zA-Z0-9 _-]{2,12}$") //Alphanumeric, space, - and _
    val keyRegex = Regex("^[a-zA-Z0-9]{8}$") //Alphanumeric

    val regex = if (selection == CREATE_GROUP) nameRegex else keyRegex
    val function = if (selection == CREATE_GROUP) addGroup else joinGroup

    val fieldText  = if (selection == CREATE_GROUP)  "Group Name" else "Group Key"
    val buttonText = if (selection == CREATE_GROUP)  "Create" else "Join"
    val sampleText = if (selection == CREATE_GROUP) "ABC456cde _-" else "abcDEF78"

    Box(modifier = modifier.fillMaxWidth().background(Color.DarkGray.copy(alpha = 0.97f), shape = roundedShape(10.dp)).padding(15.dp),
        contentAlignment = Alignment.Center){
        Column(modifier = modifier, verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
            Text(modifier = Modifier.padding(5.dp),text = "Add Group", fontSize = 30.sp)
            Spacer(modifier.padding(8.dp))
            RadioButtonSelection(modifier = modifier, options = listOf("Create Group", "Join Group"), selected = {selection = it}, onChange = {input = ""})
            Spacer(modifier.padding(8.dp))

            TextField(
                value = input,
                onValueChange = { input = it},
                label ={Text(fieldText)},
                placeholder = {Text("Use $sampleText")},
            )
            Spacer(modifier.padding(15.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)){
                Button(enabled = (!input.isEmpty() && input.matches(regex)),onClick = {
                    function(input)
                    input = ""
                    setVisibility(false)
                }){
                    Text(buttonText)
                }
                Spacer(modifier.padding(10.dp))
                Button(onClick = {
                    setVisibility(false)
                    input = "" }
                ){
                    Text("Cancel")
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun NewGroupMenuPreview(){
    NewGroupMenu(setVisibility = {}, addGroup = {}, joinGroup = {})
}