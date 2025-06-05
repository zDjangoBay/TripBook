package com.android.tripbook.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.model.Message

class MessageAdapter(
    private val messages: MutableList<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layoutSender: LinearLayout = itemView.findViewById(R.id.layoutSender)
        val layoutReceiver: LinearLayout = itemView.findViewById(R.id.layoutReceiver)

        // Sender views
        val tvSenderMessage: TextView = itemView.findViewById(R.id.tvSenderMessage)
        val tvSenderTime: TextView = itemView.findViewById(R.id.tvSenderTime)

        // Receiver views
        val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        val tvReceiverMessage: TextView = itemView.findViewById(R.id.tvReceiverMessage)
        val tvReceiverTime: TextView = itemView.findViewById(R.id.tvReceiverTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val isCurrentUser = message.senderId == currentUserId

        if (isCurrentUser) {
            // Show as sender (right side)
            holder.layoutSender.visibility = View.VISIBLE
            holder.layoutReceiver.visibility = View.GONE

            holder.tvSenderMessage.text = message.text
            holder.tvSenderTime.text = message.getFormattedTime()
        } else {
            // Show as receiver (left side)
            holder.layoutSender.visibility = View.GONE
            holder.layoutReceiver.visibility = View.VISIBLE

            holder.tvSenderName.text = message.senderName
            holder.tvReceiverMessage.text = message.text
            holder.tvReceiverTime.text = message.getFormattedTime()
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun clearMessages() {
        messages.clear()
        notifyDataSetChanged()
    }

    fun removeMessage(position: Int) {
        if (position >= 0 && position < messages.size) {
            messages.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}