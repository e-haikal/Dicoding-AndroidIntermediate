package com.siaptekno.storyapp.pref

import android.content.Context
import android.content.SharedPreferences

// A utility class for managing user session data using SharedPreferences.
class SessionManager(context: Context) {

    // SharedPreferences to store user session information.
    private val preferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "user_session" // SharedPreferences file name.
        private const val TOKEN_KEY = "auth_token"  // Key for storing the authentication token.
    }

    // Saves the authentication token in SharedPreferences.
    fun saveAuthToken(token: String) {
        preferences.edit().putString(TOKEN_KEY, token).apply()
    }

    // Retrieves the stored authentication token, or null if not found.
    fun getAuthToken(): String? {
        return preferences.getString(TOKEN_KEY, null)
    }

    // Clears the stored authentication token from SharedPreferences.
    fun clearAuthToken() {
        preferences.edit().remove(TOKEN_KEY).apply()
    }
}
