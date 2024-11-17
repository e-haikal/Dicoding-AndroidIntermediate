package com.siaptekno.storyapp.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch
import com.siaptekno.storyapp.data.Result

class DetailViewModel(private val repository: Repository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: MutableLiveData<Boolean> = _isLoading

    private val _detail = MutableLiveData<Result<Story>>()
    var detail: MutableLiveData<Result<Story>> = _detail

    fun getDetail(token: String, id: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getDetail(token, id)
            _detail.value = result
            _isLoading.value = false
        }
    }
}