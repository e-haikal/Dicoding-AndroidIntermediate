package com.siaptekno.storyapp.ui.story_map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.di.Injection

class StoryMapsFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Ensure that the requested ViewModel class is StoryMapsViewModel.
        if (modelClass.isAssignableFrom(StoryMapsViewModel::class.java)) {
            return StoryMapsViewModel(repository) as T // Provide the ViewModel instance.
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name) // Handle invalid ViewModel class.
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryMapsFactory? = null

        // Singleton pattern to ensure only one instance of StoryMapsFactory exists.
        fun getInstance(context: Context): StoryMapsFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryMapsFactory(
                    Injection.repository(context) // Retrieve the repository from the dependency injection module.
                )
            }.also { INSTANCE = it }
    }
}
