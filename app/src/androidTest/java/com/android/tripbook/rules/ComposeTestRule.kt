package com.android.tripbook.rules

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Custom test rule for Compose UI testing that provides additional functionality
 */
class ComposeTestRule : TestRule {
    private val composeRule = createAndroidComposeRule<ComponentActivity>()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    // Setup before test
                    setupTest()
                    
                    // Run the test
                    base.evaluate()
                } finally {
                    // Cleanup after test
                    cleanupTest()
                }
            }
        }
    }

    private fun setupTest() {
        // Add any setup code here
        composeRule.activityRule.scenario.onActivity { activity ->
            // Configure activity for testing
            activity.window.decorView.keepScreenOn = true
        }
    }

    private fun cleanupTest() {
        // Add any cleanup code here
        composeRule.activityRule.scenario.close()
    }

    // Expose the underlying compose rule
    fun getComposeRule() = composeRule
}

/**
 * Custom test rule for handling screenshots in UI tests
 */
class ScreenshotTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    // Setup screenshot testing
                    setupScreenshotTesting()
                    
                    // Run the test
                    base.evaluate()
                } finally {
                    // Cleanup screenshot testing
                    cleanupScreenshotTesting()
                }
            }
        }
    }

    private fun setupScreenshotTesting() {
        // Initialize screenshot testing framework
    }

    private fun cleanupScreenshotTesting() {
        // Cleanup screenshot testing resources
    }
} 