package com.tripbook.userprofileManfoDjuiko.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tripbook.userprofileManfoDjuiko.presentation.screens.MediaManagementScreen
import com.tripbook.userprofileManfoDjuiko.presentation.screens.MediaDetailScreen
import com.tripbook.userprofileManfoDjuiko.presentation.screens.MediaUploadScreen

sealed class MediaScreens(val route: String) {
    object MediaManagement : MediaScreens("media_management")
    object MediaDetail : MediaScreens("media_detail/{mediaId}")
    object MediaUpload : MediaScreens("media_upload")

    fun createRoute(vararg args: String): String {
        var route = this.route
        args.forEach { arg ->
            route = route.replaceFirst("{${route.substringAfter("{").substringBefore("}")}", arg)
        }
        return route
    }
}

@Composable
fun MediaNavigationHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = MediaScreens.MediaManagement.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MediaScreens.MediaManagement.route) {
            MediaManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(MediaScreens.MediaDetail.route) { backStackEntry ->
            val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
            MediaDetailScreen(
                mediaItem = getMediaItemById(mediaId), // Implement this function
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEdit = {
                    // Handle edit action
                },
                onDelete = {
                    // Handle delete action
                    navController.popBackStack()
                },
                onShare = {
                    // Handle share action
                }
            )
        }

        composable(MediaScreens.MediaUpload.route) {
            MediaUploadScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUploadComplete = { uris ->
                    // Handle upload completion
                    navController.popBackStack()
                }
            )
        }
    }
}

// Helper function - implement based on your data source
private fun getMediaItemById(mediaId: String): com.tripbook.userprofileManfoDjuiko.data.model.MediaItem {
    // This is a placeholder - implement according to your data source
    return com.tripbook.userprofileManfoDjuiko.data.model.MediaItem(
        id = mediaId,
        uri = android.net.Uri.EMPTY,
        type = com.tripbook.userprofileManfoDjuiko.data.model.MediaType.IMAGE,
        name = "Sample Image",
        size = 0L,
        dateAdded = java.util.Date()
    )
}