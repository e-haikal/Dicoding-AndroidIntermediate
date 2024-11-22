package com.siaptekno.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.ListStoryItem

// ViewModel class responsible for preparing and managing data for MainActivity
// Ensures data survives configuration changes like screen rotations
class MainViewModel(private val repository: Repository) : ViewModel() {

    // LiveData to indicate loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Function to fetch paginated list of stories
    // token: User's authentication token
    fun stories(token: String): LiveData<PagingData<ListStoryItem>> {
        _isLoading.value = true // Show loading indicator
        return repository.getStoriesPaging(token)
            .cachedIn(viewModelScope) // Caches data in ViewModel's scope to optimize resources
            .also {
                _isLoading.value = false // Hide loading indicator after data fetch
            }
    }
}
