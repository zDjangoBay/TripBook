package com.example.tohpoh.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserSession(userId: Int) {
        prefs.edit()
            .putInt("user_id", userId)
            .apply()
    }

    fun getUserSession(): Int? {
        val id = prefs.getInt("user_id", -1)
        return if (id != -1) id else null
    }

    fun isLoggedIn(): Boolean {
        return getUserSession() != null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
