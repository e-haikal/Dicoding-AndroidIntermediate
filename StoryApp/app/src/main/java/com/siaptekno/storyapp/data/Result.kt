package com.siaptekno.storyapp.data

// A sealed class to represent different states of a result in data operations.
sealed class Result<out R> private constructor() {

    // Represents a successful operation, containing the result data.
    data class Success<out T>(val data: T) : Result<T>()

    // Represents a failed operation, containing an error message.
    data class Error(val error: String) : Result<Nothing>()

    // Represents a loading state, often used for UI indications.
    object Loading : Result<Nothing>()
}
