package com.android.tripbook.userprofilebradley

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.userprofilebradley.ui.screen.PostCreationScreen
import com.android.tripbook.userprofilebradley.ui.screen.PostFeedScreen
import com.android.tripbook.userprofilebradley.viewmodel.PostListViewModel

@Composable
fun UserProfileBradleyApp(
    navController: NavHostController = rememberNavController()
) {
    val postListViewModel: PostListViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "post_feed"
    ) {
        composable("post_feed") {
            PostFeedScreen(
                onCreatePost = {
                    navController.navigate("create_post")
                },
                viewModel = postListViewModel
            )
        }

        composable("create_post") {
            PostCreationScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPostPublished = {
                    navController.popBackStack()
                },
                postListViewModel = postListViewModel
            )
        }
    }
}
