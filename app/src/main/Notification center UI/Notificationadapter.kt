class NotificationAdapter(
    private val onItemClick: (Notification) -> Unit,
    private val onDeleteClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NotificationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(notification: Notification) {
            binding.apply {
                textTitle.text = notification.title
                textMessage.text = notification.message
                textTimestamp.text = notification.timestamp.formatTimestamp()
                
                // Read/unread indicator
                indicatorUnread.isVisible = !notification.isRead
                root.alpha = if (notification.isRead) 0.7f else 1.0f
                
                // Type indicator color
                val colorRes = when (notification.type) {
                    NotificationType.SUCCESS -> R.color.green_500
                    NotificationType.WARNING -> R.color.orange_500
                    NotificationType.ERROR -> R.color.red_500
                    NotificationType.INFO -> R.color.blue_500
                }
                indicatorType.setBackgroundColor(
                    ContextCompat.getColor(root.context, colorRes)
                )
                
                // Metadata
                notification.metadata?.let { metadata ->
                    textSender.text = metadata.sender
                    textSender.isVisible = !metadata.sender.isNullOrEmpty()
                } ?: run {
                    textSender.isVisible = false
                }
                
                // Click listeners
                root.setOnClickListener { onItemClick(notification) }
                buttonDelete.setOnClickListener { onDeleteClick(notification) }
            }
        }
    }
}

class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
    override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem.id == newItem.id
    }
    
    override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
        return oldItem == newItem
    }
}
