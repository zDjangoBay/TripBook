package com.android.tripbook.ui.navigation

import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.android.tripbook.posts.model.PostModel
import com.android.tripbook.posts.model.Location as PostLocation // Alias pour éviter les conflits
import com.android.tripbook.posts.model.Coordinates as PostCoordinates // Alias pour éviter les conflits
import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Comment as PostComment // Alias pour éviter les conflits
import com.android.tripbook.posts.model.PostVisibility as PostModuleVisibility // Alias pour éviter les conflits
import com.android.tripbook.posts.model.UserMinimal
import com.android.tripbook.posts.screens.CreatePostScreen
import com.android.tripbook.posts.screens.EditPostScreen
import com.android.tripbook.posts.screens.PostDetailScreen
import com.android.tripbook.posts.screens.ReportedPostsScreen
import java.time.Instant
import android.net.Uri // Pour ImageModel Uri

fun NavGraphBuilder.postNavGraph(navController: NavController) {
    navigation(startDestination = "posts_home_placeholder", route = "posts_graph") {
        composable("posts_home_placeholder") {
            Text("This is a placeholder for Post Graph. Navigate via MainNavGraph.")
        }
        composable("create_post") {
            CreatePostScreen(onPostCreated = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }
        composable("edit_post/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            // Data mockée pour la démo UI
            val samplePost = PostModel(
                id = postId ?: "mock_edit_id",
                userId = "user_mock",
                username = "EditorUser",
                userAvatar = null,
                isVerified = false,
                title = "Title to Edit (Mock)",
                description = "Content to edit. This is example text to simulate content of a real post for editing.",
                images = listOf(ImageModel("img_edit", Uri.parse("https://via.placeholder.com/150/FF0000/FFFFFF?text=Edit+Image"))),
                location = PostLocation("loc_edit", "Editable Place", "Cityville", "Countryland", null),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(1000000),
                lastEditedAt = Instant.now(),
                visibility = PostModuleVisibility.PUBLIC,
                collaborators = listOf(UserMinimal("collab1", "collaborator1")),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = emptyList(),
                comments = emptyList()
            )
            EditPostScreen(
                post = samplePost,
                onPostUpdated = { updatedPost ->
                    println("Post updated (mock): ${updatedPost.title}")
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            // Données mockées pour la démo UI
            val samplePost = PostModel(
                id = postId ?: "mock_detail_id",
                userId = "user_detail_author",
                username = "DetailAuthor",
                userAvatar = null,
                isVerified = true,
                title = "Post Detail (Mock)",
                description = "Detailed content of the post. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                images = listOf(ImageModel("img_det1", Uri.parse("https://via.placeholder.com/300/0000FF/FFFFFF?text=Detail+Image+1")), ImageModel("img_det2", Uri.parse("https://via.placeholder.com/300/00FF00/FFFFFF?text=Detail+Image+2"))),
                location = PostLocation("loc_detail", "Detailed Place", "Cityville", "Countryland", PostCoordinates(40.7128, -74.0060)),
                tags = emptyList(),
                createdAt = Instant.now().minusSeconds(5000000),
                lastEditedAt = null,
                visibility = PostModuleVisibility.PUBLIC,
                collaborators = null,
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = listOf("liker1", "liker2", "liker3"),
                comments = listOf(
                    PostComment("c1", postId ?: "mock_detail_id", "commenter1", "Commenter One", null, "Great post!", Instant.now().minusSeconds(500000)),
                    PostComment("c2", postId ?: "mock_detail_id", "commenter2", "Commenter Two", null, "Very insightful!", Instant.now().minusSeconds(100000))
                )
            )
            PostDetailScreen(post = samplePost, currentUserId = "my_user_id", onBack = { navController.popBackStack() })
        }
        composable("reported_posts") {
            // Données mockées pour la démo UI
            val sampleReportedPosts = listOf(
                PostModel(
                    id = "rp_mock1",
                    userId = "user_spam",
                    username = "SpamUser",
                    userAvatar = null,
                    isVerified = false,
                    title = "Abusive Content (Mock)",
                    description = "This post violates our guidelines with inappropriate language.",
                    images = emptyList(),
                    location = PostLocation("loc_spam", "Internet", "", "Global", null),
                    tags = emptyList(),
                    createdAt = Instant.now(),
                    lastEditedAt = null,
                    visibility = PostModuleVisibility.PUBLIC,
                    collaborators = null,
                    isEphemeral = false,
                    ephemeralDurationMillis = null,
                    likes = emptyList(),
                    comments = emptyList()
                ),
                PostModel(
                    id = "rp_mock2",
                    userId = "user_hate",
                    username = "HateUser",
                    userAvatar = null,
                    isVerified = false,
                    title = "Hate Speech (Mock)",
                    description = "This post contains harmful and hateful remarks against a group.",
                    images = emptyList(),
                    location = PostLocation("loc_hate", "Internet", "", "Global", null),
                    tags = emptyList(),
                    createdAt = Instant.now(),
                    lastEditedAt = null,
                    visibility = PostModuleVisibility.PUBLIC,
                    collaborators = null,
                    isEphemeral = false,
                    ephemeralDurationMillis = null,
                    likes = emptyList(),
                    comments = emptyList()
                )
            )
            ReportedPostsScreen(
                reportedPosts = sampleReportedPosts,
                onResolveReport = { post -> println("Resolving report for post (mock): ${post.id}") },
                onDeletePost = { post -> println("Deleting post (mock): ${post.id}") },
                onBack = { navController.popBackStack() }
            )
        }
    }
}