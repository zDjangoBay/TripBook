package com.android.tripbook.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onNodeWithTag
import com.android.tripbook.base.BaseUITest
import org.junit.Test

class AccessibilityTest : BaseUITest() {
    @Test
    fun allClickableElements_haveContentDescription() {
        waitForUI()
        // Verify all clickable elements have content descriptions
        getTestRule().onAllNodes(hasClickAction())
            .fetchSemanticsNodes()
            .forEach { node ->
                assert(node.config.contains(androidx.compose.ui.semantics.SemanticsProperties.ContentDescription))
            }
    }

    @Test
    fun navigationElements_areAccessible() {
        waitForUI()
        // Verify navigation elements are accessible
        getTestRule().onNodeWithTag("nav_home").assertHasClickAction()
        getTestRule().onNodeWithTag("nav_trips").assertHasClickAction()
        getTestRule().onNodeWithTag("nav_profile").assertHasClickAction()
    }

    @Test
    fun textFields_haveProperLabels() {
        waitForUI()
        // Navigate to edit profile
        getTestRule().onNodeWithTag("edit_profile_button").performClick()
        
        // Verify text fields have proper labels
        getTestRule().onAllNodes(hasContentDescription())
            .fetchSemanticsNodes()
            .forEach { node ->
                assert(node.config.contains(androidx.compose.ui.semantics.SemanticsProperties.Text))
            }
    }

    @Test
    fun disabledElements_areProperlyMarked() {
        waitForUI()
        // Navigate to edit profile
        getTestRule().onNodeWithTag("edit_profile_button").performClick()
        
        // Initially save button should be disabled
        getTestRule().onNodeWithTag("save_profile_button").assertIsNotEnabled()
        
        // After entering text, save button should be enabled
        getTestRule().onNodeWithTag("name_input").performTextInput("Test Name")
        getTestRule().onNodeWithTag("save_profile_button").assertIsEnabled()
    }
} 