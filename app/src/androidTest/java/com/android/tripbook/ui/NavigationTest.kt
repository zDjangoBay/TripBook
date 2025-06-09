package com.android.tripbook.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.android.tripbook.base.BaseUITest
import org.junit.Test

class NavigationTest : BaseUITest() {
    @Test
    fun bottomNavigation_isDisplayed() {
        waitForUI()
        getTestRule().onNodeWithTag("bottom_navigation").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_itemsAreDisplayed() {
        waitForUI()
        // Verify all navigation items are present
        getTestRule().onNodeWithTag("nav_home").assertIsDisplayed()
        getTestRule().onNodeWithTag("nav_trips").assertIsDisplayed()
        getTestRule().onNodeWithTag("nav_profile").assertIsDisplayed()
    }

    @Test
    fun bottomNavigation_itemSelection() {
        waitForUI()
        // Verify home is selected by default
        getTestRule().onNodeWithTag("nav_home").assertIsSelected()

        // Click on trips and verify selection
        getTestRule().onNodeWithTag("nav_trips").performClick()
        getTestRule().onNodeWithTag("nav_trips").assertIsSelected()

        // Click on profile and verify selection
        getTestRule().onNodeWithTag("nav_profile").performClick()
        getTestRule().onNodeWithTag("nav_profile").assertIsSelected()
    }

    @Test
    fun navigationDrawer_opensAndCloses() {
        waitForUI()
        // Open drawer
        getTestRule().onNodeWithTag("menu_button").performClick()
        getTestRule().onNodeWithTag("navigation_drawer").assertIsDisplayed()

        // Verify drawer items
        getTestRule().onNodeWithText("Settings").assertIsDisplayed()
        getTestRule().onNodeWithText("Help").assertIsDisplayed()
        getTestRule().onNodeWithText("About").assertIsDisplayed()

        // Close drawer
        getTestRule().onNodeWithTag("close_drawer").performClick()
        // Wait for animation
        waitForUI()
    }
} 