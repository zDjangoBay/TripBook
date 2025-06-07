package com.android.tripbook.data.database.dao

import androidx.room.*
import com.android.tripbook.data.database.entities.ReservationActivityEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Reservation-Activity junction operations
 * 
 * This DAO handles the many-to-many relationship between reservations
 * and activities. It manages activity bookings within reservations.
 * 
 * Key Features:
 * - Reservation-specific activity queries
 * - Activity booking management
 * - Confirmation status tracking
 * - Cost calculations
 * - Scheduling information
 * 
 * Used by:
 * - ActivityManager for business logic
 * - SummaryStep for activity management
 * - ReservationFlow for activity booking
 * - Activity confirmation system
 */
@Dao
interface ReservationActivityDao {
    
    /**
     * Get all activities for a specific reservation
     */
    @Query("SELECT * FROM reservation_activities WHERE reservation_id = :reservationId ORDER BY created_at ASC")
    fun getActivitiesForReservation(reservationId: String): Flow<List<ReservationActivityEntity>>
    
    /**
     * Get a specific reservation-activity by ID
     */
    @Query("SELECT * FROM reservation_activities WHERE id = :id")
    suspend fun getReservationActivityById(id: String): ReservationActivityEntity?
    
    /**
     * Get reservation-activity by reservation and activity IDs
     */
    @Query("SELECT * FROM reservation_activities WHERE reservation_id = :reservationId AND activity_id = :activityId")
    suspend fun getReservationActivity(reservationId: String, activityId: String): ReservationActivityEntity?
    
    /**
     * Get confirmed activities for a reservation
     */
    @Query("SELECT * FROM reservation_activities WHERE reservation_id = :reservationId AND is_confirmed = 1 ORDER BY created_at ASC")
    fun getConfirmedActivities(reservationId: String): Flow<List<ReservationActivityEntity>>
    
    /**
     * Get unconfirmed activities for a reservation
     */
    @Query("SELECT * FROM reservation_activities WHERE reservation_id = :reservationId AND is_confirmed = 0 ORDER BY created_at ASC")
    fun getUnconfirmedActivities(reservationId: String): Flow<List<ReservationActivityEntity>>
    
    /**
     * Get activities with scheduling information
     */
    @Query("""
        SELECT * FROM reservation_activities 
        WHERE reservation_id = :reservationId 
        AND scheduled_date IS NOT NULL 
        ORDER BY scheduled_date ASC, scheduled_time ASC
    """)
    fun getScheduledActivities(reservationId: String): Flow<List<ReservationActivityEntity>>
    
    /**
     * Get activities requiring special requirements
     */
    @Query("""
        SELECT * FROM reservation_activities 
        WHERE reservation_id = :reservationId 
        AND special_requirements IS NOT NULL 
        AND special_requirements != ''
        ORDER BY created_at ASC
    """)
    fun getActivitiesWithSpecialRequirements(reservationId: String): Flow<List<ReservationActivityEntity>>
    
    /**
     * Get total cost of activities for a reservation
     */
    @Query("SELECT SUM(total_price) FROM reservation_activities WHERE reservation_id = :reservationId")
    suspend fun getTotalActivitiesCost(reservationId: String): Double?
    
    /**
     * Get total participant count for a reservation
     */
    @Query("SELECT SUM(participants) FROM reservation_activities WHERE reservation_id = :reservationId")
    suspend fun getTotalParticipants(reservationId: String): Int?
    
    /**
     * Get activity count for a reservation
     */
    @Query("SELECT COUNT(*) FROM reservation_activities WHERE reservation_id = :reservationId")
    suspend fun getActivityCount(reservationId: String): Int
    
    /**
     * Check if activity is already booked for reservation
     */
    @Query("SELECT COUNT(*) > 0 FROM reservation_activities WHERE reservation_id = :reservationId AND activity_id = :activityId")
    suspend fun isActivityBooked(reservationId: String, activityId: String): Boolean
    
    /**
     * Insert a new reservation-activity
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservationActivity(reservationActivity: ReservationActivityEntity): Long
    
    /**
     * Insert multiple reservation-activities
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservationActivities(reservationActivities: List<ReservationActivityEntity>)
    
    /**
     * Update a reservation-activity
     */
    @Update
    suspend fun updateReservationActivity(reservationActivity: ReservationActivityEntity)
    
    /**
     * Update quantity and recalculate total price
     */
    @Query("""
        UPDATE reservation_activities 
        SET quantity = :quantity, 
            total_price = unit_price * :quantity,
            updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun updateQuantity(
        id: String,
        quantity: Int,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update participants count
     */
    @Query("""
        UPDATE reservation_activities 
        SET participants = :participants, updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun updateParticipants(
        id: String,
        participants: Int,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update scheduling information
     */
    @Query("""
        UPDATE reservation_activities 
        SET scheduled_date = :scheduledDate, 
            scheduled_time = :scheduledTime,
            updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun updateSchedule(
        id: String,
        scheduledDate: String?,
        scheduledTime: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update special requirements
     */
    @Query("""
        UPDATE reservation_activities 
        SET special_requirements = :specialRequirements, updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun updateSpecialRequirements(
        id: String,
        specialRequirements: String?,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Confirm activity booking
     */
    @Query("""
        UPDATE reservation_activities 
        SET is_confirmed = 1, 
            confirmation_code = :confirmationCode,
            updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun confirmActivity(
        id: String,
        confirmationCode: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Unconfirm activity booking
     */
    @Query("""
        UPDATE reservation_activities 
        SET is_confirmed = 0, 
            confirmation_code = NULL,
            updated_at = :timestamp 
        WHERE id = :id
    """)
    suspend fun unconfirmActivity(
        id: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete a reservation-activity
     */
    @Delete
    suspend fun deleteReservationActivity(reservationActivity: ReservationActivityEntity)
    
    /**
     * Delete reservation-activity by IDs
     */
    @Query("DELETE FROM reservation_activities WHERE reservation_id = :reservationId AND activity_id = :activityId")
    suspend fun deleteReservationActivity(reservationId: String, activityId: String)
    
    /**
     * Delete all activities for a reservation
     */
    @Query("DELETE FROM reservation_activities WHERE reservation_id = :reservationId")
    suspend fun deleteAllActivitiesForReservation(reservationId: String)
    
    /**
     * Get reservation activity statistics
     */
    @Query("""
        SELECT 
            COUNT(*) as total_activities,
            SUM(CASE WHEN is_confirmed = 1 THEN 1 ELSE 0 END) as confirmed_activities,
            SUM(total_price) as total_cost,
            SUM(participants) as total_participants
        FROM reservation_activities 
        WHERE reservation_id = :reservationId
    """)
    suspend fun getReservationActivityStats(reservationId: String): ReservationActivityStats?
}

/**
 * Data class for reservation activity statistics
 */
data class ReservationActivityStats(
    @ColumnInfo(name = "total_activities") val totalActivities: Int,
    @ColumnInfo(name = "confirmed_activities") val confirmedActivities: Int,
    @ColumnInfo(name = "total_cost") val totalCost: Double,
    @ColumnInfo(name = "total_participants") val totalParticipants: Int
)
