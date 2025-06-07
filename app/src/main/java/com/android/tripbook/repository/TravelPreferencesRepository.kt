package com.android.tripbook.repository

import android.content.Context
import com.android.tripbook.data.model.TravelPreferences
import com.google.gson.Gson

class TravelPreferencesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("travel_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getPreferences(): TravelPreferences {
        val json = prefs.getString("prefs_json", null)
        return if (json != null) gson.fromJson(json, TravelPreferences::class.java)
        else TravelPreferences()
    }

    fun savePreferences(preferences: TravelPreferences) {
        val json = gson.toJson(preferences)
        prefs.edit().putString("prefs_json", json).apply()
    }
}
