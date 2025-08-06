package com.example.luckytask.firestore

import kotlinx.serialization.Serializable

@Serializable
data class GroupDAO(
    var name:String = "Group",
    var id:String = "12345678", //Set by user when creating Group
    //UserList and TaskList: implemented in Firestore as Collection inside Group document
)
