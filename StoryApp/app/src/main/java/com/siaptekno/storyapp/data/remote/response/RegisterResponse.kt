package com.siaptekno.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

// Represents the response for a registration request.
data class RegisterResponse(

    @field:SerializedName("error")
    val error: Boolean, // Indicates if there was an error during registration.

    @field:SerializedName("message")
    val message: String // A message describing the registration status.
)
