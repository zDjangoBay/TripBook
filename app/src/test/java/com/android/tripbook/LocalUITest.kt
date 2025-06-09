package com.android.tripbook

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [33])
class LocalUITest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun simpleTest() {
        // Simple test to verify setup
        assert(true)
    }

    @Test
    fun mainActivity_launches_successfully() {
        // Set up the content
        composeTestRule.setContent {
            MainActivity()
        }
        
        // Verify main scaffold is displayed
        composeTestRule.onNodeWithTag("main_scaffold").assertIsDisplayed()
    }

    @Test
    fun userProfile_isDisplayed() {
        composeTestRule.setContent {
            MainActivity()
        }
        
        // Verify user profile container is displayed
        composeTestRule.onNodeWithTag("user_profile_container").assertIsDisplayed()
    }

    @Test
    fun navigation_works() {
        composeTestRule.setContent {
            MainActivity()
        }
        
        // Test navigation clicks
        composeTestRule.onNodeWithTag("nav_trips").performClick()
        composeTestRule.onNodeWithTag("nav_profile").performClick()
        composeTestRule.onNodeWithTag("nav_home").performClick()
    }
} 