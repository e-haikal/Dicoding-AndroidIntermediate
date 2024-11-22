package com.siaptekno.storyapp.data.remote

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.siaptekno.storyapp.data.Result
import com.siaptekno.storyapp.data.remote.response.*
import com.siaptekno.storyapp.data.remote.retrofit.ApiService
import com.siaptekno.storyapp.data.remote.retrofit.StoryPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class Repository private constructor(
    private val apiService: ApiService // Retrofit service for API interactions
) {

    // Function to log in a user and handle API response
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) { // Use IO dispatcher for network calls
            try {
                val response = apiService.login(email, password)
                if (!response.error) Result.Success(response) // Success case
                else Result.Error(response.message) // API returned an error
            } catch (e: Exception) {
                Result.Error("${e.message}") // Handle unexpected errors
            }
        }
    }

    // Function to register a new user
    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(name, email, password)
                if (!response.error) Result.Success(response)
                else Result.Error(response.message)
            } catch (e: HttpException) {
                Result.Error("${e.message}") // Handle HTTP errors
            }
        }
    }

    // Function to fetch all stories
    suspend fun getAllStories(token: String): Result<List<ListStoryItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStories("Bearer $token")
                if (!response.error) Result.Success(response.listStory)
                else Result.Error(response.message)
            } catch (e: Exception) {
                Result.Error("An error occurred: ${e.message}") // Handle general exceptions
            }
        }
    }

    // Function to fetch details of a specific story
    suspend fun getDetail(token: String, id: String): Result<Story> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getDetail("Bearer $token", id)
                if (!response.error) Result.Success(response.story)
                else Result.Error(response.message)
            } catch (e: Exception) {
                Result.Error("An error occurred: ${e.message}")
            }
        }
    }

    // Function to upload a new story with optional location data
    suspend fun addStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Result<AddStoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $token"
                val response = apiService.addStory(token, description, photo, lat, lon)
                if (!response.error) {
                    Result.Success(response)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: HttpException) {
                Result.Error("HTTP Exception: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An error occurred: ${e.message}")
            }
        }
    }


    // Function to fetch stories with location filters
    suspend fun getStoryWithMap(token: String, location: Int): Result<List<ListStoryItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = "Bearer $token"
                val response = apiService.getStories(token, location = location)
                if (!response.error) {
                    Result.Success(response.listStory)
                } else {
                    Result.Error(response.message)
                }
            } catch (e: HttpException) {
                Result.Error("Http Exception: ${e.message}")
            } catch (e: Exception) {
                Result.Error("An error occured: ${e.message}")
            }
        }
    }

    // Function to fetch stories using pagination
    fun getStoriesPaging(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5), // Configure page size
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).liveData
    }

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        // Singleton pattern to provide a single instance of the Repository
        fun getInstance(apiService: ApiService): Repository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(apiService)
            }.also { INSTANCE = it }
    }
}
