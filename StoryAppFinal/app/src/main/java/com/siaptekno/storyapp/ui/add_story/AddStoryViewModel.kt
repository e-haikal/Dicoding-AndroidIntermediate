package com.siaptekno.storyapp.ui.add_story

import androidx.lifecycle.MutableLiveData
import com.siaptekno.storyapp.data.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siaptekno.storyapp.data.remote.Repository
import com.siaptekno.storyapp.data.remote.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

// ViewModel to manage data for adding a story
class AddStoryViewModel(private val repository: Repository) : ViewModel() {

    // LiveData to observe loading state
    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: MutableLiveData<Boolean> = _isLoading

    // LiveData to observe upload result
    private val _uploadResult = MutableLiveData<Result<AddStoryResponse>>()
    var uploadResult: MutableLiveData<Result<AddStoryResponse>> = _uploadResult

    /**
     * Upload a story to the server.
     * @param token Authentication token
     * @param description Story description as RequestBody
     * @param photo Story image as MultipartBody.Part
     * @param lat Optional latitude for the story
     * @param lon Optional longitude for the story
     */
    fun uploadStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        _isLoading.value = true // Show loading indicator
        viewModelScope.launch {
            val result = repository.addStory(token, description, photo, lat, lon)
            _uploadResult.value = result // Update upload result LiveData
            _isLoading.value = false // Hide loading indicator
        }
    }
}
