package com.android.tripbook.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.tripbook.ComposeTestHelper
import com.android.tripbook.runComposeTest
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestHelper = ComposeTestHelper()

    @Test
    fun `navigation bar items are selectable`() {
        composeTestHelper.runComposeTest({
            var selectedItem by remember { mutableStateOf(0) }
            
            NavigationBar(
                modifier = androidx.compose.ui.Modifier.testTag("bottom_navigation")
            ) {
                NavigationBarItem(
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 },
                    icon = { },
                    label = { Text("Home") },
                    modifier = androidx.compose.ui.Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 },
                    icon = { },
                    label = { Text("Profile") },
                    modifier = androidx.compose.ui.Modifier.testTag("nav_profile")
                )
            }
        }) {
            // Verify initial state
            onNodeWithTag("nav_home").assertIsSelected()
            
            // Click profile and verify selection
            onNodeWithTag("nav_profile").performClick()
            onNodeWithTag("nav_profile").assertIsSelected()
            
            // Click home and verify selection
            onNodeWithTag("nav_home").performClick()
            onNodeWithTag("nav_home").assertIsSelected()
        }
    }
} 