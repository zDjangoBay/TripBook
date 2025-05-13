package com.android.tripbook.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing app theme settings
 */
class ThemeViewModel : ViewModel() {
    // Theme mode state (light, dark, system)
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()
    
    /**
     * Update the theme mode
     */
    fun updateThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }
}

/**
 * Enum for theme modes
 */
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}
