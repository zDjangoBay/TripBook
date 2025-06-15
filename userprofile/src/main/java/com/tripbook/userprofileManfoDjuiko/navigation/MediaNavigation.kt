package com.tripbook.userprofileManfoDjuiko.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tripbook.userprofileManfoDjuiko.data.model.MediaItem
import com.tripbook.userprofileManfoDjuiko.data.model.MediaType
import com.tripbook.userprofileManfoDjuiko.presentation.screens.MediaDetailScreen
import com.tripbook.userprofileManfoDjuiko.presentation.screens.MediaManagementScreen
import com.tripbook.userprofileManfoDjuiko.presentation.screens.MediaUploadScreen
import java.util.Calendar
import java.util.Date
import androidx.core.net.toUri

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


// In a suitable place, like a companion object or a separate TestData file
object SampleMediaData {

    val sampleImage1 = MediaItem(
        id = "image_001",
        uri = "content://media/external/images/media/123".toUri(), // Example URI
        type = MediaType.IMAGE,
        name = "Sunset Over Mountains",
        size = 1024 * 1024 * 2L, // 2MB
        dateAdded = Calendar.getInstance().apply { set(2023, Calendar.JANUARY, 15) }.time
    )

    val sampleImage2 = MediaItem(
        id = "image_002",
        uri = "content://media/external/images/media/456".toUri(), // Example URI
        type = MediaType.IMAGE,
        name = "City Skyline at Night",
        size = 1024 * 1024 * 1.5.toLong(), // 1.5MB
        dateAdded = Calendar.getInstance().apply { set(2023, Calendar.FEBRUARY, 20) }.time
    )

    val sampleVideo1 = MediaItem(
        id = "video_001",
        uri = "content://media/external/video/media/789".toUri(), // Example URI
        type = MediaType.VIDEO,
        name = "Beach Waves",
        size = 1024 * 1024 * 50L, // 50MB
        dateAdded = Calendar.getInstance().apply { set(2023, Calendar.MARCH, 10) }.time
    )

    val allSampleMedia: List<MediaItem> = listOf(sampleImage1, sampleImage2, sampleVideo1)
}


// Assume SampleMediaData is accessible here (e.g., in the same file or imported)
private fun getMediaItemById(mediaId: String): MediaItem {
    return SampleMediaData.allSampleMedia.find { it.id == mediaId }
        ?: run {
            // Fallback if the ID is not found in the sample data
            // You might want to throw an exception or return a default item
            // For this example, returning a default placeholder:
            MediaItem(
                id = mediaId,
                uri = Uri.EMPTY,
                type = MediaType.IMAGE, // Or determine dynamically if possible
                name = "Unknown Item",
                size = 0L,
                dateAdded = Date()
            )
        }
}