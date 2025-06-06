package com.android.tripbook.ui.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.w3c.dom.Text

@Composable
fun NavigationGraph(navController: NavHostController) {
    // État partagé pour les amis
    val allUsers = listOf("Alice", "Bob", "Charlie", "Diane")
    val addedFriends = mutableListOf<String>() // à améliorer plus tard (viewModel)

    NavHost(navController = navController, startDestination = "profile") {
        composable("friend_requests") {
            FriendRequestsScreen()
        }
        composable("profile_personal") {
            PersonalProfileScreen()
        }
        composable("profile") {
            UserProfileScreen(
                Button(onClick = {
                    navController.navigate("friend_requests")
                }) {
                    Text("Voir les demandes d'amis")
                },
                onFriendAdded = TODO()
            )
        }

        composable("friends") {
            FriendListScreen(friends = addedFriends)
        }
    }
}

private fun AnimatedContentScope.Button(onClick: () -> Unit, function: @Composable () -> Unit) {}

@Composable
fun Button(onClick: () -> Unit, content: @Composable () -> Text) {
    TODO("Not yet implemented")
}
