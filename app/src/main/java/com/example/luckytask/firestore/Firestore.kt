package com.example.luckytask.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class Firestore {
    companion object {
        suspend fun loadTodos(group:String, onResult: (List<TodoDAO>) -> Unit){
            val todos = mutableListOf<TodoDAO>()
            Log.i("Firestore","Loading Todos from Group $group")
            val doc = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            try{
                val res = doc.get().await()
                Log.i("Firestore", "Loading Todos Success $todos")
                for (document in res) {
                    todos.add(document.toObject(TodoDAO::class.java))
                    todos.last().id = document.id
                    Log.i("Firestore", "${document.id} => ${document.data}")
                }
                onResult(todos)
            }catch (e:Exception){
                Log.e("Firestore", "Error getting documents.", e)
            }
       }

        suspend fun addTodo(group:String, todo:TodoDAO){
            val ref = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            try {
                val res = ref.add(todo).await()
                val id = res.id
                todo.id= id
                Log.i("Firestore","Todo Added to Doc $todo")
            }catch (e:Exception){
                Log.e("Firestore","Error adding Todo", e)
            }
        }

        suspend fun removeTodo(group:String, todo:TodoDAO){
            val ref = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            if(todo.id.isNullOrEmpty()) {
                Log.e("Firestore","Todo has no ID")
            }
            try{
                ref.document(todo.id!!).delete().await()
            }catch (e:Exception){
                Log.e("Firestore","Error removing Todo", e)
            }
        }


        suspend fun checkIfGroupExists(id:String):Pair<Boolean, String?>{
            var existence = false
            var name:String? = null
            val ref = FirebaseFirestore.getInstance().collection("groups")
            val doc = ref.document(id)
            try{
                val res = doc.get().await()
                if(res.exists()) {
                    Log.d("Firestore", "Group $id exists")
                    existence = true
                    name = res.data?.get("name").toString()
                } else {
                    Log.d("Firestore", "Group does not exist")
                }
            }catch (e:Exception){
                Log.e("Firestore", "Error getting documents.", e)

            }
            return Pair(existence, name)
        }

       suspend fun createGroup(group:GroupDAO){
            val ref = FirebaseFirestore.getInstance().collection("groups")
           try{
               val res = ref.document(group.id).set(group).await()
               Log.i("Firestore","New Group ${group.name} created")
           }catch (e:Exception){
               Log.e("Firestore","Error adding Group", e)
           }
       }
    }
}