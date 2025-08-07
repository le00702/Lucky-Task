package com.example.luckytask.firestore

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.luckytask.data.GroupTaskItem
import com.example.luckytask.data.PrivateTaskItem
import com.example.luckytask.data.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val TAG = "GroupTaskViewModel"
const val KEY_LENGTH = 8
class GroupTaskViewModel:ViewModel() {

    private val _taskList = MutableStateFlow<List<TaskItem>>(emptyList())
    val taskList: StateFlow<List<TaskItem>>
        get() = _taskList.asStateFlow()

    private val _groupList = mutableStateListOf<GroupDAO>()
    val groupList: List<GroupDAO>
        get() = _groupList

    private var groupMaker by mutableStateOf(false)
    val groupMakerState: Boolean
        get() = groupMaker

    val setGroupMaker: (Boolean) -> Unit = { groupMaker = it }
    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading

    private var _currentGroup by mutableStateOf<GroupDAO?>(null)
    val currentGroup: GroupDAO?
        get() = _currentGroup

    private var _currentUser by mutableStateOf<UserDAO?>(null)
    val currentUser: UserDAO?
        get() = _currentUser

    private var _userMaker by mutableStateOf(false)
    val userMakerState: Boolean
        get() = _userMaker

    val setUserMaker: (Boolean) -> Unit = { _userMaker = it }

    private var _isNewUser by mutableStateOf(false)
    val isNewUser: Boolean
        get() = _isNewUser

    private var _userList by mutableStateOf<List<UserDAO>?>(null)
    val userList: List<UserDAO>?
        get() = _userList



