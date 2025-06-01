package com.android.tripbook.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.tripbook.screens.auth.LoginScreen
import com.android.tripbook.screens.auth.RegisterScreen
import com.android.tripbook.screens.content.CreatePostScreen
import com.android.tripbook.screens.content.EditPostScreen
import com.android.tripbook.screens.content.UserContentScreen
import com.android.tripbook.screens.discovery.DiscoveryScreen
import com.android.tripbook.screens.landing.LandingScreen
import com.android.tripbook.screens.post.PostDetailScreen
import com.android.tripbook.screens.profile.UserProfileScreen
import com.android.tripbook.screens.agency.AgencyDetailScreen
import com.android.tripbook.screens.agency.AdminAgencyScreen

// Define route constants to avoid typos
object AppRoute {
    const val LANDING = "landing"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DISCOVERY = "discovery"
    const val USER_CONTENT = "user_content"
    const val CREATE_POST = "create_post"
    const val EDIT_POST = "edit_post/{postId}"
    const val POST_DETAIL = "post_detail/{postId}"
    const val USER_PROFILE = "user_profile/{userId}"
    const val AGENCY_DETAIL = "agency_detail/{agencyId}"
    const val ADMIN_AGENCY = "admin_agency"
    
    // Helper functions to generate routes with arguments
    fun editPost(postId: String) = "edit_post/$postId"
    fun postDetail(postId: String) = "post_detail/$postId"
    fun userProfile(userId: String) = "user_profile/$userId"
    fun agencyDetail(agencyId: String) = "agency_detail/$agencyId"
}

@Composable
fun AppNavigation(navController: NavHostController, startDestination: String = AppRoute.LANDING) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Landing screen - Entry point for the app
        composable(AppRoute.LANDING) {
            LandingScreen(
                navigateToLogin = { navController.navigate(AppRoute.LOGIN) },
                navigateToRegister = { navController.navigate(AppRoute.REGISTER) },
                navigateToDiscovery = { navController.navigate(AppRoute.DISCOVERY) {
                    popUpTo(AppRoute.LANDING) { inclusive = true }
                }}
            )
        }
        
        // Authentication screens
        composable(AppRoute.LOGIN) {
            LoginScreen(
                navigateToRegister = { navController.navigate(AppRoute.REGISTER) {
                    popUpTo(AppRoute.LOGIN) { inclusive = true }
                }},
                navigateToDiscovery = { navController.navigate(AppRoute.DISCOVERY) {
                    popUpTo(AppRoute.LOGIN) { inclusive = true }
                }},
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AppRoute.REGISTER) {
            RegisterScreen(
                navigateToLogin = { navController.navigate(AppRoute.LOGIN) {
                    popUpTo(AppRoute.REGISTER) { inclusive = true }
                }},
                navigateToDiscovery = { navController.navigate(AppRoute.DISCOVERY) {
                    popUpTo(AppRoute.REGISTER) { inclusive = true }
                }},
                navigateBack = { navController.popBackStack() }
            )
        }
        
        // Discovery screen - Main screen after login
        composable(AppRoute.DISCOVERY) {
            DiscoveryScreen(
                navigateToPostDetail = { postId -> navController.navigate(AppRoute.postDetail(postId)) },
                navigateToUserProfile = { userId -> navController.navigate(AppRoute.userProfile(userId)) },
                navigateToUserContent = { navController.navigate(AppRoute.USER_CONTENT) },
                navigateToAgency = { agencyId -> navController.navigate(AppRoute.agencyDetail(agencyId)) },
                navigateToAdminAgency = { navController.navigate(AppRoute.ADMIN_AGENCY) }
            )
        }
        
        // Content Management screens
        composable(AppRoute.USER_CONTENT) {
            UserContentScreen(
                navigateToCreatePost = { navController.navigate(AppRoute.CREATE_POST) },
                navigateToEditPost = { postId -> navController.navigate(AppRoute.editPost(postId)) },
                navigateToPostDetail = { postId -> navController.navigate(AppRoute.postDetail(postId)) },
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AppRoute.CREATE_POST) {
            CreatePostScreen(
                onPostCreated = { postId -> 
                    navController.navigate(AppRoute.postDetail(postId)) {
                        popUpTo(AppRoute.USER_CONTENT)
                    }
                },
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = AppRoute.EDIT_POST,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            EditPostScreen(
                postId = postId,
                onPostUpdated = { 
                    navController.navigate(AppRoute.postDetail(postId)) {
                        popUpTo(AppRoute.USER_CONTENT)
                    }
                },
                navigateBack = { navController.popBackStack() }
            )
        }
        
        // Post Detail screen
        composable(
            route = AppRoute.POST_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostDetailScreen(
                postId = postId,
                navigateToUserProfile = { userId -> navController.navigate(AppRoute.userProfile(userId)) },
                navigateBack = { navController.popBackStack() }
            )
        }
        
        // User Profile screen
        composable(
            route = AppRoute.USER_PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfileScreen(
                userId = userId,
                navigateToPostDetail = { postId -> navController.navigate(AppRoute.postDetail(postId)) },
                navigateBack = { navController.popBackStack() }
            )
        }
        
        // Agency screens
        composable(
            route = AppRoute.AGENCY_DETAIL,
            arguments = listOf(navArgument("agencyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val agencyId = backStackEntry.arguments?.getString("agencyId") ?: ""
            AgencyDetailScreen(
                agencyId = agencyId,
                navigateBack = { navController.popBackStack() }
            )
        }
        
        composable(AppRoute.ADMIN_AGENCY) {
            AdminAgencyScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
