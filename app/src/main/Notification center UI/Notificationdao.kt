// NotificationDao.kt
@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE isRead = :isRead ORDER BY timestamp DESC")
    fun getNotificationsByReadStatus(isRead: Boolean): Flow<List<Notification>>
    
    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: String): Notification?
    
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)
    
    @Update
    suspend fun updateNotification(notification: Notification)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
    
    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
    
    @Delete
    suspend fun deleteNotification(notification: Notification)
}

// NotificationDatabase.kt
@Database(
    entities = [Notification::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    
    companion object {
        @Volatile
        private var INSTANCE: NotificationDatabase? = null
        
        fun getDatabase(context: Context): NotificationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotificationDatabase::class.java,
                    "notification_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Converters.kt
class Converters {
    @TypeConverter
    fun fromNotificationMetadata(metadata: NotificationMetadata?): String? {
        return metadata?.let { Gson().toJson(it) }
    }
    
    @TypeConverter
    fun toNotificationMetadata(metadataString: String?): NotificationMetadata? {
        return metadataString?.let { Gson().fromJson(it, NotificationMetadata::class.java) }
    }
}
