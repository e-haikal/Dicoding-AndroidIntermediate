package com.siaptekno.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

// Represents the response for a login request.
data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: LoginResult, // Contains user information after a successful login.

    @field:SerializedName("error")
    val error: Boolean, // Indicates if there was an error during login.

    @field:SerializedName("message")
    val message: String // A descriptive message about the login status.
)

// Represents the details of a successful login result.
data class LoginResult(

    @field:SerializedName("name")
    val name: String, // The user's name.

    @field:SerializedName("userId")
    val userId: String, // The user's unique ID.

    @field:SerializedName("token")
    val token: String // Authentication token for the user.
)
