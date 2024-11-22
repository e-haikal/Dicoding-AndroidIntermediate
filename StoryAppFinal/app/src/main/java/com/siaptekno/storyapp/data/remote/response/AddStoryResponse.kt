package com.siaptekno.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

// Represents the response of adding a story to the API.
data class AddStoryResponse(

    @field:SerializedName("error")
    val error: Boolean, // Indicates if there was an error in the request.

    @field:SerializedName("message")
    val message: String // A descriptive message about the request's success or failure.
)
