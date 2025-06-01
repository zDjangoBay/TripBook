package com.android.tripbook.posts

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SubmitPostButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun submitPostButton_click_triggersCallback() {
        var clicked = false

        composeTestRule.setContent {
            SubmitPostButton(onClick = { clicked = true })
        }

        composeTestRule.onNodeWithText("Submit Post").performClick()
        assert(clicked)
    }

    @Test
    fun submitPostButton_disabled_doesNotTriggerCallback() {
        var clicked = false

        composeTestRule.setContent {
            SubmitPostButton(
                onClick = { clicked = true },
                enabled = false
            )
        }

        composeTestRule.onNodeWithText("Submit Post").performClick()
        assert(!clicked)
    }
}

