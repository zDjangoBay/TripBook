//chatviewmodel.kt
package com.android.tripbook.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.android.tripbook.model.ChatMessage

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun sendMessage(senderId: String, message: String) {
        val chat = ChatMessage(senderId = senderId, message = message)
        db.collection("chats").add(chat)
    }

    fun loadMessages() {
        db.collection("chats")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val result = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                _messages.value = result
            }
    }
}
