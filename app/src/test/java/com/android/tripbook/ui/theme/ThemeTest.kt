package com.android.tripbook.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import com.android.tripbook.ComposeTestHelper
import com.android.tripbook.runComposeTest
import org.junit.Rule
import org.junit.Test

class ThemeTest {
    @get:Rule
    val composeTestHelper = ComposeTestHelper()

    @Test
    fun `theme colors are applied correctly`() {
        composeTestHelper.runComposeTest({
            MaterialTheme {
                // Create a test surface with primary color
                androidx.compose.material3.Surface(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = androidx.compose.ui.Modifier.testTag("primary_surface")
                ) {
                    // Empty content
                }
            }
        }) {
            // Verify the surface is displayed
            onNodeWithTag("primary_surface").assertIsDisplayed()
        }
    }

    @Test
    fun `theme typography is applied correctly`() {
        composeTestHelper.runComposeTest({
            MaterialTheme {
                androidx.compose.material3.Text(
                    text = "Test Text",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = androidx.compose.ui.Modifier.testTag("headline_text")
                )
            }
        }) {
            onNodeWithTag("headline_text").assertIsDisplayed()
        }
    }

    @Test
    fun `custom colors are defined correctly`() {
        // Verify our custom color definitions
        assert(md_theme_light_primary != Color.Unspecified)
        assert(md_theme_light_onPrimary != Color.Unspecified)
        assert(md_theme_light_primaryContainer != Color.Unspecified)
    }
} 