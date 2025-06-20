package com.android.tripbook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.tripbook.base.BaseUITest
import org.junit.Test

class ThemeTest : BaseUITest() {
    @Test
    fun themeSwitch_togglesDarkMode() {
        waitForUI()
        // Open settings
        getTestRule().onNodeWithTag("menu_button").performClick()
        getTestRule().onNodeWithText("Settings").performClick()

        // Find and click theme switch
        getTestRule().onNodeWithTag("theme_switch").performClick()
        
        // Verify dark mode is applied
        getTestRule().onNodeWithTag("dark_mode_indicator").assertIsDisplayed()
        
        // Toggle back to light mode
        getTestRule().onNodeWithTag("theme_switch").performClick()
    }

    @Test
    fun colorScheme_isAppliedCorrectly() {
        waitForUI()
        // Verify primary color is applied
        getTestRule().onNodeWithTag("primary_surface").assertIsDisplayed()
        
        // Verify secondary color is applied
        getTestRule().onNodeWithTag("secondary_surface").assertIsDisplayed()
        
        // Verify tertiary color is applied
        getTestRule().onNodeWithTag("tertiary_surface").assertIsDisplayed()
    }

    @Test
    fun typography_isAppliedCorrectly() {
        waitForUI()
        // Verify different text styles
        getTestRule().onNodeWithTag("headline_text").assertIsDisplayed()
        getTestRule().onNodeWithTag("body_text").assertIsDisplayed()
        getTestRule().onNodeWithTag("caption_text").assertIsDisplayed()
    }
} 