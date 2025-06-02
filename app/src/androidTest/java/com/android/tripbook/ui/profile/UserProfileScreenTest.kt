package com.android.tripbook.ui.profile

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.android.tripbook.R
import com.android.tripbook.base.BaseUITest
import com.facebook.testing.screenshot.Screenshot
import org.junit.Test

class UserProfileScreenTest : BaseUITest() {

    @Test
    fun testProfileScreenDisplayed() {
        // Navigate to profile screen
        // This depends on your navigation implementation
        
        // Verify profile elements are displayed
        onView(withId(R.id.profile_image))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.profile_name))
            .check(matches(isDisplayed()))
            
        onView(withId(R.id.profile_bio))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testEditProfileFlow() {
        // Navigate to edit profile
        onView(withId(R.id.edit_profile_button))
            .perform(click())

        // Enter new profile information
        onView(withId(R.id.edit_name))
            .perform(clearText(), typeText("John Doe"))

        onView(withId(R.id.edit_bio))
            .perform(clearText(), typeText("Test Bio"))

        // Save changes
        onView(withId(R.id.save_button))
            .perform(click())

        // Verify changes are displayed
        onView(withId(R.id.profile_name))
            .check(matches(withText("John Doe")))

        onView(withId(R.id.profile_bio))
            .check(matches(withText("Test Bio")))
    }

    @Test
    fun testProfileScreenScreenshot() {
        // Navigate to profile screen
        // Take screenshot of the entire screen
        Screenshot.snap(activity.window.decorView)
            .setName("user_profile_screen")
            .record()
    }
} 