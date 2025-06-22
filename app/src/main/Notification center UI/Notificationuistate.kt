sealed class NotificationUIState {
    object Loading : NotificationUIState()
    data class Success(
        val notifications: List<Notification>,
        val hasMore: Boolean = false
    ) : NotificationUIState()
    object Empty : NotificationUIState()
    data class Error(val message: String) : NotificationUIState()
}
