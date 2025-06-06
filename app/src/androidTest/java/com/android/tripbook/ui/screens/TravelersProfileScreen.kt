package com.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tripbook.data.model.TravelerProfile
import com.tripbook.repository.ProfileRepository
import kotlinx.coroutines.launch

@Composable
fun TravelerProfileScreen() {
    val repo = remember { ProfileRepository() }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }

    // Load profile when screen starts
    LaunchedEffect(Unit) {
        repo.getProfile()?.let {
            username = it.username
            bio = it.bio
            interests = it.interests.joinToString(", ")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Profile", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = interests,
            onValueChange = { interests = it },
            label = { Text("Interests (comma-separated)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                val profile = TravelerProfile(
                    uid = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    username = username,
                    bio = bio,
                    interests = interests.split(",").map { it.trim() },
                    profileImageUrl = "" // Placeholder for now
                )
                repo.updateProfile(profile)
            }
        }) {
            Text("Save Profile")
        }
    }
}
