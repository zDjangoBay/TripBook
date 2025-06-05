package com.android.tripbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.android.tripbook.posts.model.PostModel // Utilise le nouveau PostModel
// Supprime cet alias PostLocation car nous utiliserons directement TravelLocation de data.model
// import com.android.tripbook.posts.model.Location as PostLocation

import com.android.tripbook.posts.model.ImageModel
import com.android.tripbook.posts.model.Comment as PostComment // Alias pour éviter les conflits
import com.android.tripbook.posts.model.PostVisibility as PostModuleVisibility // Alias pour éviter les conflits

import com.android.tripbook.posts.screens.CreatePostScreen
import com.android.tripbook.posts.screens.PostDetailScreen
import com.android.tripbook.posts.screens.ReportedPostsScreen // Import pour ReportedPostsScreen

import com.android.tripbook.ui.screens.feed.HomeFeedScreen
import com.android.tripbook.ui.screens.feed.SavedPostsScreen
import com.android.tripbook.ui.screens.feed.PinnedPostsScreen
import com.android.tripbook.ui.screens.feed.GroupFeedScreen
import com.android.tripbook.ui.screens.travel.LocationFeedScreen
import com.android.tripbook.ui.screens.travel.TripBookingScreen

// C'est l'import correct et nécessaire pour les TravelLocation dans les PostModels
import com.android.tripbook.data.model.TravelLocation
import com.android.tripbook.posts.model.UserMinimal // Import nécessaire pour UserMinimal

import java.time.Instant
import android.net.Uri // Pour ImageModel Uri


