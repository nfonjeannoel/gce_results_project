package com.camgist.gceresults.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.camgist.gceresults.database.StudentResultDao
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val database : StudentResultDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown view model")
    }
}