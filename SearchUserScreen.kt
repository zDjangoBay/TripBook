package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun SearchUserScreen() {
    val db = FirebaseFirestore.getInstance
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var searchQuery by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Rechercher un nom...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            db.collection("users")
                .whereGreaterThanOrEqualTo("name", searchQuery)
                .whereLessThanOrEqualTo("name", searchQuery + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    results = documents.map { doc ->
                        val data = doc.data.toMutableMap()
                        data["id"] = doc.id
                        data
                    }
                }
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Rechercher")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(results) { user ->
                val name = user["name"] as? String ?: "Inconnu"
                val id = user["id"] as? String ?: ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = name)

                    Button(onClick = {
                        if (currentUserId != null && currentUserId != id) {
                            val friendRequest = hashMapOf(
                                "from" to currentUserId,
                                "to" to id,
                                "status" to "pending"
                            )
                            db.collection("friend_requests").add(friendRequest)
                        }
                    }) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}
