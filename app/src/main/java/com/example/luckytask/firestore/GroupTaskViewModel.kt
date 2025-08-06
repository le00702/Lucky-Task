package com.example.luckytask.firestore

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

const val MOCK: Boolean = true //Don't Load Real data to save usages
const val MOCK_GROUP = "Some Group"
class GroupTaskViewModel:ViewModel() {

    private val _todoDAOS = mutableStateListOf<TodoDAO>()
    val todoDAOS: List<TodoDAO>
        get() = _todoDAOS

    private var _todoMaker by mutableStateOf(false)
    val todoMakerState: Boolean
        get() = _todoMaker
    val setTodoMaker: (Boolean) -> Unit = { _todoMaker = it }

    private var _isLoadingTasks by mutableStateOf(false)
    val isLoadingTasks: Boolean
        get() = _isLoadingTasks


    private val _groupDAOS = mutableStateListOf<GroupDAO>()
    val groupDAOS: List<GroupDAO>
        get() = _groupDAOS

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

    init {
        viewModelScope.launch {
            loadTodos()
        }
    }

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
                    _groupDAOS.clear()
                    (data as Map<out Any?, Any?>).forEach{
                        _groupDAOS.add(GroupDAO(id = it.key as String, name = it.value as String))
                    }
                }
            }catch(e:Exception){
                Log.e("GroupTaskViewModel", "Error loading groups. $e")
            }finally {
                _isLoadingGroups = false
            }
        }

    }

    fun joinGroup(context: Context, id: String) {
        val exists: Boolean
        val name: String?
        if (MOCK) {
            exists = true
            name = "Mock Group"
        } else {
            val result = Firestore.checkIfGroupExists(id)
            exists = result.first
            name = result.second
        }
        if (exists) {
            viewModelScope.launch {
                _isLoadingGroups = true
                try{
                    AppSettings.addGroup(context, GroupDAO(id = id, name = name ?: "New Group"))
                }catch(e:Exception){
                    Log.e("GroupTaskViewModel", "Error joining group. $e")
                }finally {
                    _isLoadingGroups = false
                }
            }
        }
    }

    fun exitGroup(context: Context, id: String) {
        viewModelScope.launch {
            _isLoadingGroups = true
            try{
                AppSettings.removeGroup(context, id)
            }catch (e:Exception){
                Log.e("GroupTaskViewModel", "Error leaving group. $e")
            }finally {
                _isLoadingGroups = false
            }
        }
    }

    fun loadCurrentGroup(context:Context) {
        var data:Pair<String, String>?
        viewModelScope.launch {
            _isLoadingGroups = true
            try{
                data = AppSettings.getCurrentGroup(context)
                data?.let { _currentGroup = GroupDAO(id = it.first, name = it.second) }
            }catch (e:Exception){
                Log.e("GroupTaskViewModel", "Error loading group content. $e")
            }finally{
                _isLoadingGroups = false
                loadTodos()
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
                Log.e("GroupTaskViewModel", "Error loading group content. $e")
            }finally{
                _isLoadingGroups = false
                loadTodos()
            }
        }
    }

    fun loadTodos() {
        _todoDAOS.clear()
        _isLoadingTasks = true
        if (_currentGroup == null) {
            Log.i("TodoViewModel", "No Group Selected")
            _isLoadingTasks = false
            return
        }
        if (MOCK) {
            _todoDAOS.addAll(
                listOf(
                    TodoDAO("Title1", "Description1"),
                    TodoDAO("Title2", "Description2"),
                    TodoDAO("Title3", "Description3")
                )
            )
            _isLoadingTasks = false
            return
        }
        Log.i("TodoViewModel", "Loading Todos")
        Firestore.loadTodos(_currentGroup!!.name) { todos ->
            _todoDAOS.addAll(todos)
            _isLoadingTasks = false
        }
    }

    fun addTodo(todoDAO: TodoDAO) {
        if (_currentGroup == null) {
            Log.i("TodoViewModel", "No Group Selected")
            return
        }
        if (MOCK) {
            _todoDAOS.add(todoDAO)
            return
        }
        _todoDAOS.add(todoDAO)
        Firestore.addTodo(_currentGroup!!.name, todoDAO)
    }

    fun removeTodo(index: Int) {
        if (_currentGroup == null) {
            Log.i("TodoViewModel", "No Group Selected")
            return
        }

        if (MOCK) {
            _todoDAOS.removeAt(index)
            return
        }
        Firestore.removeTodo(_currentGroup!!.name, todoDAOS[index])
        _todoDAOS.removeAt(index)
    }

    /**
     * Returns List with new order on purpose (Done Tasks at the end)
     */
    fun setTodoDone(index: Int): Int {
        val todo = _todoDAOS[index]
        if (!todo.isCompleted) {
            _todoDAOS[index].apply {
                isCompleted = true
                title = todo.title + " (Done)"
            }
            return _todoDAOS.size
        } else {
            return -1
        }
    }

    fun setTodoUndone(index: Int): Int {
        val todo = _todoDAOS[index]
        if (todo.isCompleted) {
            _todoDAOS[index].apply {
                isCompleted = false
                title.replace("(Done)", "")
            }
            return _todoDAOS.size
        } else {
            return -1
        }
    }
}