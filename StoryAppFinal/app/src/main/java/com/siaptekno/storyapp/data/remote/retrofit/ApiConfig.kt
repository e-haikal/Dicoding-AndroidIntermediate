package com.siaptekno.storyapp.data.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        // Provides an instance of ApiService
        fun getApiService(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Log request/response details
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add logging to OkHttpClient
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/") // Base URL for API
                .addConverterFactory(GsonConverterFactory.create()) // Convert JSON to objects
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
