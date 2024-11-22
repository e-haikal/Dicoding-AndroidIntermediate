package com.siaptekno.storyapp.ui.story_map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.di.Injection


class StoryMapsFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryMapsViewModel::class.java)) {
            return StoryMapsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryMapsFactory? = null
        fun getInstance(context: Context): StoryMapsFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: StoryMapsFactory(
                    Injection.repository(context)
                )
            }.also { INSTANCE = it }
    }
}