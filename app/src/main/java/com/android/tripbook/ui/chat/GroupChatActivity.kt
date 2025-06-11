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
import com.android.tripbook.R
import com.android.tripbook.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

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
    private var userId: String? = null // Optional

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_chat_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tripId = intent.getStringExtra("TRIP_ID") ?: ""
        tripName = intent.getStringExtra("TRIP_NAME") ?: "Group Chat"
        userId = intent.getStringExtra("USER_ID")

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
        val currentUserId = auth.currentUser?.uid.orEmpty()
        messageAdapter = MessageAdapter(mutableListOf(), currentUserId)
        rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvMessages.adapter = messageAdapter
    }

    private fun setupClickListeners() {
        btnSend.setOnClickListener { sendMessage() }

        etMessage.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                sendMessage()
                true
            } else false
        }
    }

    private fun sendMessage() {
        val messageText = etMessage.text.toString().trim()
        if (TextUtils.isEmpty(messageText)) {
            etMessage.error = "Please enter a message"
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to send messages", Toast.LENGTH_SHORT).show()
            return
        }

        val senderName = currentUser.displayName?.takeIf { it.isNotEmpty() } ?: "Anonymous"
        
        val message = Message(
            text = messageText,
            senderId = currentUser.uid,
            senderName = senderName,
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
                etMessage.error = null
                scrollToBottomDelayed()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                btnSend.isEnabled = true
                etMessage.isEnabled = true
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
                    doc.toObject(Message::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                messageAdapter.updateMessages(messages)
                scrollToBottomDelayed()
            }
    }

    private fun scrollToBottomDelayed() {
        rvMessages.post {
            if (messageAdapter.itemCount > 0) {
                rvMessages.scrollToPosition(messageAdapter.itemCount - 1)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish(); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        messagesListener?.remove()
    }
}
