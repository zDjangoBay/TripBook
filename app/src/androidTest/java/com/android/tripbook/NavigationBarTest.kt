package com.android.tripbook

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class NavigationBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun clickOnNavigationItem_navigatesToCorrectScreen() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            NavGraph(navController = navController) // Your navigation setup with screens
        }

        // Click on Home
        composeTestRule.onNodeWithContentDescription("Home").performClick()
        composeTestRule.onNodeWithText("Welcome to the Feed Screen").assertIsDisplayed()

        // Click on Post
        composeTestRule.onNodeWithContentDescription("Post").performClick()
        composeTestRule.onNodeWithText("Create a New Post").assertIsDisplayed()

        // Click on Profile
        composeTestRule.onNodeWithContentDescription("Profile").performClick()
        composeTestRule.onNodeWithText("Saha Tiomela Randy").assertIsDisplayed()
    }
}
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "feed") {
        composable("feed") {
            FeedScreen()
        }
        composable("post") {
            PostScreen()
        }
        composable("profile") {
            ProfileScreen(navController)
        }
    }
}

