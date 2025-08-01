package com.example.luckytask.firestore

data class TodoDAO(var title:String = "",
                   var text:String = "",
                   var done:Boolean = false,
                   var id:String? = null)