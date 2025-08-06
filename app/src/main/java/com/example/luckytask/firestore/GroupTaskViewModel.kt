package com.example.luckytask.firestore

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

const val MOCK: Boolean = true //Don't Load Real data to save usages
const val MOCK_GROUP = "Some Group"
class GroupTaskViewModel:ViewModel() {

    private val _todoDAOS =  mutableStateListOf<TodoDAO>()
    val todoDAOS:List<TodoDAO>
        get() = _todoDAOS

    private var _todoMaker by mutableStateOf(false)
    val todoMakerState:Boolean
        get() = _todoMaker
    val setTodoMaker: (Boolean) -> Unit = { _todoMaker = it }

    private var _isLoadingTasks by mutableStateOf(false)
    val isLoadingTasks: Boolean
        get() = _isLoadingTasks


    private var groupMaker by mutableStateOf(false)
    val groupMakerState:Boolean
        get() = groupMaker

    val setGroupMaker: (Boolean) -> Unit = { groupMaker = it }
    private var _isLoadingGroups by mutableStateOf(false)
    val isLoadingGroups: Boolean
        get() = _isLoadingGroups

    var currentGroup by mutableStateOf<GroupDAO?>(null)

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

    fun joinGroup(context:Context, id:String){
        val exists:Boolean
        val name:String?
        if(MOCK){
            exists = true
            name = "Mock Group"
        }else{
            val result = Firestore.checkIfGroupExists(id)
            exists = result.first
            name = result.second
        }
        if(exists){
            viewModelScope.launch {
                AppSettings.addGroup(context, GroupDAO(id = id, name = name?:""))
            }
        }
    }

    fun exitGroup(context:Context, id:String){
        viewModelScope.launch {
            AppSettings.removeGroup(context, id)
        }
    }

    fun loadGroup(context:Context, id:String){
        viewModelScope.launch {

        }
    }

  fun loadTodos(){
        _todoDAOS.clear()
      if(currentGroup == null){
          Log.i("TodoViewModel", "No Group Selected")
          return
      }
      if(MOCK){
          _todoDAOS.addAll(listOf(TodoDAO("Title1", "Description1"), TodoDAO("Title2", "Description2"), TodoDAO("Title3", "Description3")))
          return
      }
       viewModelScope.launch {
           Log.i("TodoViewModel", "Loading Todos")
           _isLoadingTasks = true
           Firestore.loadTodos(currentGroup!!.name){ todos ->
               _todoDAOS.addAll(todos)
           }
           _isLoadingTasks = false
       }
    }

    fun addTodo(todoDAO: TodoDAO){
        if(currentGroup == null){
            Log.i("TodoViewModel", "No Group Selected")
            return
        }
        if(MOCK){
            _todoDAOS.add(todoDAO)
            return
        }
        _todoDAOS.add(todoDAO)
        Firestore.addTodo(currentGroup!!.name, todoDAO)
    }

    fun removeTodo(index:Int){
        if(currentGroup == null){
            Log.i("TodoViewModel", "No Group Selected")
            return
        }

        if(MOCK){
            _todoDAOS.removeAt(index)
            return
        }
        Firestore.removeTodo(currentGroup!!.name, todoDAOS[index])
        _todoDAOS.removeAt(index)
    }

    /**
     * Returns List with new order on purpose (Done Tasks at the end)
     */
    fun setTodoDone(index:Int):Int{
        val todo = _todoDAOS[index]
        if(!todo.isCompleted){
            _todoDAOS[index].apply{
                isCompleted = true
                title = todo.title + " (Done)"
            }
            return _todoDAOS.size
        }else{
            return -1
        }
    }

    fun setTodoUndone(index:Int):Int{
        val todo = _todoDAOS[index]
        if(todo.isCompleted){
            _todoDAOS[index].apply{
                isCompleted = false
                title.replace("(Done)", "")
            }
            return _todoDAOS.size
        }else{
            return -1
        }
    }

}