package com.android.tripbook

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.hamcrest.CoreMatchers.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.android.tripbook", appContext.packageName)
    }

    @Test
    fun testOTPInputField() {
        // Test if OTP input field is displayed
        onView(withId(R.id.otp_input)).check(matches(isDisplayed()))
    }

    @Test
    fun testSendOTPButton() {
        // Check if the Send OTP button is displayed and clickable
        onView(withId(R.id.send_otp_button))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }

    @Test
    fun testOTPVerification() {
        // Assuming the OTP is "123456"
        val generatedOTP = "123456"

        // Simulate sending OTP
        onView(withId(R.id.send_otp_button)).perform(click())

        // Simulate entering the OTP
        onView(withId(R.id.otp_input)).perform(typeText(generatedOTP), closeSoftKeyboard())

        // Simulate clicking the Verify button
        onView(withId(R.id.verify_button)).perform(click())

        // Check that the correct verification message is displayed
        onView(withText("OTP verified successfully!")).check(matches(isDisplayed()))
    }

    @Test
    fun testInvalidOTP() {
        // Simulate sending OTP
        onView(withId(R.id.send_otp_button)).perform(click())

        // Simulate entering an invalid OTP
        onView(withId(R.id.otp_input)).perform(typeText("wrongotp"), closeSoftKeyboard())

        // Simulate clicking the Verify button
        onView(withId(R.id.verify_button)).perform(click())

        // Check that the error message is displayed
        onView(withText("Invalid OTP. Please try again.")).check(matches(isDisplayed()))
    }
}
