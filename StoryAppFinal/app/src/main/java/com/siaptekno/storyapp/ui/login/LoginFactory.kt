package com.siaptekno.storyapp.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.di.Injection

// Factory for creating instances of LoginViewModel.
// Follows the Dependency Injection (DI) principle to decouple ViewModel creation from the UI.
class LoginFactory(private val repository: Repository) : ViewModelProvider.NewInstanceFactory() {

    // Overriding create method to provide an instance of LoginViewModel.
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    // Singleton pattern for providing a single instance of the factory.
    companion object {
        @Volatile
        private var INSTANCE: LoginFactory? = null

        fun getInstance(context: Context): LoginFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LoginFactory(
                    Injection.repository(context) // Provides repository instance from DI
                )
            }.also { INSTANCE = it }
    }
}
