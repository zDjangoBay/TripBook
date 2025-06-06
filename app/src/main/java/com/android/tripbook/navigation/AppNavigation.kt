package com.android.tripbook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.tripbook.posts.screens.CreatePostScreen
import com.android.tripbook.posts.screens.PostDetailScreen
import com.android.tripbook.posts.screens.PostListScreen
import com.android.tripbook.posts.viewmodel.PostViewModel

@Composable
fun AppNavigation(
    postViewModel: PostViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "post_list"
    ) {
        composable("post_list") {
            PostListScreen(
                viewModel = postViewModel,
                onNavigateToCreatePost = {
                    navController.navigate("create_post")
                },
                onNavigateToPostDetail = { postId ->
                    navController.navigate("post_detail/$postId")
                }
            )
        }
        
        composable("create_post") {
            CreatePostScreen(
                viewModel = postViewModel,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        
        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                viewModel = postViewModel,
                postId = postId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}