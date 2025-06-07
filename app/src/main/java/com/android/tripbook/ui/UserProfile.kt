package com.android.tripbook.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileScreen(modifier: Modifier = Modifier) {
    // Simple user profile screen UI
    Column(modifier = modifier) {
        Text(text = "User Profile", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Name: John Doe")
        Text(text = "Email: johndoe@example.com")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle edit profile click */ }) {
            Text(text = "Edit Profile")
        }
    }
}

@Preview
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen()
}

