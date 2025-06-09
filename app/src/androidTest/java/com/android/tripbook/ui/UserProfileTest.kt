package com.android.tripbook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.tripbook.base.BaseUITest
import org.junit.Test

class UserProfileTest : BaseUITest() {
    @Test
    fun userProfileHeader_isDisplayed() {
        waitForUI()
        // Verify profile header components
        getTestRule().onNodeWithTag("profile_avatar").assertIsDisplayed()
        getTestRule().onNodeWithTag("profile_name").assertIsDisplayed()
        getTestRule().onNodeWithTag("profile_bio").assertIsDisplayed()
    }

    @Test
    fun editProfile_updatesUserInfo() {
        waitForUI()
        // Click edit profile button
        getTestRule().onNodeWithTag("edit_profile_button").performClick()
        
        // Update profile information
        getTestRule().onNodeWithTag("name_input").performTextInput("John Doe")
        getTestRule().onNodeWithTag("bio_input").performTextInput("Travel enthusiast")
        
        // Save changes
        getTestRule().onNodeWithTag("save_profile_button").performClick()
        
        // Verify updates
        getTestRule().onNodeWithTag("profile_name").assertTextEquals("John Doe")
        getTestRule().onNodeWithTag("profile_bio").assertTextEquals("Travel enthusiast")
    }

    @Test
    fun profileStats_areDisplayed() {
        waitForUI()
        // Verify stats components
        getTestRule().onNodeWithTag("trips_count").assertIsDisplayed()
        getTestRule().onNodeWithTag("followers_count").assertIsDisplayed()
        getTestRule().onNodeWithTag("following_count").assertIsDisplayed()
    }

    @Test
    fun profileActions_areAccessible() {
        waitForUI()
        // Verify action buttons
        getTestRule().onNodeWithTag("share_profile_button").assertIsDisplayed()
        getTestRule().onNodeWithTag("settings_button").assertIsDisplayed()
        
        // Test settings navigation
        getTestRule().onNodeWithTag("settings_button").performClick()
        getTestRule().onNodeWithText("Profile Settings").assertIsDisplayed()
    }
} 