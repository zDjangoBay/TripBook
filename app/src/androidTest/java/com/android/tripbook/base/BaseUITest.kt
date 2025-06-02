package com.android.tripbook.base

import androidx.test.espresso.IdlingRegistry
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.tripbook.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class BaseUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    protected val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    open fun setup() {
        // Setup code that runs before each test
        // Register any custom idling resources here
    }

    @After
    open fun tearDown() {
        // Cleanup code that runs after each test
        // Unregister any custom idling resources here
        IdlingRegistry.getInstance().resources.forEach { 
            IdlingRegistry.getInstance().unregister(it)
        }
    }

    protected fun waitForIdle() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
} 