package com.example.luckytask.firestore

data class GroupDAO(val users:List<UserDAO>, val tasks:List<TodoDAO>)
