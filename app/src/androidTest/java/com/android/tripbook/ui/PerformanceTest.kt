package com.android.tripbook.ui

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.tripbook.base.BaseUITest
import com.android.tripbook.utils.TestUtils.waitForCondition
import com.android.tripbook.utils.performScrollToIndex
import org.junit.Test
import kotlin.system.measureTimeMillis

class PerformanceTest : BaseUITest() {
    @Test
    fun navigationTransitions_areSmooth() {
        waitForUI()
        
        // Measure navigation transition times
        val transitionTime = measureTimeMillis {
            // Navigate to trips
            getTestRule().onNodeWithTag("nav_trips").performClick()
            waitForUI()
            
            // Navigate to profile
            getTestRule().onNodeWithTag("nav_profile").performClick()
            waitForUI()
            
            // Navigate back home
            getTestRule().onNodeWithTag("nav_home").performClick()
            waitForUI()
        }
        
        // Assert transition time is within acceptable range (e.g., under 1 second)
        assert(transitionTime < 1000) { "Navigation transitions took too long: $transitionTime ms" }
    }

    @Test
    fun scrolling_isSmooth() {
        waitForUI()
        
        // Navigate to trips list
        getTestRule().onNodeWithTag("nav_trips").performClick()
        
        // Measure scrolling performance
        val scrollTime = measureTimeMillis {
            // Perform scroll action
            getTestRule().onNodeWithTag("trips_list").performScrollToIndex(50)
            waitForUI()
        }
        
        // Assert scroll time is within acceptable range
        assert(scrollTime < 500) { "Scrolling took too long: $scrollTime ms" }
    }

    @Test
    fun imageLoading_isOptimized() {
        waitForUI()
        
        var imageLoadTime = 0L
        
        // Navigate to profile
        getTestRule().onNodeWithTag("nav_profile").performClick()
        
        // Measure image loading time
        imageLoadTime = measureTimeMillis {
            // Wait for profile image to load
            waitForCondition { 
                getTestRule().onNodeWithTag("profile_avatar").fetchSemanticsNode().config
                    .contains(androidx.compose.ui.semantics.SemanticsProperties.ContentDescription)
            }
        }
        
        // Assert image loading time is within acceptable range
        assert(imageLoadTime < 2000) { "Image loading took too long: $imageLoadTime ms" }
    }

    @Test
    fun appStartup_isOptimized() {
        // Measure time from activity creation to first render
        val startupTime = measureTimeMillis {
            // Wait for main content to be displayed
            waitForCondition { 
                getTestRule().onNodeWithTag("main_scaffold").fetchSemanticsNode().config
                    .contains(androidx.compose.ui.semantics.SemanticsProperties.Focused)
            }
        }
        
        // Assert startup time is within acceptable range
        assert(startupTime < 3000) { "App startup took too long: $startupTime ms" }
    }
} 