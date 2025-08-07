package com.example.luckytask.firestore

import kotlinx.serialization.Serializable

@Serializable
data class UserDAO(
    var id:String = "",
    var name:String = "User",
)
