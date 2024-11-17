package com.siaptekno.storyapp.ui.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.di.Injection

// Factory class for creating instances of RegisterViewModel.
// This follows the best practice of using a Factory pattern for ViewModel creation when dependencies are involved.
class RegisterFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the model class is assignable from RegisterViewModel.
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            // Create a RegisterViewModel instance using the repository dependency.
            return RegisterViewModel(repository) as T
        }
        // Throw an exception if the ViewModel class is unknown.
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        // Volatile singleton instance to ensure thread safety when creating the factory.
        @Volatile
        private var INSTANCE: RegisterFactory? = null

        // Provide a thread-safe way to get the singleton instance of RegisterFactory.
        fun getInstance(context: Context): RegisterFactory =
            INSTANCE ?: synchronized(this) {
                // If the instance is null, create a new one using the repository injection.
                INSTANCE ?: RegisterFactory(
                    Injection.repository(context)
                )
            }.also { INSTANCE = it }
    }
}
