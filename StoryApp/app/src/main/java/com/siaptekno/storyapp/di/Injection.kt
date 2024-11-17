package com.siaptekno.storyapp.di

import android.content.Context
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun repository(context: Context): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}