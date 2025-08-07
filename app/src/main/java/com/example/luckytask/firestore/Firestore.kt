package com.example.luckytask.firestore

import android.util.Log
import com.example.luckytask.data.GroupTaskItem
import com.example.luckytask.data.TaskItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate


class Firestore {
    companion object {
        fun convertDateMap(map: Map<*, *>?): LocalDate?{
            map?:return null
            val year = (map["year"] as Long).toInt()
            val month = (map["monthValue"] as Long).toInt()
            val day = (map["dayOfMonth"] as Long).toInt()
            return LocalDate.of(year, month, day)
        }
        suspend fun loadTasks(group:String, onResult: (List<TaskItem>) -> Unit){
            val tasks = mutableListOf<TaskItem>()
            Log.i("Firestore","Loading Tasks from Group $group")
            val doc = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            try{
                val res = doc.get().await()
                Log.i("Firestore", "Loading Tasks Success ${res.size()}")
                for (document in res) {
                    val task = GroupTaskItem(
                        remoteId = document.id,
                        title = document.data["title"].toString(),
                        description = document.data["description"].toString(),
                        dueDate = convertDateMap(document.data["dueDate"] as Map<*,*>?),
                        isActive = document.data["active"] as Boolean,
                        isCompleted = document.data["completed"] as Boolean,
                        assignee = document.data["assignee"] as String?
                    )
                    tasks.add(task)
                    Log.i("Firestore", "${document.id} => ${document.data}")
                }
                onResult(tasks)
            }catch (e:Exception){
                Log.e("Firestore", "Error getting documents.", e)
            }
       }

        suspend fun getTask(groupId:String, taskId:String, onResult: (GroupTaskItem) -> Unit){
            if(taskId.isEmpty()){
                Log.e("Firestore","Task has no ID")
                return
            }
            val ref = FirebaseFirestore.getInstance().collection("groups/$groupId/todos")
            try{
                val res = ref.document(taskId).get().await()
                val task = GroupTaskItem(
                    remoteId = res.id,
                    title = res.data?.get("title").toString(),
                    description = res.data?.get("description").toString(),
                    dueDate = convertDateMap(res.data?.get("dueDate") as Map<*,*>?),
                    isActive = res.data?.get("active") as Boolean,
                    isCompleted = res.data?.get("completed") as Boolean,
                    assignee = res.data?.get("assignee") as String?
                )
                onResult(task)
            }catch (e:Exception) {
                Log.e("Firestore", "Error getting document.", e)
            }
        }

        suspend fun editTask(group:String, task:GroupTaskItem){
            val ref = FirebaseFirestore.getInstance().collection("groups/$group/todos")
            if(task.remoteId.isEmpty()) {
                Log.e("Firestore","Task has no ID")
            }
            try{
                ref.document(task.remoteId).set(task).await()
            }catch (e:Exception){
                Log.e("Firestore","Error editing Task", e)
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

        suspend fun loadUsers(groupId:String):List<UserDAO>{
            val list:List<UserDAO> = listOf()
            val ref = FirebaseFirestore.getInstance().collection("groups/$groupId/users")
            try{
                val res = ref.get().await()
                Log.i("Firestore", "Loading Userlist Success ${res.size()}")
                for (document in res) {
                    val user = UserDAO(
                        id = document.id,
                        name = document.data["name"].toString(),
                    )
                    list.plus(user)
                    Log.i("Firestore", "${document.id} => ${document.data}")
                }
            }catch (e:Exception){
                Log.e("Firestore", "Error getting documents.", e)
            }
            return list
        }

        suspend fun registerUser(groupId:String, user:UserDAO){
            val ref = FirebaseFirestore.getInstance().collection("groups/$groupId/users")
            try{
                val res = ref.add(user).await()
                user.id = res.id
                Log.i("Firestore","New User ${user.name} registered")
            }catch (e:Exception){
                Log.e("Firestore","Error adding User", e)
            }
        }

        suspend fun unregisterUser(groupId:String, user:UserDAO){
            val ref = FirebaseFirestore.getInstance().collection("groups/$groupId/users")
            try {
                ref.document(user.id).delete().await()
            }catch (e:Exception){
                Log.e("Firestore","Error removing User", e)
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