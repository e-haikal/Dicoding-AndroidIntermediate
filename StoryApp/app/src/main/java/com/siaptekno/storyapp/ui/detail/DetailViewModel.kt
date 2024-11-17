package com.siaptekno.storyapp.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch
import com.siaptekno.storyapp.data.Result

class DetailViewModel(private val repository: Repository) : ViewModel() {
    // LiveData to track loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    // LiveData to store the result of fetching story details
    private val _detail = MutableLiveData<Result<Story>>()
    val detail: MutableLiveData<Result<Story>> = _detail

    /**
     * Fetches story details using a given token and story ID.
     */
    fun getDetail(token: String, id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            // Fetch the story details from the repository
            val result = repository.getDetail(token, id)
            _detail.value = result
            _isLoading.value = false // Update loading state
        }
    }
}
