package com.example.luckytask.ui.theme.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import java.nio.file.Files.size

@Composable
fun <T> Dropdown(
    items: List<T>,
    defaultText:String = "Select item",
    onValueChange: (T) -> T, //load new Data
    text: (T) -> String = {it.toString()},
    type: String = "item",
    specialFirstItem:  Pair<String, (Boolean)->Unit>? = null
){
    var expanded by remember { mutableStateOf(false) }
    var selection by remember { mutableStateOf<T?>(null) }
    var buttonWidth by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        Button(modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
            buttonWidth = coordinates.size.width
        },
            onClick = { expanded = !expanded; }) {
            Text( if(selection != null) text(selection!!) else defaultText)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select first $type")
        }

        val menuItemModifier = Modifier.fillMaxWidth()
        DropdownMenu(
            modifier = Modifier.width(with(LocalDensity.current) { buttonWidth.toDp() }),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if(specialFirstItem != null){
                val text = specialFirstItem.first
                val func = specialFirstItem.second
                DropdownMenuItem(text = {Text(text, menuItemModifier)}, onClick = {expanded = false; func(true)})
            }

            if(items.isEmpty()){
                DropdownMenuItem(text = {Text("No $type found",menuItemModifier)}, onClick = {expanded})
            }else{
                items.forEach{ item ->
                    DropdownMenuItem(
                        text = {Text(text(item),menuItemModifier)},
                        onClick = {
                            expanded = false
                            selection = item
                            onValueChange(item)
                        }
                    )
                }
            }

        }
    }
}