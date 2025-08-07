package com.example.luckytask

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.luckytask.firestore.GroupDAO
import com.example.luckytask.data.GroupTaskItem
import com.example.luckytask.data.TaskItem
import com.example.luckytask.data.TaskRepository
import com.example.luckytask.firestore.Firestore
import com.example.luckytask.sensor.ShakeListener
import com.example.luckytask.ui.theme.LuckyTaskTheme
import com.example.luckytask.ui.theme.elements.AddTaskButton
import com.example.luckytask.ui.theme.elements.AppWithDrawer
import com.example.luckytask.ui.theme.elements.Dice
import com.example.luckytask.ui.theme.elements.EditableTaskCard
import com.example.luckytask.firestore.GroupTaskViewModel
import com.example.luckytask.firestore.UserDAO
import com.example.luckytask.ui.theme.elements.Dropdown
import com.example.luckytask.ui.theme.elements.NewGroupMenu
import com.example.luckytask.ui.theme.elements.NewUserMenu
import com.example.luckytask.ui.theme.elements.TaskCard

private const val REMOTE = true

/*** Pass the name of the activity to display it correctly on the hamburger menu ***/
private val ACTIVITY_NAME = "GroupTasksActivity"

class GroupTasksActivity() : ComponentActivity() {
    private lateinit var shakeListener: ShakeListener

    private val TAG = "[SENSOR]"

    /*** Use this variable to keep track of animation ***/
    private var triggerAnimation = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*** Pass the code for onShake() for shakeListener --> trigger animation ***/
        shakeListener = ShakeListener(this) {
            Log.d(TAG, "Shake detected!")
            triggerAnimation.value = true
        }

