package com.siaptekno.storyapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.launch
import com.siaptekno.storyapp.data.Result

class RegisterViewModel(private val repository: Repository): ViewModel() {

    // LiveData to track loading state for showing/hiding progress bar
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData to store the result of the registration process (success/error/loading)
    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    // Function to initiate the registration process
    fun register(name: String, email: String, password: String) {
        _isLoading.value = true // Show loading indicator
        viewModelScope.launch {
            // Call the repository function to register a user and capture the result
            val result = repository.register(name, email, password)
            _registerResult.value = result // Post the result to LiveData
            _isLoading.value = false // Hide loading indicator
        }
    }
}
