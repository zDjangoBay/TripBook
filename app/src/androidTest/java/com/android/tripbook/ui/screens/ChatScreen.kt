package com.tripbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.tripbook.data.model.Message

@Composable
fun ChatScreen(currentUserId: String, chatPartnerId: String) {
    var message by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val messages = remember { mutableStateListOf<Message>() }

    LaunchedEffect(Unit) {
        db.collection("messages")
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", chatPartnerId)
            .addSnapshotListener { value, _ ->
                messages.clear()
                value?.forEach { doc ->
                    messages.add(doc.toObject(Message::class.java))
                }
            }
    }

    Column {
        messages.forEach {
            Text("${it.senderId}: ${it.content}")
        }

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Type message") }
        )
        Button(onClick = {
            val msg = Message(currentUserId, chatPartnerId, message)
            db.collection("messages").add(msg)
            message = ""
        }) {
            Text("Send")
        }
    }
}
