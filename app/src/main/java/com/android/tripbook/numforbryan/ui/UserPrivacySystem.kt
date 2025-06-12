package com.android.tripbook.numforbryan.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.launch
import com.android.tripbook.numforbryan.ui.theme.DataStoreManager
import com.android.tripbook.numforbryan.ui.Visibility
import com.android.tripbook.numforbryan.ui.theme.UserPrivacyViewModel
import com.android.tripbook.numforbryan.ui.theme.UserPrivacyViewModelFactory

@Composable
fun UserPrivacySystemRoot(context: Context) {
    val navController = rememberNavController()

    // ViewModel to hold UI state and persistence logic
    val viewModel: UserPrivacyViewModel = viewModel(factory = UserPrivacyViewModelFactory(context))

    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") {
            UserProfileScreen(
                navController = navController,
                currentVisibility = viewModel.visibility.collectAsState().value,
                onVisibilityChange = { newVisibility ->
                    viewModel.updateVisibility(newVisibility)
                }
            )
        }
        composable("privacySettings") {
            PrivacySettingsScreen(
                currentVisibility = viewModel.visibility.collectAsState().value,
                onVisibilityChange = { newVisibility ->
                    viewModel.updateVisibility(newVisibility)
                },
                navController = navController
            )
        }
    }
}

@Composable
fun UserProfileScreen(
    navController: NavHostController,
    currentVisibility: Visibility,
    onVisibilityChange: (Visibility) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // showing button and current visibility
        Button(onClick = { navController.navigate("privacySettings") }) {
            Text("Privacy Settings")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Current Visibility: ${currentVisibility.name}")
    }
}

@Composable
fun PrivacySettingsScreen(
    currentVisibility: Visibility,
    onVisibilityChange: (Visibility) -> Unit,
    navController: NavHostController
) {
    val options = Visibility.values().toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Privacy Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Choose who can view your content", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = option == currentVisibility,
                    onClick = { onVisibilityChange(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(option.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Profile")
        }
    }
}
