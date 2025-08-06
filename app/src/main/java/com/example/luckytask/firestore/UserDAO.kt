package com.example.luckytask.firestore

import kotlinx.serialization.Serializable

@Serializable
data class UserDAO(
    var id:String? = null,
    var name:String = "User",
    var nickName:String? = "U",//short (one, two letter nickname)
)
