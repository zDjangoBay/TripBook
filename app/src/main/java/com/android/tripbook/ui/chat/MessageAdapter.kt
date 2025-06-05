package com.android.tripbook.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
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
        val diffCallback = MessageDiffCallback(messages, newMessages)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        messages.clear()
        messages.addAll(newMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvMessage.text = message.text
            tvTime.text = formatTime(message.timestamp?.toDate())
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvSenderName: TextView = itemView.findViewById(R.id.tvSenderName)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)

        fun bind(message: Message) {
            tvMessage.text = message.text
            tvSenderName.text = message.senderName
            tvTime.text = formatTime(message.timestamp?.toDate())
        }
    }

    private fun formatTime(date: Date?): String {
        if (date == null) return ""
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(date)
    }

    class MessageDiffCallback(
        private val oldList: List<Message>,
        private val newList: List<Message>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
