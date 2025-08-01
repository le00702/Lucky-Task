package com.example.luckytask.firestore

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

const val MOCK_GROUP = "Some Group"
class TodoViewModel:ViewModel() {

    private val _todoDAOS =  mutableStateListOf<TodoDAO>()
    val todoDAOS:List<TodoDAO>
        get() = _todoDAOS
    var revealTodoMaker by mutableStateOf(false)

    var isLoading by mutableStateOf(false)

    init {
        viewModelScope.launch {
            loadTodos()
        }
    }

  fun loadTodos(){
        _todoDAOS.clear()
       viewModelScope.launch {
           Log.i("TodoViewModel", "Loading Todos")
           isLoading = true
           Firestore.loadTodos(MOCK_GROUP){ todos ->
               _todoDAOS.addAll(todos)
           }
           isLoading = false
       }
    }

    fun addTodo(todoDAO: TodoDAO){
        _todoDAOS.add(todoDAO)
        Firestore.addTodo(MOCK_GROUP, todoDAO)
    }

    fun removeTodo(index:Int){
        Firestore.removeTodo(MOCK_GROUP, todoDAOS[index])
        _todoDAOS.removeAt(index)
    }

    /**
     * Returns List with new order on purpose (Done Tasks at the end)
     */
    fun setTodoDone(index:Int):Int{
        val todo = _todoDAOS[index]
        if(!todo.done){
            val newTodo = todo.copy(done = true, title = todo.title + " (Done)")
            _todoDAOS[index] = newTodo
            return _todoDAOS.size
        }else{
            return -1
        }
    }

    fun setTodoUndone(index:Int):Int{
        val todo = _todoDAOS[index]
        if(todo.done){
            val newTodo = todo.copy(done = false, title = todo.title.replace("(Done)", ""))
            _todoDAOS[index] = newTodo
            return _todoDAOS.size
        }else{
            return -1
        }
    }

}