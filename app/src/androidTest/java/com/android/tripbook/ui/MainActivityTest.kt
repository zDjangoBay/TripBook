package com.android.tripbook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.android.tripbook.base.BaseUITest
import org.junit.Test

class MainActivityTest : BaseUITest() {
    @Test
    fun mainActivity_launches_successfully() {
        // Verify that the main activity launches successfully
        waitForUI()
    }

    @Test
    fun userProfile_isDisplayed() {
        // Since we're using UserProfileNdeDilanEntryPoint, we should verify it's displayed
        waitForUI()
        
        // Add specific assertions for your user profile UI elements
        // Note: You'll need to add testTags to your UI components
        getTestRule().onNodeWithTag("user_profile_container").assertIsDisplayed()
    }

    @Test
    fun navigationBar_isDisplayed() {
        waitForUI()
        // Verify navigation components are displayed
        getTestRule().onNodeWithTag("bottom_navigation").assertIsDisplayed()
    }
} 