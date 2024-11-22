package com.siaptekno.storyapp.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.di.Injection

// Factory class to create instances of MainViewModel
// Handles ViewModel dependency injection
class MainFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    // Creates ViewModel instances and injects the repository dependency
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: MainFactory? = null

        // Singleton pattern to ensure a single instance of MainFactory
        fun getInstance(context: Context): MainFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MainFactory(Injection.repository(context))
            }.also { INSTANCE = it }
    }
}
