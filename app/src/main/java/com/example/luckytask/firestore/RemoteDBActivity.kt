package com.example.luckytask.firestore

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckytask.R
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AppWithDrawer


const val ACTIVITY_NAME = "Remote DB Mock"
var roundedShape:(Dp) -> Shape = { size ->
    RoundedCornerShape(size)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TodoView(modifier: Modifier = Modifier, viewModel: GroupTaskViewModel = viewModel()){

    val pullRefreshState = rememberPullRefreshState(
        viewModel.isLoading,
        {viewModel.loadTodos()}
    )

    val groupMaker = viewModel.groupMakerState
    val setGroupMenu = viewModel.setGroupMaker

    val todoMaker = viewModel.todoMakerState
    val setTodoMenu = viewModel.setTodoMaker


    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            setTodoMenu(true)
        }) {
            Icon(painter = painterResource(R.drawable.add_task_icon), contentDescription = "Add Task")
        }
    }) {
        Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)){
            LazyColumn(
                modifier = modifier.fillMaxHeight().padding(bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                val size = viewModel.todoDAOS.size
                if(size == 0){
                    item{
                        Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center){
                            Text(text = if (viewModel.isLoading) "" else "No Tasks yet")
                        }
                    }
                }
                items(size){ index ->
                    TodoItem(viewModel.todoDAOS[index], index = index,
                        done = {viewModel.setTodoDone(index)},undone = {viewModel.setTodoUndone(index)}, remove = {viewModel.removeTodo(index)})
                }
            }
            PullRefreshIndicator(
                viewModel.isLoading,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }


        if(todoMaker){
            Box(modifier = Modifier.fillMaxSize().padding(top = 50.dp).clickable(onClick = {/*Absorb Click*/}), contentAlignment = Alignment.TopCenter){
                TodoByUser(
                    setVisibility = setTodoMenu,
                    addTodo = {todo -> viewModel.addTodo(todo) }
                )
            }
        }
    }
}

@Composable
fun TodoByUser(modifier:Modifier = Modifier,setVisibility:(vis:Boolean) -> Unit, addTodo:(todoDAO: TodoDAO) -> Unit){
    var title by remember{mutableStateOf("")}
    var text by remember{mutableStateOf("")}
    Box(modifier = modifier.fillMaxWidth().fillMaxHeight(fraction = 0.5f).background(Color.DarkGray.copy(alpha = 0.8f), shape = roundedShape(10.dp)),
        contentAlignment = Alignment.Center){
        LazyColumn {
            item{
                Column(modifier = modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.CenterHorizontally){
                    Text(modifier = Modifier.padding(5.dp),text = "New Task", fontSize = 30.sp)
                    Spacer(modifier.padding(20.dp))
                    TextField(value = title, onValueChange = {title = it}, label ={Text("Title")}, placeholder ={Text("Todo")} )
                    Spacer(modifier.padding(15.dp))
                    TextField(value = text, onValueChange = {text = it}, label ={Text("Description")}  , placeholder ={Text("Just do it")} )
                    Spacer(modifier.padding(30.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)){
                        Button(enabled = (!title.isEmpty()),onClick = {
                            addTodo(TodoDAO(title = title, description = text))
                            title = ""
                            text = ""
                            setVisibility(false)}){
                            Text("OK")
                        }
                        Button(onClick = {setVisibility(false)}){
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Single List Item
 */
@Composable
fun TodoItem(todoDAO: TodoDAO, index:Int, done :(index:Int) -> Unit, undone:(index:Int)-> Unit, remove:(index:Int) -> Unit){
    Card(elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(Color.Transparent)){
        Box(modifier = Modifier.fillMaxWidth().padding(5.dp).background(color= MaterialTheme.colorScheme.background, shape = roundedShape(5.dp))
            .pointerInput(Unit){
                detectTapGestures(
                    onDoubleTap = {
                    remove(index)
                },
                    onLongPress = {
                        undone(index)
                    }
                )
            }
        ){
            Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Column {
                    Text(text = todoDAO.title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                    Box(modifier = Modifier.fillMaxWidth(fraction = 0.7f)){
                        Text(text = todoDAO.description, fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                Box(modifier = Modifier.padding(10.dp).clickable(onClick = {})){
                    IconButton(onClick = {done(index)}) {
                        Icon(
                            painter = painterResource(R.drawable.done_icon),
                            contentDescription = "Set Task as Done",
                            modifier = Modifier.size(50.dp),
                            tint = if(todoDAO.isCompleted)  Color.Green else Color.Gray
                        )
                    }
                }
            }
        }
    }

}

class RemoteDBActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                AppWithDrawer(
                    currentActivityName = ACTIVITY_NAME,
                    topBarTitle = stringResource(R.string.title_mock)
                ){
                    TodoView()
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp ,height=891dp", showSystemUi = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun TodoItemPreview() {
    LuckyTaskTheme {
        val text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam"
        //TodoItem(todo = Todo("Title",text))
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp", showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun ViewModelInitializerPreview() {
    LuckyTaskTheme {
        TodoView()
    }
}