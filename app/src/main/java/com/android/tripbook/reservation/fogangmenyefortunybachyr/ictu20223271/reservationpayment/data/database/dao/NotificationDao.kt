package com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.dao

import androidx.room.*
import com.android.tripbook.reservation.fogangmenyefortunybachyr.ictu20223271.reservationpayment.data.database.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for Notification operations
 * 
 * This DAO handles all database operations for notifications including
 * CRUD operations, read/unread status management, and filtering.
 * It supports the NotificationScreen and notification system.
 * 
 * Key Features:
 * - User-specific notification queries
 * - Read/unread status management
 * - Filter by type and priority
 * - Expiration handling
 * - Reactive data with Flow
 * 
 * Used by:
 * - NotificationManager for business logic
 * - NotificationScreen for displaying notifications
 * - FakeNotificationDispatcher for creating notifications
 * - Push notification system
 */
@Dao
interface NotificationDao {
    
    /**
     * Get all notifications for a user
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    fun getUserNotifications(userId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get unread notifications for a user
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = 0 ORDER BY created_at DESC")
    fun getUnreadNotifications(userId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get read notifications for a user
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = 1 ORDER BY read_at DESC")
    fun getReadNotifications(userId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get notifications by type
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND type = :type ORDER BY created_at DESC")
    fun getNotificationsByType(userId: String, type: String): Flow<List<NotificationEntity>>
    
    /**
     * Get notifications by priority
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND priority = :priority ORDER BY created_at DESC")
    fun getNotificationsByPriority(userId: String, priority: String): Flow<List<NotificationEntity>>
    
    /**
     * Get high priority notifications
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND priority = 'HIGH' ORDER BY created_at DESC")
    fun getHighPriorityNotifications(userId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get notifications related to a specific reservation
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND reservation_id = :reservationId ORDER BY created_at DESC")
    fun getReservationNotifications(userId: String, reservationId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get notifications with actions
     */
    @Query("SELECT * FROM notifications WHERE user_id = :userId AND action_url IS NOT NULL ORDER BY created_at DESC")
    fun getActionableNotifications(userId: String): Flow<List<NotificationEntity>>
    
    /**
     * Get non-expired notifications
     */
    @Query("""
        SELECT * FROM notifications 
        WHERE user_id = :userId 
        AND (expires_at IS NULL OR expires_at > :currentTime)
        ORDER BY created_at DESC
    """)
    fun getNonExpiredNotifications(userId: String, currentTime: LocalDateTime = LocalDateTime.now()): Flow<List<NotificationEntity>>
    
    /**
     * Get recent notifications (last N days)
     */
    @Query("""
        SELECT * FROM notifications 
        WHERE user_id = :userId 
        AND created_at > :sinceTimestamp
        ORDER BY created_at DESC
    """)
    fun getRecentNotifications(userId: String, sinceTimestamp: Long): Flow<List<NotificationEntity>>
    
    /**
     * Get a specific notification by ID
     */
    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: String): NotificationEntity?
    
    /**
     * Get unread notification count
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    fun getUnreadCount(userId: String): Flow<Int>
    
    /**
     * Get unread count by type
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0 AND type = :type")
    suspend fun getUnreadCountByType(userId: String, type: String): Int
    
    /**
     * Insert a new notification
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long
    
    /**
     * Insert multiple notifications
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
    
    /**
     * Update a notification
     */
    @Update
    suspend fun updateNotification(notification: NotificationEntity)
    
    /**
     * Mark notification as read
     */
    @Query("""
        UPDATE notifications 
        SET is_read = 1, read_at = :readAt, updated_at = :timestamp 
        WHERE id = :notificationId
    """)
    suspend fun markAsRead(
        notificationId: String,
        readAt: LocalDateTime = LocalDateTime.now(),
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Mark notification as unread
     */
    @Query("""
        UPDATE notifications 
        SET is_read = 0, read_at = NULL, updated_at = :timestamp 
        WHERE id = :notificationId
    """)
    suspend fun markAsUnread(
        notificationId: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Mark all notifications as read for a user
     */
    @Query("""
        UPDATE notifications 
        SET is_read = 1, read_at = :readAt, updated_at = :timestamp 
        WHERE user_id = :userId AND is_read = 0
    """)
    suspend fun markAllAsRead(
        userId: String,
        readAt: LocalDateTime = LocalDateTime.now(),
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Mark all notifications of a type as read
     */
    @Query("""
        UPDATE notifications 
        SET is_read = 1, read_at = :readAt, updated_at = :timestamp 
        WHERE user_id = :userId AND type = :type AND is_read = 0
    """)
    suspend fun markTypeAsRead(
        userId: String,
        type: String,
        readAt: LocalDateTime = LocalDateTime.now(),
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete a notification
     */
    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)
    
    /**
     * Delete notification by ID
     */
    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotificationById(notificationId: String)
    
    /**
     * Delete all read notifications for a user
     */
    @Query("DELETE FROM notifications WHERE user_id = :userId AND is_read = 1")
    suspend fun deleteReadNotifications(userId: String)
    
    /**
     * Delete expired notifications
     */
    @Query("DELETE FROM notifications WHERE expires_at IS NOT NULL AND expires_at < :currentTime")
    suspend fun deleteExpiredNotifications(currentTime: LocalDateTime = LocalDateTime.now())
    
    /**
     * Delete old notifications (older than specified timestamp)
     */
    @Query("DELETE FROM notifications WHERE created_at < :beforeTimestamp")
    suspend fun deleteOldNotifications(beforeTimestamp: Long)
    
    /**
     * Get total notification count for a user
     */
    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId")
    suspend fun getTotalNotificationCount(userId: String): Int
    
    /**
     * Get notification statistics for a user
     */
    @Query("""
        SELECT 
            COUNT(*) as total_notifications,
            SUM(CASE WHEN is_read = 0 THEN 1 ELSE 0 END) as unread_count,
            SUM(CASE WHEN priority = 'HIGH' THEN 1 ELSE 0 END) as high_priority_count,
            SUM(CASE WHEN action_url IS NOT NULL THEN 1 ELSE 0 END) as actionable_count
        FROM notifications 
        WHERE user_id = :userId
    """)
    suspend fun getNotificationStats(userId: String): NotificationStats?
}

/**
 * Data class for notification statistics
 */
data class NotificationStats(
    @ColumnInfo(name = "total_notifications") val totalNotifications: Int,
    @ColumnInfo(name = "unread_count") val unreadCount: Int,
    @ColumnInfo(name = "high_priority_count") val highPriorityCount: Int,
    @ColumnInfo(name = "actionable_count") val actionableCount: Int
)
