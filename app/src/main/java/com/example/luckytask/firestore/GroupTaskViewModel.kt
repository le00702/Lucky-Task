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

const val MOCK: Boolean = true //Don't Load Real data to save usages
const val MOCK_GROUP = "Some Group"

const val TAG = "GroupTaskViewModel"
const val KEY_LENGTH = 8
class GroupTaskViewModel:ViewModel() {

    private val _taskList = MutableStateFlow<List<TaskItem>>(emptyList())
    val taskList: StateFlow<List<TaskItem>>
        get() = _taskList.asStateFlow()

    private var _taskMaker by mutableStateOf(false)
    val taskMakerState: Boolean
        get() = _taskMaker
    val setTaskMaker: (Boolean) -> Unit = { _taskMaker = it }

    private var _isLoadingTasks by mutableStateOf(false)
    val isLoadingTasks: Boolean
        get() = _isLoadingTasks


    private val _groupList = mutableStateListOf<GroupDAO>()
    val groupList: List<GroupDAO>
        get() = _groupList

    private var groupMaker by mutableStateOf(false)
    val groupMakerState: Boolean
        get() = groupMaker

    val setGroupMaker: (Boolean) -> Unit = { groupMaker = it }
    private var _isLoadingGroups by mutableStateOf(false)
    val isLoadingGroups: Boolean
        get() = _isLoadingGroups

    private var _currentGroup by mutableStateOf<GroupDAO?>(null)
    val currentGroup: GroupDAO?
        get() = _currentGroup

    fun generateRandomAlphanumeric(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun loadGroups(context: Context){
        var data:Map<String, String>?
        viewModelScope.launch {
            _isLoadingGroups = true
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
                _isLoadingGroups = false
            }
        }
    }

    fun createGroup(context:Context, name:String){
        var idExists:Boolean
        var id:String
        var counter = 0
        viewModelScope.launch {
            _isLoadingGroups = true
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
                _isLoadingGroups = false
            }
        }
    }

    fun joinGroup(context: Context, id: String) {
        viewModelScope.launch {
            _isLoadingGroups = true
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
                _isLoadingGroups = false
            }
        }
    }

    fun leaveGroup(context: Context, id: String) {
        viewModelScope.launch {
            _isLoadingGroups = true
            try{
                AppSettings.removeGroup(context, id)
            }catch (e:Exception){
                Log.e(TAG, "Error leaving group",e)
            }finally {
                _isLoadingGroups = false
            }
        }
    }

    fun loadCurrentGroup(context:Context) {
        var data:GroupDAO?
        viewModelScope.launch {
            _isLoadingGroups = true
            try{
                data = AppSettings.getCurrentGroup(context)
                data?.let { _currentGroup = GroupDAO(id = it.id, name = it.name) }
            }catch (e:Exception){
                Log.e(TAG, "Error loading group content.",e)
            }finally{
                _isLoadingGroups = false
                loadTasks()
            }
        }
    }

    fun setCurrentGroup(context: Context, group:GroupDAO){
        viewModelScope.launch {
            _isLoadingGroups = true
            try{
                AppSettings.setCurrentGroup(context, group)
                _currentGroup = group
            }catch (e:Exception){
                Log.e(TAG, "Error loading group content.",e)
            }finally{
                _isLoadingGroups = false
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
        _isLoadingTasks = true
        viewModelScope.launch {
            Log.i(TAG, "Loading Tasks")
            val newList = _taskList.value.toMutableList()
            try {
                Firestore.loadTasks(_currentGroup!!.name) { task ->
                    newList.addAll(task)
                }
                _taskList.value = newList
            } catch (e: Exception) {
                Log.e(TAG, "Error loading group content.", e)
            } finally {
                _isLoadingTasks = false
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



    /**
     * Returns List with new order on purpose (Done Tasks at the end)
     */
    fun setTaskDone(index: Int): Int {
        if (_currentGroup == null) {
            Log.i(TAG, "No Group Selected")
            return -1
        }
        val currentTasks = _taskList.value.toMutableList()
        val todo = currentTasks[index]
        if (!todo.isCompleted) {
            val newTask = GroupTaskItem(
                id = todo.id,
                title = todo.title + " (Done)",
                description = todo.description,
                dueDate = todo.dueDate,
                isActive = todo.isActive,
                isCompleted = true
            )
            currentTasks[index] = newTask
            _taskList.value = currentTasks
            return currentTasks.size
        } else {
            return -1
        }
    }

    fun setTaskUndone(index: Int): Int {
        if (_currentGroup == null) {
            Log.i(TAG, "No Group Selected")
            return -1
        }
        val currentTasks = _taskList.value.toMutableList()
        val todo = currentTasks[index]
        if (!todo.isCompleted) {
            val newTask = GroupTaskItem(
                id = todo.id,
                title = todo.title.replace("(Done)", ""),
                description = todo.description,
                dueDate = todo.dueDate,
                isActive = todo.isActive,
                isCompleted = false
            )
            currentTasks[index] = newTask
            _taskList.value = currentTasks
            return currentTasks.size
        }else {
            return -1
        }
    }
}