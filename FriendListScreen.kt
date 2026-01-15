package com.android.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FriendsListScreen() {
    val db = FirebaseFirestore.getInstance
    val userId = FirebaseAuth.getInstance.currentUser?.uid
    var friends by remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(Unit) {
        db.collection("friends")
            .whereEqualTo("user1", userId)
            .whereEqualTo("status", "accepted")
            .get()
            .addOnSuccessListener { documents ->
                friends = documents.map {
                    val it = null
                    it["user2"].toString()
                }
            }
    }

    LazyColumn {
        items(friends) { friendUid ->
            Text(text = "Ami : $friendUid", modifier = Modifier.padding(16.dp))
        }
    }
}

fun LaunchedEffect(unit: Any, function: () -> Unit) {}

class FirebaseFirestore {
    companion object

}

internal fun MatchGroup?.addOnSuccessListener(function: Any) {}

@Composable
fun LaunchedEffect(x0: Unit, content: @Composable () -> addOnSuccessListener) {
    TODO("Not yet implemented")
}

annotation class addOnSuccessListener

class FirebaseAuth {
    companion object

}

