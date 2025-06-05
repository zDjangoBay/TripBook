package com.android.tripbook.ui.chat

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
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
        setContentView(R.layout.group_chat_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
        layoutManager.stackFromEnd = true

        rvMessages.layoutManager = layoutManager
        rvMessages.adapter = messageAdapter
    }

    private fun setupClickListeners() {
        btnSend.setOnClickListener {
            sendMessage()
        }

        etMessage.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                sendMessage()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun sendMessage() {
        val messageText = etMessage.text.toString().trim()
        if (TextUtils.isEmpty(messageText)) return

        val currentUser = auth.currentUser ?: run {
            Toast.makeText(this, "Please log in to send messages", Toast.LENGTH_SHORT).show()
            return
        }

        val message = Message(
            text = messageText,
            senderId = currentUser.uid,
            senderName = currentUser.displayName ?: "Anonymous",
            tripId = tripId,
            timestamp = null
        )

        btnSend.isEnabled = false
        etMessage.isEnabled = false

        firestore.collection("trips")
            .document(tripId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                etMessage.setText("")
                scrollToBottomDelayed()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to send message: ${it.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                btnSend.isEnabled = true
                etMessage.isEnabled = true
                etMessage.requestFocus()
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
                    } catch (ex: Exception) {
                        null
                    }
                } ?: emptyList()

                messageAdapter.updateMessages(messages)
                scrollToBottomDelayed()
            }
    }

    private fun scrollToBottom() {
        if (messageAdapter.itemCount > 0) {
            rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private fun scrollToBottomDelayed() {
        rvMessages.post {
            scrollToBottom()
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
