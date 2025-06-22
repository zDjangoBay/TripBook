data class Notification(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.INFO,
    val metadata: NotificationMetadata? = null
)

enum class NotificationType {
    INFO, WARNING, SUCCESS, ERROR
}

data class NotificationMetadata(
    val sender: String? = null,
    val category: String? = null,
    val actionUrl: String? = null
)
