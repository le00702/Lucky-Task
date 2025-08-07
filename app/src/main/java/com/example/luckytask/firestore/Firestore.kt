package com.example.luckytask.firestore

import android.util.Log
import com.example.luckytask.data.GroupTaskItem
import com.example.luckytask.data.TaskItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate


class Firestore {
    companion object {
        suspend fun loadTasks(group:String, onResult: (List<TaskItem>) -> Unit){
            val tasks = mutableListOf<TaskItem>()
            Log.i("Firestore","Loading Todos from Group $group")
            val doc = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            try{
                val res = doc.get().await()
                Log.i("Firestore", "Loading Todos Success ${res.size()}")
                for (document in res) {
                    val task = GroupTaskItem(
                        remoteId = document.id,
                        title = document.data["title"].toString(),
                        description = document.data["description"].toString(),
                        dueDate = document.data["dueDate"] as LocalDate?,
                        isActive = document.data["isActive"] as Boolean,
                        isCompleted = document.data["isCompleted"] as Boolean
                    )
                    tasks.add(task)
                    Log.i("Firestore", "${document.id} => ${document.data}")
                }
                onResult(tasks)
            }catch (e:Exception){
                Log.e("Firestore", "Error getting documents.", e)
            }
       }

        suspend fun addTask(group:String, task:GroupTaskItem){
            val ref = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            try {
                val res = ref.add(task).await()
                task.remoteId= res.id
                Log.i("Firestore","Task Added to Doc $task")
            }catch (e:Exception){
                Log.e("Firestore","Error adding Task", e)
            }
        }

        suspend fun removeTask(group:String, task:GroupTaskItem){
            val ref = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            if(task.remoteId.isEmpty()) {
                Log.e("Firestore","Task has no ID")
            }
            try{
                ref.document(task.remoteId).delete().await()
            }catch (e:Exception){
                Log.e("Firestore","Error removing Task", e)
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