package com.siaptekno.storyapp.di

import android.content.Context
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.retrofit.ApiConfig

// A singleton object for Dependency Injection to provide the necessary dependencies.
object Injection {

    // Provides an instance of the Repository, ensuring ApiService is initialized.
    fun repository(context: Context): Repository {
        // Initialize ApiService using ApiConfig.
        val apiService = ApiConfig.getApiService()

        // Return a singleton instance of Repository with ApiService.
        return Repository.getInstance(apiService)
    }
}
