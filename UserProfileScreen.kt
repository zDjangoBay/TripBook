package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserProfileScreen(
    onViewFriendsClick: Unit,
    onFriendAdded: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allUsers = listOf("Alice", "Bob", "Charlie", "Diane", "Emile")
    val requestsSent = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = onViewFriendsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voir mes amis")
        }

        Button(
            onClick = {
                val navController = null
                navController.navigate("profile_personal")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Mon Profil Personnel")
        }


        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Rechercher un ami...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val filteredUsers = allUsers.filter {
            it.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn {
            items(filteredUsers) { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = user)
                    Button(
                        onClick = {
                            if (!requestsSent.contains(user)) {
                                requestsSent.add(user)
                                onFriendAdded(user) // ajout à la liste d'amis
                            }
                        },
                        enabled = !requestsSent.contains(user)
                    ) {
                        Text(if (requestsSent.contains(user)) "Demande envoyée" else "Ajouter")
                    }
                }
            }
        }
    }
}

private fun Nothing?.navigate(string: String) {}
