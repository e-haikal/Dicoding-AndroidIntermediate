package com.siaptekno.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch
import com.siaptekno.storyapp.data.Result

class LoginViewModel(private val repository: Repository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginResult.value = result
            _isLoading.value = false
        }
    }
}