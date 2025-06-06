package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FriendRequestsScreen() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid

    var requests by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    // Charger les demandes d'amis où `to` == current user
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            db.collection("friend_requests")
                .whereEqualTo("to", currentUserId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener { documents ->
                    requests = documents.map { it.data + ("id" to it.id) }
                }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Demandes d'amis", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        requests.forEach { request ->
            val fromUserId = request["from"] as? String ?: "Inconnu"
            val requestId = request["id"] as? String ?: ""

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Demande de : $fromUserId")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            updateRequestStatus(db, requestId, "accepted")
                        }) {
                            Text("Accepter")
                        }

                        Button(onClick = {
                            updateRequestStatus(db, requestId, "rejected")
                        }) {
                            Text("Refuser")
                        }
                    }
                }
            }
        }
    }
}

fun updateRequestStatus(db: FirebaseFirestore, requestId: String, status: String) {
    db.collection("friend_requests").document(requestId)
        .update("status", status)
}