        enableEdgeToEdge()
        setContent {
            LuckyTaskTheme {
                AppWithDrawer(
                    currentActivityName = ACTIVITY_NAME,
                    topBarTitle = stringResource(R.string.title_group_todos)
                ) {
                    GroupTasksScreen(
                        modifier = Modifier.padding(20.dp),
                        triggerAnimation
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeListener.start()
    }

    override fun onPause() {
        super.onPause()
        shakeListener.stop()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupTasksScreen(modifier: Modifier = Modifier, triggerAnimation: MutableState<Boolean>, viewModel: GroupTaskViewModel = viewModel()) {

    val HEADER_SIZE = 30.sp
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val loadAll:() -> Unit = {
        viewModel.loadGroups(context)
        viewModel.loadCurrentGroup(context)
        viewModel.loadUser(context)
        viewModel.loadUserList()
        viewModel.loadTasks()
    }

    val pullRefreshState = rememberPullRefreshState(
        viewModel.isLoading,
        {loadAll()}
    )

    val groupMaker = viewModel.groupMakerState
    val setGroupMenu = viewModel.setGroupMaker

    val groupList = viewModel.groupList

    val currentGroup = viewModel.currentGroup

    val userMaker = viewModel.userMakerState

    val currentUser = viewModel.currentUser

    val userList = viewModel.userList

    val setAndLoadUser: (String) -> Unit = {
        viewModel.setUser(context, UserDAO(name = it))
        if(viewModel.isNewUser){
            viewModel.registerUserToAllGroups()
        }
        //loadAll()

    }

    //When changing groups reload taks list
    val loadGroup: (GroupDAO) -> Unit = {
        viewModel.setCurrentGroup(context,it)
        viewModel.loadTasks()
    }


    /*** Data from Remote Repository (Firestore) ***/
    val remoteTasks by viewModel.taskList.collectAsState()

    /*** Data from local Repository (RoomDB) ***/
    val taskRepository = remember { TaskRepository.getInstance() }
    val localTasks by taskRepository.tasks.collectAsState()


    val tasks = if(REMOTE) remoteTasks else localTasks

    var refreshTrigger by remember { mutableIntStateOf(0) }

    val groupTasks = tasks.filterIsInstance<GroupTaskItem>()

    val activeTasks = groupTasks.filter { it.isActive && it.assignee == currentUser?.name }

    val roommateTasks = groupTasks.filter { it.assignee != null && it.assignee != currentUser?.name }

    val todoTasks = groupTasks.filter { !it.isActive && it.assignee == null }

    Log.i("GroupTasksScreen", "Current User: ${currentUser?.name}")
    Log.i("GroupTasksScreen", "tasks: ${tasks.size}")
    Log.i("GroupTasksScreen", "groupTasks: ${groupTasks.size}")
    Log.i("GroupTasksScreen", "activeTasks: ${activeTasks.size}")
    Log.i("GroupTasksScreen", "roommateTasks: ${roommateTasks.size}")
    Log.i("GroupTasksScreen", "todoTasks: ${todoTasks.size}")

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                loadAll()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(triggerAnimation.value) {
        if(triggerAnimation.value){
            val task = viewModel.drawRandomTask() as GroupTaskItem?
            if(task != null){
                val newTask = GroupTaskItem(
                    remoteId = task.remoteId,
                    title = task.title,
                    description = task.description,
                    dueDate = task.dueDate,
                    isActive = true,
                    isCompleted = false,
                    assignee = currentUser?.name)
                Firestore.editTask(currentGroup!!.id,  newTask)
                Log.i("GroupTasksScreen", "newTask: ${newTask.isActive}")
                viewModel.loadTasks()
            }
        }

    }

    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {

        /*** Organize elements in column ***/
        LazyColumn(
            modifier = modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {

            item {
                Text(
                    text = "My Group Tasks",
                    fontSize = HEADER_SIZE
                )
            }
            item {
                Dropdown(
                    items = groupList,
                    defaultText = currentGroup?.name ?: "Select Group",
                    onValueChange = { loadGroup(it) },
                    text = { it.name },
                    type = "Group",
                    specialFirstItem = Pair("Create Group", setGroupMenu)
                )
            }
            if(currentGroup != null){
                item{
                    Text(
                        text = "Group Key: ${currentGroup.id}",
                        fontSize = 20.sp
                    )
                }
            }
            if (activeTasks.isEmpty()) {
                item {
                    Text(
                        "You currently have no active tasks. Roll the dice to start a task!",
                        color = colorResource(R.color.task_text_color),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(activeTasks) { task ->
                    TaskCard(
                        task = task,
                        isMine = true,
                        setDone = { viewModel.setDone(it as GroupTaskItem) },
                        deleteTask = { viewModel.deleteTask(it as GroupTaskItem) }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(30.dp)) }

            item {
                Dice(
                    modifier = modifier,

                    /*** Pass value of triggerAnimation to Dice for the actual animation ***/
                    triggerAnimation = triggerAnimation,
                    isMock = false
                )
            }

            item { Spacer(modifier = Modifier.height(50.dp)) }

            item {
                Text(
                    text = "My Roommates' Group Tasks",
                    fontSize = HEADER_SIZE,
                    textAlign = TextAlign.Center,
                    /*** Use this to add space between the lines ***/
                    lineHeight = 30.sp
                )
            }

            /*** If there are no active tasks, display the following message
             *   --> user is asked to roll dice
             *   --> align text centered ***/
            if (roommateTasks.isEmpty()) {
                item {
                    Text(
                        "Your roommates haven’t drawn a task yet.",
                        color = colorResource(R.color.task_text_color),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                /*** If there ARE roommate tasks, display them all ***/
                items(roommateTasks) { task ->
                    TaskCard(
                        task = task,
                        isMine = false,
                        setDone = { },
                        deleteTask = { }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            item {
                Text(
                    text = "Group TODO List",
                    fontSize = HEADER_SIZE
                )
            }

            item {
                AddTaskButton(
                    modifier = Modifier,
                    context,
                    ACTIVITY_NAME,
                    stringResource(R.string.title_group_todos),
                    isGroupTask = true
                )
            }

            // Editable Group Tasks --> not assigned to/drawn by anyone yet
            items(todoTasks) { taskItem ->
                EditableTaskCard(
                    task = taskItem,
                    modifier = Modifier,
                    onTaskUpdated = { refreshTrigger++ },
                    isGroupTask = true
                )
            }
        }

        PullRefreshIndicator(
            viewModel.isLoading,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }
        }
        val popupModifier = modifier.fillMaxSize().padding(horizontal = 5.dp).clickable { null }
        if(userMaker && !viewModel.isLoading) {
            Box(
                modifier =popupModifier,
                contentAlignment = Alignment.TopCenter
            ){
                NewUserMenu(
                    setUser = setAndLoadUser,
                    setVisibility = viewModel.setUserMaker
                )
            }
        }

        if (groupMaker && !viewModel.isLoading) {
            Box(
                modifier = popupModifier,
                contentAlignment = Alignment.TopCenter
            ) {
                NewGroupMenu(
                    setVisibility = setGroupMenu,
                    addGroup = { viewModel.createGroup(context = context, name = it) },
                    joinGroup = {
                        viewModel.joinGroup(context = context, id = it)
                    })
            }
        }
    }
}