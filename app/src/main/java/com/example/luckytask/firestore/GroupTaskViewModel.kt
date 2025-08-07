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
import com.example.luckytask.data.TaskItem
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

    val _isNewUser by mutableStateOf(false)
    val isNewUser: Boolean
        get() = _isNewUser



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
                    _userMaker = true
                }
            }catch (e: Exception){
                Log.e(TAG, "Error loading user. $e")
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
                loadGroups(context)
            }catch(e:Exception){
                Log.e(TAG, "Error creating group. $e")
            }finally {
                _isLoading = false
            }
        }
    }

    fun joinGroup(context: Context, id: String) {
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
                _isLoading = false
                loadTasks()
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

    fun addTask(task: GroupTaskItem) {
        if (_currentGroup == null) {
            Log.i(TAG, "No Group Selected")
            return
        }
        val currentTasks = _taskList.value.toMutableList()
        currentTasks.add(task)
        viewModelScope.launch{
            Log.i(TAG, "Adding Todo to Firestore")
            Firestore.addTask(_currentGroup!!.name, task)
        }

    }

    fun removeTask(index: Int) {
        if (_currentGroup == null) {
            Log.i(TAG, "No Group Selected")
            return
        }
        val currentTasks = _taskList.value.toMutableList()
        currentTasks.removeAt(index)
        viewModelScope.launch{
            Log.i(TAG, "Removing Todo from Firestore")
            Firestore.removeTask(_currentGroup!!.name, currentTasks[index] as GroupTaskItem)
        }
    }
}