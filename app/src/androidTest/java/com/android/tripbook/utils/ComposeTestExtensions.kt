package com.android.tripbook.utils

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp

/**
 * Extension functions for Compose UI testing
 */

/**
 * Performs a scroll to the specified index in a lazy list
 */
fun SemanticsNodeInteraction.performScrollToIndex(index: Int) {
    var currentIndex = 0
    while (currentIndex < index) {
        performTouchInput { 
            swipeUp()
        }
        performScrollTo()
        currentIndex++
    }
} 