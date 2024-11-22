package com.siaptekno.storyapp.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.di.Injection

class DetailFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    /**
     * Creates a ViewModel instance and ensures it matches the required type.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: DetailFactory? = null

        /**
         * Singleton instance for the factory, initialized with a repository.
         */
        fun getInstance(context: Context): DetailFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DetailFactory(
                    Injection.repository(context) // Dependency injection for repository
                )
            }.also { INSTANCE = it }
    }
}
