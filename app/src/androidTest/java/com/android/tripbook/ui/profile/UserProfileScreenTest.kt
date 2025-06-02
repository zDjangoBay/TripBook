package com.android.tripbook.ui.profile

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.android.tripbook.R
import com.android.tripbook.base.BaseUITest
import com.facebook.testing.screenshot.Screenshot
import org.junit.Test
import org.hamcrest.Matchers.not

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

    @Test
    fun testEmptyFieldValidation() {
        // Navigate to edit profile
        onView(withId(R.id.edit_profile_button))
            .perform(click())

        // Clear all fields
        onView(withId(R.id.edit_name))
            .perform(clearText())
        onView(withId(R.id.edit_bio))
            .perform(clearText())

        // Try to save
        onView(withId(R.id.save_button))
            .perform(click())

        // Verify error messages are displayed
        onView(withId(R.id.name_error))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.error_name_required)))

        // Take screenshot of validation errors
        Screenshot.snap(activity.window.decorView)
            .setName("profile_validation_errors")
            .record()
    }

    @Test
    fun testProfileImageUpload() {
        // Navigate to edit profile
        onView(withId(R.id.edit_profile_button))
            .perform(click())

        // Click on profile image to trigger upload
        onView(withId(R.id.profile_image))
            .perform(click())

        // Verify image picker options are displayed
        onView(withId(R.id.image_picker_options))
            .check(matches(isDisplayed()))

        // Verify camera option is displayed
        onView(withId(R.id.camera_option))
            .check(matches(isDisplayed()))

        // Verify gallery option is displayed
        onView(withId(R.id.gallery_option))
            .check(matches(isDisplayed()))

        // Take screenshot of image picker options
        Screenshot.snap(activity.window.decorView)
            .setName("profile_image_picker")
            .record()
    }

    @Test
    fun testCancelProfileEdit() {
        // Navigate to edit profile
        onView(withId(R.id.edit_profile_button))
            .perform(click())

        // Enter new information
        onView(withId(R.id.edit_name))
            .perform(clearText(), typeText("Test Name"))
        onView(withId(R.id.edit_bio))
            .perform(clearText(), typeText("Test Bio"))

        // Click cancel button
        onView(withId(R.id.cancel_button))
            .perform(click())

        // Verify original profile data remains unchanged
        onView(withId(R.id.profile_name))
            .check(matches(not(withText("Test Name"))))
        onView(withId(R.id.profile_bio))
            .check(matches(not(withText("Test Bio"))))

        // Take screenshot of unchanged profile
        Screenshot.snap(activity.window.decorView)
            .setName("profile_edit_cancelled")
            .record()
    }

    @Test
    fun testMaxLengthValidation() {
        // Navigate to edit profile
        onView(withId(R.id.edit_profile_button))
            .perform(click())

        // Try to enter text exceeding maximum length
        val longName = "A".repeat(51) // Assuming max length is 50
        val longBio = "A".repeat(201) // Assuming max length is 200

        onView(withId(R.id.edit_name))
            .perform(clearText(), typeText(longName))
        onView(withId(R.id.edit_bio))
            .perform(clearText(), typeText(longBio))

        // Try to save
        onView(withId(R.id.save_button))
            .perform(click())

        // Verify error messages
        onView(withId(R.id.name_error))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.error_name_too_long)))

        onView(withId(R.id.bio_error))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.error_bio_too_long)))

        // Take screenshot of length validation errors
        Screenshot.snap(activity.window.decorView)
            .setName("profile_length_validation")
            .record()
    }
} 