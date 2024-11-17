package com.siaptekno.storyapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.ListStoryItem

class MainViewModel(private val repository: Repository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun stories(token: String): LiveData<PagingData<ListStoryItem>> {
        _isLoading.value = true
        return repository.getStoriesPaging(token)
            .cachedIn(viewModelScope)
            .also {
                _isLoading.value = false
            }
    }


}