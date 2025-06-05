package com.android.tripbook.posts.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.posts.model.UserMinimal
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.Alignment
import com.android.tripbook.ui.theme.TripBookTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostCollaboratorsInput(
    selectedCollaborators: List<UserMinimal>,
    onCollaboratorAdded: (UserMinimal) -> Unit,
    onCollaboratorRemoved: (UserMinimal) -> Unit,
    onSearchUsers: (String) -> List<UserMinimal>
) {
    var searchText by remember { mutableStateOf("") }
    val searchResults = remember(searchText) {
        if (searchText.isNotBlank()) {
            onSearchUsers(searchText).filter { it !in selectedCollaborators }
        } else {
            emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Ajouter des collaborateurs (nom d'utilisateur)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (searchText.isNotBlank() && searchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    searchResults.forEach { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(user.username)
                            IconButton(onClick = {
                                onCollaboratorAdded(user)
                                searchText = ""
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Ajouter")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedCollaborators.isNotEmpty()) {
            Text(text = "Collaborateurs sélectionnés:", style = MaterialTheme.typography.labelMedium)
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                selectedCollaborators.forEach { user ->
                    AssistChip(
                        onClick = { onCollaboratorRemoved(user) },
                        label = { Text(user.username) },
                        trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Supprimer") }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostCollaboratorsInput() {
    TripBookTheme {
        val sampleUsers = listOf(
            UserMinimal("u1", "alice"),
            UserMinimal("u2", "bob"),
            UserMinimal("u3", "charlie")
        )
        var selectedCollaborators by remember { mutableStateOf(listOf(sampleUsers[0])) }

        PostCollaboratorsInput(
            selectedCollaborators = selectedCollaborators,
            onCollaboratorAdded = { user -> selectedCollaborators = selectedCollaborators + user },
            onCollaboratorRemoved = { user -> selectedCollaborators = selectedCollaborators - user },
            onSearchUsers = { query ->
                sampleUsers.filter { it.username.contains(query, ignoreCase = true) }
            }
        )
    }
}