package com.example.luckytask.firestore

data class GroupDAO(
    var name:String = "Group",
    var id:String? = null, //Set by Firestore
    var joinKey:String = "12345", //Set when creating Group
    //UserList and TaskList: implemented in Firestore as Collection inside Group document
)
