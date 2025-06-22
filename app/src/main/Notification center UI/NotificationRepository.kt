class NotificationRepository(private val notificationDao: NotificationDao) {
    
    fun getAllNotifications(): Flow<List<Notification>> = notificationDao.getAllNotifications()
    
    fun getUnreadNotifications(): Flow<List<Notification>> = 
        notificationDao.getNotificationsByReadStatus(false)
    
    fun getReadNotifications(): Flow<List<Notification>> = 
        notificationDao.getNotificationsByReadStatus(true)
    
    fun getUnreadCount(): Flow<Int> = notificationDao.getUnreadCount()
    
    suspend fun getNotificationById(id: String): Notification? = 
        notificationDao.getNotificationById(id)
    
    suspend fun insertNotification(notification: Notification) = 
        notificationDao.insertNotification(notification)
    
    suspend fun markAsRead(id: String) = notificationDao.markAsRead(id)
    
    suspend fun markAllAsRead() = notificationDao.markAllAsRead()
    
    suspend fun deleteNotification(notification: Notification) = 
        notificationDao.deleteNotification(notification)
}
