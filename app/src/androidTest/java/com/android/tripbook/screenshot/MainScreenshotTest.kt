package com.android.tripbook.screenshot

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Test

class MainScreenshotTest : ScreenshotTest() {
    @Test
    fun captureHomeScreen() {
        waitForUI()
        // Navigate to home if not already there
        getTestRule().onNodeWithTag("nav_home").performClick()
        // Capture the screenshot
        captureScreenshot("home_screen")
    }

    @Test
    fun captureProfileScreen() {
        waitForUI()
        // Navigate to profile
        getTestRule().onNodeWithTag("nav_profile").performClick()
        // Capture the screenshot
        captureScreenshot("profile_screen")
    }

    @Test
    fun captureTripsScreen() {
        waitForUI()
        // Navigate to trips
        getTestRule().onNodeWithTag("nav_trips").performClick()
        // Capture the screenshot
        captureScreenshot("trips_screen")
    }

    @Test
    fun captureSettingsScreen() {
        waitForUI()
        // Open menu and navigate to settings
        getTestRule().onNodeWithTag("menu_button").performClick()
        getTestRule().onNodeWithTag("settings_menu_item").performClick()
        // Capture the screenshot
        captureScreenshot("settings_screen")
    }

    @Test
    fun captureDarkModeTransition() {
        waitForUI()
        // Capture light mode
        captureScreenshot("light_mode")
        
        // Switch to dark mode
        getTestRule().onNodeWithTag("menu_button").performClick()
        getTestRule().onNodeWithTag("settings_menu_item").performClick()
        getTestRule().onNodeWithTag("theme_switch").performClick()
        
        // Capture dark mode
        captureScreenshot("dark_mode")
    }
} 