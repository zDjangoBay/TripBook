class MessageAdapter(
    private val messages: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val textView = holder.view.findViewById<TextView>(R.id.textViewMessage)
        textView.text = message.message

        if (message.senderId == currentUserId) {
            // Style for sender
            textView.setBackgroundResource(R.drawable.bg_message_sender)
        } else {
            textView.setBackgroundResource(R.drawable.bg_message_receiver)
        }
    }

    override fun getItemCount(): Int = messages.size
}
