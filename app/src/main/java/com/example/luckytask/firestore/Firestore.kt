package com.example.luckytask.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class Firestore {
    companion object {
        fun loadTodos(group:String, onResult: (List<TodoDAO>) -> Unit){
            val todos = mutableListOf<TodoDAO>()
            Log.i("Firestore","Loading Todos from Group $group")
            val doc = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            doc.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Firestore", "Loading Todos Success $todos")
                    for (document in task.result) {
                        todos.add(document.toObject(TodoDAO::class.java))
                        todos.last().id = document.id
                        Log.i("Firestore", "${document.id} => ${document.data}")
                    }
                    onResult(todos)
                } else {
                    Log.w("Firestore", "Error getting documents.", task.exception)
                }
            }
       }

        fun addTodo(group:String, todo:TodoDAO){
            val ref = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            ref.add(todo).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val id = task.result.id
                    todo.id= id
                    Log.i("Firestore","Todo Added to Doc $todo")
                }else{
                    Log.w("Firestore","Error adding Todo")
                }
            }
        }

        fun removeTodo(group:String, todo:TodoDAO){
            val ref = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            if(todo.id.isNullOrEmpty()) {
                Log.e("Firestore","Todo has no ID")
            }
            ref.document(todo.id!!).delete().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.i("Firestore","Todo Removed from Doc ${todo.id}")
                }else{
                    Log.w("Firestore","Error removing Todo ${todo.id}")
                }
            }
        }


        fun checkIfGroupExists(id:String):Boolean{
            var existence = false
            val ref = FirebaseFirestore.getInstance().collection("groups")
            val doc = ref.document(id)
            doc.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    existence = document.exists()
                    if (existence) {
                        Log.d("Firestore", "Group $id exists")
                    } else {
                        Log.d("Firestore", "Group does not exist")
                    }
                }
            }
            return existence
        }
    }
}