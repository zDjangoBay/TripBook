package com.android.tripbook.util

import android.app.Activity
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException

class ScreenshotTestRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    base.evaluate()
                } catch (t: Throwable) {
                    // If the test fails, take a screenshot
                    val activity = getActivityInstance()
                    activity?.let {
                        val screenCapture = Screenshot.capture(it)
                        val testClass = description.className
                        val testName = description.methodName
                        screenCapture.name = "${testClass}_${testName}_failure"
                        try {
                            screenCapture.record()
                        } catch (e: IOException) {
                            throw RuntimeException("Failed to save screenshot", e)
                        }
                    }
                    throw t
                }
            }
        }
    }

    private fun getActivityInstance(): Activity? {
        // Implementation to get current activity
        // This would depend on your app's architecture
        return null // TODO: Implement this based on your app's architecture
    }
} 