package com.android.tripbook

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * A test rule that sets up a compose test environment
 */
class ComposeTestHelper : TestRule {
    private val composeRule = createComposeRule()

    override fun apply(base: Statement, description: Description): Statement {
        return composeRule.apply(base, description)
    }

    fun setTestContent(content: @Composable () -> Unit) {
        composeRule.setContent(content)
    }

    fun getTestRule(): ComposeContentTestRule = composeRule
}

/**
 * Extension function to run a test with compose content
 */
fun ComposeTestHelper.runComposeTest(
    testContent: @Composable () -> Unit,
    test: ComposeContentTestRule.() -> Unit
) {
    setTestContent(testContent)
    getTestRule().test()
} 