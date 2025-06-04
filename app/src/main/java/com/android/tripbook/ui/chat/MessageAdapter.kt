package com.android.tripbook.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.android.tripbook.R
import com.android.tripbook.model.Message

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewMessage: TextView = itemView.findViewById(R.id.textViewMessage)
        private val messageContainer: LinearLayout = itemView.findViewById(R.id.messageContainer)

        fun bind(message: Message) {
            textViewMessage.text = message.message

            val isCurrentUser = message.senderId == currentUserId
            val layoutParams = messageContainer.layoutParams as ViewGroup.MarginLayoutParams

            if (isCurrentUser) {
                textViewMessage.setBackgroundResource(R.drawable.bg_message_sender)
                layoutParams.marginStart = 100
                layoutParams.marginEnd = 0
            } else {
                textViewMessage.setBackgroundResource(R.drawable.bg_message_receiver)
                layoutParams.marginStart = 0
                layoutParams.marginEnd = 100
            }

            messageContainer.layoutParams = layoutParams
        }
    }
}
