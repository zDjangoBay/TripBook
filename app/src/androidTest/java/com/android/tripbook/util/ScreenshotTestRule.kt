package com.android.tripbook.util

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException

class ScreenshotTestRule(
    private val capturePortrait: Boolean = true,
    private val captureLandscape: Boolean = true
) : TestRule {
    
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    if (capturePortrait) {
                        setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        captureScreenshot(description, "portrait")
                    }
                    
                    if (captureLandscape) {
                        setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                        captureScreenshot(description, "landscape")
                    }
                    
                    base.evaluate()
                } catch (t: Throwable) {
                    // If the test fails, take a screenshot in current orientation
                    captureFailureScreenshot(description)
                    throw t
                } finally {
                    // Reset orientation
                    setOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                }
            }
        }
    }

    private fun captureScreenshot(description: Description, orientation: String) {
        val activity = getActivityInstance()
        activity?.let {
            // Wait for any animations to complete
            Thread.sleep(500)
            
            val screenCapture = Screenshot.capture(it)
            val testClass = description.className.substringAfterLast('.')
            val testName = description.methodName
            screenCapture.name = "${testClass}_${testName}_${orientation}"
            try {
                screenCapture.record()
            } catch (e: IOException) {
                throw RuntimeException("Failed to save screenshot", e)
            }
        }
    }

    private fun captureFailureScreenshot(description: Description) {
        val activity = getActivityInstance()
        activity?.let {
            val screenCapture = Screenshot.capture(it)
            val testClass = description.className.substringAfterLast('.')
            val testName = description.methodName
            screenCapture.name = "${testClass}_${testName}_failure"
            try {
                screenCapture.record()
            } catch (e: IOException) {
                // Log error but don't fail the test
                e.printStackTrace()
            }
        }
    }

    private fun setOrientation(orientation: Int) {
        val activity = getActivityInstance()
        activity?.requestedOrientation = orientation
        Thread.sleep(1000) // Wait for orientation change
    }

    private fun getActivityInstance(): Activity? {
        var activity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
            activity = resumedActivities.firstOrNull()
        }
        return activity
    }
} 