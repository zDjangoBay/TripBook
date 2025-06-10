package com.android.tripbook.companycatalog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    // Simulate editable profile data with states
    var name by remember { mutableStateOf(TextFieldValue("Alain Brice")) }
    var email by remember { mutableStateOf(TextFieldValue("alainbrice@gmail.com")) }
    var bio by remember { mutableStateOf(TextFieldValue("Lover of adventure and travel. Exploring new places every chance I get.")) }

    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Your TripBook Profile",
                style = MaterialTheme.typography.headlineMedium
            )

            // Editable Name Field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Editable Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Editable Bio Field (multi-line)
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            // Profile stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(title = "Trips Taken", count = 18)
                ProfileStat(title = "Favorites", count = 5)
                ProfileStat(title = "Reviews", count = 12)
            }

            // Update Profile Button
            Button(
                onClick = {
                    // TODO: Add update profile logic here
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Update Profile")
            }

            // Logout Button
            TextButton(
                onClick = {
                    // TODO: Add logout logic here
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log Out", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun ProfileStat(title: String, count: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}