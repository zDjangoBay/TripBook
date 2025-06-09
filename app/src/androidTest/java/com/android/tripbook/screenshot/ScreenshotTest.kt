package com.android.tripbook.screenshot

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import com.android.tripbook.base.BaseUITest
import com.facebook.testing.screenshot.Screenshot
import com.facebook.testing.screenshot.ViewHelpers
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName
import java.io.File
import java.io.FileOutputStream

/**
 * Base class for screenshot tests
 */
abstract class ScreenshotTest : BaseUITest() {
    @get:Rule
    val testName = TestName()

    @Before
    override fun setup() {
        super.setup()
        // Additional setup for screenshot testing
    }

    /**
     * Capture and save a screenshot
     */
    protected fun captureScreenshot(name: String) {
        waitForUI()
        
        // Capture the screen
        val bitmap = getTestRule().onRoot().captureToImage().asAndroidBitmap()
        
        // Save the screenshot
        saveScreenshot(name, bitmap)
        
        // Record with Facebook's screenshot testing library
        Screenshot.snap(getTestRule().activity.window.decorView)
            .setName(name)
            .record()
    }

    /**
     * Save bitmap to file
     */
    private fun saveScreenshot(name: String, bitmap: Bitmap) {
        val screenshotDirectory = File(context.filesDir, "screenshots").apply { mkdirs() }
        val screenshotFile = File(screenshotDirectory, "${testName.methodName}_$name.png")
        
        FileOutputStream(screenshotFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    /**
     * Compare screenshots with a threshold
     */
    protected fun compareScreenshots(
        firstScreenshot: File,
        secondScreenshot: File,
        threshold: Float = 0.1f
    ): Boolean {
        // Implement screenshot comparison logic
        // This is a placeholder - you would implement actual comparison logic here
        return true
    }
} 