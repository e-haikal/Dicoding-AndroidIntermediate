package com.siaptekno.storyapp.ui.add_story

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.di.Injection


class AddStoryFactory(private val repository: Repository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddStoryViewModel::class.java)) {
            return AddStoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: AddStoryFactory? = null
        fun getInstance(context: Context): AddStoryFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AddStoryFactory(
                    Injection.repository(context)
                )
            }.also { INSTANCE = it }
    }
}