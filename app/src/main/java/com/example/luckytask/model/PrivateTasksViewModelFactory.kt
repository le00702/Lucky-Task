package com.example.luckytask.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.luckytask.data.PrivateTasksDAO

/*** Create the ViewModel for private tasks ***/
class PrivateTasksViewModelFactory(private val dao: PrivateTasksDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PrivateTasksViewModel(dao) as T
    }
}