@Composable
fun MainNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home_feed") {
        composable("home_feed") {
            // Exemple de données mockées pour la démo
            val samplePosts = listOf(
                PostModel(
                    id = "p1",
                    userId = "u1",
                    username = "AlexTravels",
                    userAvatar = null,
                    isVerified = true,
                    title = "Exploring Yaounde!",
                    description = "Visited Reunification monument, Bastos, and enjoyed amazing croissants. #food #travel",
                    images = listOf(ImageModel("p1_img1", Uri.parse("https://s.rfi.fr/media/display/f73472c6-2227-11f0-9b31-005056a97e36/w:1280/p:4x3/000_1MH9LV.jpg"))),
                    location = TravelLocation(id = "loc_paris", name = "Yaounde", latitude = 48.8584, longitude = 2.2945, description = "Yaounde, Cameroun", imageUrl = null),
                    tags = emptyList(),
                    createdAt = Instant.now().minusSeconds(86400),
                    lastEditedAt = null,
                    visibility = PostModuleVisibility.PUBLIC,
                    collaborators = null,
                    isEphemeral = false,
                    ephemeralDurationMillis = null,
                    likes = listOf("user_a"),
                    comments = listOf(PostComment("c1", "p1", "u2", "Bob", null, "Awesome!", Instant.now()))
                ),
                PostModel(
                    id = "p2",
                    userId = "u2",
                    username = "MariaAdventures",
                    userAvatar = null,
                    isVerified = false,
                    title = "Hiking in Patagonia",
                    description = "Breath-taking views and challenging trails. A true adventure!",
                    images = listOf(ImageModel("p2_img1", Uri.parse("https://via.placeholder.com/300/00FF00/FFFFFF?text=Patagonia"))),
                    location = TravelLocation(id = "loc_patagonia", name = "Fitz Roy", latitude = -49.2743, longitude = -73.0426, description = "El Chalten, Argentina", imageUrl = null),
                    tags = emptyList(),
                    createdAt = Instant.now().minusSeconds(86400 * 2),
                    lastEditedAt = null,
                    visibility = PostModuleVisibility.PUBLIC,
                    collaborators = null,
                    isEphemeral = false,
                    ephemeralDurationMillis = null,
                    likes = listOf("user_b", "user_c"),
                    comments = emptyList()
                )
            )
            HomeFeedScreen(
                posts = samplePosts,
                onPostClick = { post -> navController.navigate("post_detail/${post.id}") },
                onNavigateToCreatePost = { navController.navigate("create_post") },
                // Passant les callbacks de navigation à HomeFeedScreen pour les tests
                onNavigateToSavedPosts = { navController.navigate("saved_posts") },
                onNavigateToPinnedPosts = { navController.navigate("pinned_posts") },
                onNavigateToGroupFeed = { groupName -> navController.navigate("group_feed/$groupName") },
                onNavigateToReportedPosts = { navController.navigate("reported_posts") },
                onNavigateToLocationFeed = { navController.navigate("location_feed") },
                onNavigateToTripBooking = { locationId -> navController.navigate("book_trip/$locationId") }
            )
        }
        composable("create_post") {
            CreatePostScreen(onPostCreated = { navController.popBackStack() }, onBack = { navController.popBackStack() })
        }
        composable("post_detail/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            // Données mockées pour la démo UI
            val post = PostModel(
                id = postId,
                userId = "user123",
                username = "Author",
                userAvatar = null,
                isVerified = true,
                title = "Detail of Post ${postId}",
                description = "This is the detailed content of post ${postId}. It is a placeholder.",
                images = listOf(ImageModel("img_det", Uri.parse("https://via.placeholder.com/600/0000FF/FFFFFF?text=Detail+Image"))),
                // CORRECTION ICI: Utilise TravelLocation du module data.model
                location = TravelLocation(id = "loc_det", name = "Detail Place", latitude = 0.0, longitude = 0.0, description = "City, Country", imageUrl = null),
                tags = emptyList(),
                createdAt = Instant.now(),
                lastEditedAt = null,
                visibility = PostModuleVisibility.PUBLIC,
                collaborators = emptyList(),
                isEphemeral = false,
                ephemeralDurationMillis = null,
                likes = listOf("liker1"),
                comments = emptyList<PostComment>()
            )
            PostDetailScreen(post = post, currentUserId = "my_user_id", onBack = { navController.popBackStack() })
        }
        composable("saved_posts") {
            val sampleSavedPosts = listOf(
                PostModel(
                    id = "sp1",
                    userId = "u1",
                    username = "Saver",
                    userAvatar = null,
                    isVerified = false,
                    title = "Saved Post 1",
                    description = "This post was saved.",
                    images = emptyList(),
                    // CORRECTION ICI: Utilise TravelLocation du module data.model
                    location = TravelLocation(id = "loc_saved", name = "Saved Spot", latitude = 0.0, longitude = 0.0, description = null, imageUrl = null),
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
            SavedPostsScreen(savedPosts = sampleSavedPosts, onPostClick = { post -> navController.navigate("post_detail/${post.id}") }, onBack = { navController.popBackStack() })
        }
        composable("pinned_posts") {
            val samplePinnedPosts = listOf(
                PostModel(
                    id = "pp1",
                    userId = "u1",
                    username = "Pinner",
                    userAvatar = null,
                    isVerified = false,
                    title = "Pinned Post 1",
                    description = "This post is pinned.",
                    images = emptyList(),
                    // CORRECTION ICI: Utilise TravelLocation du module data.model
                    location = TravelLocation(id = "loc_pinned", name = "Pinned Place", latitude = 0.0, longitude = 0.0, description = null, imageUrl = null),
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
            PinnedPostsScreen(pinnedPosts = samplePinnedPosts, onPostClick = { post -> navController.navigate("post_detail/${post.id}") }, onBack = { navController.popBackStack() })
        }
        composable("group_feed/{groupName}") { backStackEntry ->
            val groupName = backStackEntry.arguments?.getString("groupName") ?: "Unknown Group"
            val sampleGroupPosts = listOf(
                PostModel(
                    id = "gp1",
                    userId = "u1",
                    username = "GroupMember",
                    userAvatar = null,
                    isVerified = false,
                    title = "Group Post: ${groupName}",
                    description = "Content for group ${groupName}.",
                    images = emptyList(),
                    // CORRECTION ICI: Utilise TravelLocation du module data.model
                    location = TravelLocation(id = "loc_group", name = "Group Meetup", latitude = 0.0, longitude = 0.0, description = null, imageUrl = null),
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
            GroupFeedScreen(groupName = groupName, groupPosts = sampleGroupPosts, onPostClick = { post -> navController.navigate("post_detail/${post.id}") }, onBack = { navController.popBackStack() })
        }
        composable("location_feed") {
            // Note: LocationFeedScreen attend TravelLocation (de data.model), ce qui est déjà correct ici
            val sampleLocations = listOf(
                com.android.tripbook.data.model.TravelLocation("loc1", "Tokyo", 35.6895, 139.6917, "Capital of Japan", null),
                com.android.tripbook.data.model.TravelLocation("loc2", "New York", 40.7128, -74.0060, "The Big Apple", null)
            )
            LocationFeedScreen(locations = sampleLocations, onLocationClick = { location -> navController.navigate("book_trip/${location.id}") }, onBack = { navController.popBackStack() })
        }
        composable("book_trip/{locationId}") { backStackEntry ->
            val locationId = backStackEntry.arguments?.getString("locationId") ?: return@composable
            // Data mockée pour la démo UI
            // Note: TripBookingScreen attend TravelLocation (de data.model), ce qui est déjà correct ici
            val location = com.android.tripbook.data.model.TravelLocation(id = locationId, name = "Booking Place", latitude = 0.0, longitude = 0.0, description = null, imageUrl = null)
            TripBookingScreen(
                location = location,
                onBookingConfirmed = { loc, start, end ->
                    println("Booking confirmed for ${loc.name} from $start to $end (mock)")
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("reported_posts") {
            // Data mockée pour la démo UI
            val sampleReportedPosts = listOf(
                PostModel(
                    id = "rp_mock1",
                    userId = "user_spam",
                    username = "SpamUser",
                    userAvatar = null,
                    isVerified = false,
                    title = "Abusive Content (Mock)",
                    description = "This post violates our guidelines.",
                    images = emptyList(),
                    // CORRECTION ICI: Utilise TravelLocation du module data.model
                    location = TravelLocation("loc_spam", "Internet", 0.0, 0.0, null, null),
                    tags = emptyList(),
                    createdAt = Instant.now(),
                    lastEditedAt = null,
                    visibility = PostModuleVisibility.PUBLIC,
                    collaborators = null,
                    isEphemeral = false,
                    ephemeralDurationMillis = null,
                    likes = emptyList(),
                    comments = emptyList<PostComment>()
                )
            )
            ReportedPostsScreen(
                reportedPosts = sampleReportedPosts,
                onResolveReport = { post -> println("Resolving report for post (mock): ${post.id}") },
                onDeletePost = { post -> println("Deleting post (mock): ${post.id}") },
                onBack = { navController.popBackStack() }
            )
        }
        postNavGraph(navController)
    }
}