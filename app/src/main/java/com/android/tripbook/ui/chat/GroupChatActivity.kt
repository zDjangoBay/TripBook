// First, create the Message data class
// File: app/src/main/java/com/android/tripbook/model/Message.kt
package com.android.tripbook.model

import com.google.firebase.Timestamp
import java.util.Date

data class Message(
    var id: String = "",
    val text: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val tripId: String = "",
    val timestamp: Timestamp = Timestamp(Date())
)

// Second, create the MessageAdapter
// File: app/src/main/java/com/android/tripbook/ui/chat/MessageAdapter.kt
package com.android.tripbook.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private var messages: MutableList<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            TYPE_SENT
        } else {
            TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_SENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            TYPE_RECEIVED -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvMessage.text = message.text
            tvTime.text = formatTime(message.timestamp.toDate())
        }
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvMessage.text = message.text
            tvSenderName.text = message.senderName
            tvTime.text = formatTime(message.timestamp.toDate())
        }
    }

    private fun formatTime(date: Date): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }
}

// Third, the complete GroupChatActivity
// File: app/src/main/java/com/android/tripbook/ui/chat/GroupChatActivity.kt
package com.android.tripbook.ui.chat

import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.android.tripbook.R
import com.android.tripbook.model.Message

class GroupChatActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var tvChatTitle: TextView

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var tripId: String = ""
    private var tripName: String = ""
    private var messagesListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get trip details from intent
        tripId = intent.getStringExtra("TRIP_ID") ?: ""
        tripName = intent.getStringExtra("TRIP_NAME") ?: "Group Chat"

        if (tripId.isEmpty()) {
            Toast.makeText(this, "Invalid trip ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        initializeFirebase()
        setupRecyclerView()
        setupClickListeners()
        loadMessages()
    }

    private fun initializeViews() {
        rvMessages = findViewById(R.id.rvMessages)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)
        tvChatTitle = findViewById(R.id.tvChatTitle)

        tvChatTitle.text = tripName
    }

    private fun initializeFirebase() {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    private fun setupRecyclerView() {
        val currentUserId = auth.currentUser?.uid ?: ""
        messageAdapter = MessageAdapter(mutableListOf(), currentUserId)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // Start from bottom

        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = messageAdapter
    }

    private fun setupClickListeners() {
        btnSend.setOnClickListener {
            sendMessage()
        }

        etMessage.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun sendMessage() {
        val messageText = etMessage.text.toString().trim()
        if (messageText.isEmpty()) {
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to send messages", Toast.LENGTH_SHORT).show()
            return
        }

        val message = Message(
            text = messageText,
            senderId = currentUser.uid,
            senderName = currentUser.displayName ?: "Anonymous",
            tripId = tripId
        )

        // Add message to Firestore
        firestore.collection("trips")
            .document(tripId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                etMessage.setText("")
                scrollToBottom()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadMessages() {
        messagesListener = firestore.collection("trips")
            .document(tripId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading messages: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val messages = snapshots?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Message::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                messageAdapter.updateMessages(messages)
                scrollToBottom()
            }
    }

    private fun scrollToBottom() {
        if (messageAdapter.itemCount > 0) {
            rvMessages.smoothScrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        messagesListener?.remove()
    }
}