    fun generateRandomAlphanumeric(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


    fun loadUser(context: Context){
        var data:UserDAO?
        viewModelScope.launch {
            _isLoading = true
            try{
                data = AppSettings.getUserInfo(context)
                if(data != null){
                    _currentUser = data
                }else{
                    _isNewUser = true
                    _userMaker = true
                }
            }catch (e: Exception){
                Log.e(TAG, "Error loading user. $e")
            }finally {
                _isLoading = false
            }
        }
    }

    fun loadUserList(){
        if(_currentUser == null){
            Log.e(TAG, "User not set")
            _isNewUser = true
            return
        }
        viewModelScope.launch {
            _isLoading = true
            try {
                _userList = Firestore.loadUsers(_currentGroup!!.id)
            }catch (e:Exception){
                Log.e(TAG, "Error loading user list. $e")
            }finally {
                _isLoading = false
            }
        }
    }

    fun setUser(context:Context, user:UserDAO){
        viewModelScope.launch {
            _isLoading = true
            try{
                AppSettings.setUserInfo(context, user)
                _currentUser = user
            }catch (e: Exception){
                Log.e(TAG, "Error setting user. $e")
            }finally {
                _isLoading = false
            }

        }
    }

    fun registerUserToAllGroups(){
        CoroutineScope(Dispatchers.IO).launch {
            _isLoading = true
            for(g in _groupList){
                Firestore.registerUser(g.id, _currentUser!!)
            }
            _isNewUser = false
            _isLoading = false
        }
    }


    fun loadGroups(context: Context){
        var data:Map<String, String>?
        viewModelScope.launch {
            _isLoading = true
            try{
                data = AppSettings.getGroups(context)
                if(data != null){
                    _groupList.clear()
                    (data as Map<out Any?, Any?>).forEach{
                        _groupList.add(GroupDAO(id = it.key as String, name = it.value as String))
                    }
                }
            }catch(e:Exception){
                Log.e(TAG, "Error loading groups. $e")
            }finally {
                _isLoading = false
            }
        }
    }

    fun createGroup(context:Context, name:String){
        var idExists:Boolean
        var id:String
        var counter = 0
        if(_currentUser == null){
            Log.e(TAG, "User not set")
            _isNewUser = true
            return
        }
        viewModelScope.launch {
            _isLoading = true
            try{
                id = generateRandomAlphanumeric(KEY_LENGTH)
                idExists = Firestore.checkIfGroupExists(id).first
                while(idExists){
                    Log.i(TAG, "ID $id already exists. Try $counter")
                    id = generateRandomAlphanumeric(KEY_LENGTH)
                    idExists = Firestore.checkIfGroupExists(id).first
                    counter++
                }
                assert(id.length == KEY_LENGTH)
                Log.i(TAG, "New ID $id found after $counter tries")
                val group = GroupDAO(id = id, name = name)
                Firestore.createGroup(group)
                AppSettings.addGroup(context, group)
                Firestore.registerUser(group.id, _currentUser!!)
                loadGroups(context)
            }catch(e:Exception){
                Log.e(TAG, "Error creating group.",e)
            }finally {
                _isLoading = false
            }
        }
    }

    fun joinGroup(context: Context, id: String) {
        if(_currentUser == null){
            Log.e(TAG, "User not set")
            _isNewUser = true
            return
        }
        viewModelScope.launch {
            _isLoading = true
            val exists: Boolean
            val name: String?
            try{
                val result = Firestore.checkIfGroupExists(id)
                exists = result.first
                name = result.second
                if (exists) {
                    AppSettings.addGroup(context, GroupDAO(id = id, name = name ?: "New Group"))
                }
                Firestore.registerUser(id, _currentUser!!)
            }catch(e:Exception){
                Log.e(TAG, "Error joining group",e)
            }finally {
                _isLoading = false
            }
        }
    }

    fun leaveGroup(context: Context, id: String) {
        viewModelScope.launch {
            _isLoading = true
            try{
                AppSettings.removeGroup(context, id)
            }catch (e:Exception){
                Log.e(TAG, "Error leaving group",e)
            }finally {
                _isLoading = false
            }
        }
    }

    fun loadCurrentGroup(context:Context) {
        var data:GroupDAO?
        viewModelScope.launch {
            _isLoading = true
            try{
                data = AppSettings.getCurrentGroup(context)
                data?.let { _currentGroup = GroupDAO(id = it.id, name = it.name) }
            }catch (e:Exception){
                Log.e(TAG, "Error loading group content.",e)
            }finally{
                _isLoading = false
                loadTasks()
            }
        }
    }

    fun setCurrentGroup(context: Context, group:GroupDAO){
        viewModelScope.launch {
            _isLoading = true
            try{
                AppSettings.setCurrentGroup(context, group)
                _currentGroup = group
            }catch (e:Exception){
                Log.e(TAG, "Error loading group content.",e)
            }finally{
                loadTasks()
                _isLoading = false
            }
        }
    }

    fun loadTasks() {
        if (_currentGroup == null) {
            Log.i(TAG, "No Group Selected")
            return
        }
        _taskList.value = emptyList()
        _isLoading = true
        viewModelScope.launch {
            Log.i(TAG, "Loading Tasks")
            val newList = _taskList.value.toMutableList()
            try {
                Firestore.loadTasks(_currentGroup!!.id) { task ->
                    newList.addAll(task)
                }
                _taskList.value = newList
            } catch (e: Exception) {
                Log.e(TAG, "Error loading group content.", e)
            } finally {
                _isLoading = false
            }
        }
    }

    fun drawRandomTask(): TaskItem? {
        val inactiveTasks =  _taskList.value.filter { !it.isActive }
        val size = inactiveTasks.size
        if(size > 0 ){
            val drawNumbers = 0..<size
            val randomIndex = drawNumbers.random()
            return inactiveTasks[randomIndex]
        }
        return null
    }

    fun setDone(task: GroupTaskItem){
        val newTask = GroupTaskItem(
            remoteId = task.remoteId,
            title = task.title + " (Done)",
            description = task.description,
            dueDate = task.dueDate,
            isActive = task.isActive,
            isCompleted = true,
            assignee = task.assignee
        )
        viewModelScope.launch {
            _isLoading = true
            Firestore.editTask(_currentGroup!!.id, newTask)
            loadTasks()
            _isLoading = false
        }
    }

    fun deleteTask(task: GroupTaskItem){
        viewModelScope.launch {
            _isLoading = true
            Firestore.removeTask(_currentGroup!!.id, task)
            loadTasks()
            _isLoading = false
        }
    }
}