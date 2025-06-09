package com.android.tripbook.utils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Utility functions for UI testing
 */
object TestUtils {
    /**
     * Get the current activity
     */
    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            currentActivity = ActivityLifecycleMonitorRegistry.getInstance()
                .getActivitiesInStage(Stage.RESUMED)
                .elementAtOrNull(0)
        }
        return currentActivity
    }

    /**
     * Get application context
     */
    fun getContext(): Context = ApplicationProvider.getApplicationContext()

    /**
     * Wait for a condition with timeout
     */
    fun waitForCondition(timeoutMs: Long = 5000L, condition: () -> Boolean) {
        val latch = CountDownLatch(1)
        var conditionMet = false
        
        Thread {
            val startTime = System.currentTimeMillis()
            while (!conditionMet && System.currentTimeMillis() - startTime < timeoutMs) {
                if (condition()) {
                    conditionMet = true
                    latch.countDown()
                }
                Thread.sleep(100)
            }
        }.start()
        
        latch.await(timeoutMs, TimeUnit.MILLISECONDS)
        if (!conditionMet) {
            throw AssertionError("Condition not met within timeout")
        }
    }

    /**
     * Wait for view to be visible
     */
    fun waitForView(timeoutMs: Long = 5000L, view: () -> View?) {
        waitForCondition(timeoutMs) { view() != null && view()!!.visibility == View.VISIBLE }
    }

    /**
     * Print Compose UI hierarchy for debugging
     */
    fun ComposeTestRule.dumpSemanticNodes() {
        onRoot().printToLog("UI Hierarchy")
    }
} 