class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<NotificationUIState>(NotificationUIState.Loading)
    val uiState: StateFlow<NotificationUIState> = _uiState.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow("all")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()
    
    val unreadCount: StateFlow<Int> = repository.getUnreadCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    init {
        loadNotifications()
    }
    
    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        loadNotifications()
    }
    
    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                val notificationsFlow = when (_selectedFilter.value) {
                    "unread" -> repository.getUnreadNotifications()
                    "read" -> repository.getReadNotifications()
                    else -> repository.getAllNotifications()
                }
                
                notificationsFlow.collect { notifications ->
                    _uiState.value = if (notifications.isEmpty()) {
                        NotificationUIState.Empty
                    } else {
                        NotificationUIState.Success(notifications)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = NotificationUIState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markAsRead(notificationId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                repository.markAllAsRead()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun deleteNotification(notification: Notification) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(notification)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun createDemoNotification() {
        val demoNotifications = listOf(
            Notification(
                title = "Welcome!",
                message = "Thanks for joining us. Explore all features.",
                type = NotificationType.SUCCESS,
                metadata = NotificationMetadata(sender = "System", category = "Welcome")
            ),
            Notification(
                title = "New Message",
                message = "You have a new message from John Doe.",
                type = NotificationType.INFO,
                metadata = NotificationMetadata(sender = "John Doe", category = "Messages")
            ),
            Notification(
                title = "Security Alert",
                message = "Login detected from new device.",
                type = NotificationType.WARNING,
                metadata = NotificationMetadata(sender = "Security", category = "Security")
            )
        )
        
        viewModelScope.launch {
            repository.insertNotification(demoNotifications.random())
        }
    }
}
