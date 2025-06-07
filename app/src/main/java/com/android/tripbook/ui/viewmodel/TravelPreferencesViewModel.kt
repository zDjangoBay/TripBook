package com.android.tripbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.tripbook.data.model.TravelPreferences
import com.android.tripbook.repository.TravelPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TravelPreferencesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TravelPreferencesRepository(application.applicationContext)

    // private mutable state
    private val _travelPreferences = MutableStateFlow(repository.getPreferences())

    // public read-only state
    val travelPreferences: StateFlow<TravelPreferences> = _travelPreferences

    fun savePreferences(prefs: TravelPreferences) {
        repository.savePreferences(prefs)
        _travelPreferences.value = prefs // ✅ No compilation error here
    }
}
