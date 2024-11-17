package com.siaptekno.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch
import com.siaptekno.storyapp.data.Result

// ViewModel for handling login logic and managing UI-related data in a lifecycle-aware way
class LoginViewModel(private val repository: Repository) : ViewModel() {

    // LiveData to track the loading state during the login process
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading // Exposed immutable LiveData for UI observation

    // LiveData to store the result of the login API call
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult // Exposed immutable LiveData for UI observation

    // Function to initiate the login process
    fun login(email: String, password: String) {
        // Set loading state to true when the login process starts
        _isLoading.value = true

        // Launch a coroutine in the ViewModel's scope for asynchronous API call
        viewModelScope.launch {
            // Call the repository function to perform the login operation
            val result = repository.login(email, password)

            // Update the login result LiveData with the result from the repository
            _loginResult.value = result

            // Set loading state to false after completing the login process
            _isLoading.value = false
        }
    }
}
