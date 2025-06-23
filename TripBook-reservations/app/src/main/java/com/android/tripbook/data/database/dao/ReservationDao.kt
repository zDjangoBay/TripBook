package com.android.tripbook.data.database.dao

import androidx.room.*
import com.android.tripbook.data.database.entities.ReservationEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for Reservation operations
 * 
 * This DAO handles all database operations for reservations including
 * CRUD operations, status management, and user-specific queries.
 * It supports the ReservationListScreen and booking flow.
 * 
 * Key Features:
 * - User-specific reservation queries
 * - Status-based filtering (PENDING, UPCOMING, COMPLETED, CANCELLED)
 * - Payment status tracking
 * - Reactive data with Flow
 * - Comprehensive reservation management
 * 
 * Used by:
 * - ReservationManager for business logic
 * - ReservationListScreen for displaying reservations
 * - ReservationFlow for creating bookings
 * - PaymentScreen for payment tracking
 */
@Dao
interface ReservationDao {
    
    /**
     * Get all reservations for a specific user
     * Returns a Flow for reactive UI updates
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId ORDER BY booking_date DESC")
    fun getUserReservations(userId: String): Flow<List<ReservationEntity>>
    
    /**
     * Get a specific reservation by ID
     */
    @Query("SELECT * FROM reservations WHERE id = :reservationId")
    suspend fun getReservationById(reservationId: String): ReservationEntity?
    
    /**
     * Get reservations by status for a user
     * Used by ReservationListScreen tabs (Pending, Upcoming, Completed)
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND status = :status ORDER BY booking_date DESC")
    fun getReservationsByStatus(userId: String, status: String): Flow<List<ReservationEntity>>
    
    /**
     * Get pending reservations for a user
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND status = 'PENDING' ORDER BY booking_date DESC")
    fun getPendingReservations(userId: String): Flow<List<ReservationEntity>>
    
    /**
     * Get upcoming reservations for a user
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND status = 'UPCOMING' ORDER BY booking_date ASC")
    fun getUpcomingReservations(userId: String): Flow<List<ReservationEntity>>
    
    /**
     * Get completed reservations for a user
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND status = 'COMPLETED' ORDER BY booking_date DESC")
    fun getCompletedReservations(userId: String): Flow<List<ReservationEntity>>
    
    /**
     * Get cancelled reservations for a user
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND status = 'CANCELLED' ORDER BY cancelled_at DESC")
    fun getCancelledReservations(userId: String): Flow<List<ReservationEntity>>
    
    /**
     * Get reservations by payment status
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND payment_status = :paymentStatus ORDER BY booking_date DESC")
    fun getReservationsByPaymentStatus(userId: String, paymentStatus: String): Flow<List<ReservationEntity>>
    
    /**
     * Get reservations requiring payment
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND payment_status = 'PENDING' ORDER BY booking_date ASC")
    fun getReservationsRequiringPayment(userId: String): Flow<List<ReservationEntity>>
    
    /**
     * Get reservations for a specific trip
     */
    @Query("SELECT * FROM reservations WHERE trip_id = :tripId ORDER BY booking_date DESC")
    fun getReservationsForTrip(tripId: String): Flow<List<ReservationEntity>>
    
    /**
     * Get reservations within a date range
     */
    @Query("""
        SELECT * FROM reservations 
        WHERE user_id = :userId 
        AND booking_date BETWEEN :startDate AND :endDate 
        ORDER BY booking_date DESC
    """)
    fun getReservationsByDateRange(
        userId: String, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): Flow<List<ReservationEntity>>
    
    /**
     * Search reservations by confirmation number
     */
    @Query("SELECT * FROM reservations WHERE user_id = :userId AND confirmation_number LIKE '%' || :confirmationNumber || '%'")
    suspend fun searchByConfirmationNumber(userId: String, confirmationNumber: String): List<ReservationEntity>
    
    /**
     * Get reservation statistics for a user
     */
    @Query("""
        SELECT 
            COUNT(*) as total_reservations,
            SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
            SUM(CASE WHEN status = 'UPCOMING' THEN 1 ELSE 0 END) as upcoming_count,
            SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
            SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_count,
            SUM(total_cost) as total_spent
        FROM reservations 
        WHERE user_id = :userId
    """)
    suspend fun getUserReservationStats(userId: String): ReservationStats?
    
    /**
     * Insert a new reservation
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity): Long
    
    /**
     * Update an existing reservation
     */
    @Update
    suspend fun updateReservation(reservation: ReservationEntity)
    
    /**
     * Update reservation status
     */
    @Query("""
        UPDATE reservations 
        SET status = :status, updated_at = :timestamp 
        WHERE id = :reservationId
    """)
    suspend fun updateReservationStatus(
        reservationId: String, 
        status: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Update payment status
     */
    @Query("""
        UPDATE reservations 
        SET payment_status = :paymentStatus, updated_at = :timestamp 
        WHERE id = :reservationId
    """)
    suspend fun updatePaymentStatus(
        reservationId: String, 
        paymentStatus: String, 
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Cancel a reservation
     */
    @Query("""
        UPDATE reservations 
        SET status = 'CANCELLED', 
            cancelled_at = :cancelledAt, 
            cancellation_reason = :reason,
            updated_at = :timestamp 
        WHERE id = :reservationId
    """)
    suspend fun cancelReservation(
        reservationId: String, 
        cancelledAt: LocalDateTime, 
        reason: String? = null,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Set confirmation number
     */
    @Query("""
        UPDATE reservations 
        SET confirmation_number = :confirmationNumber, updated_at = :timestamp 
        WHERE id = :reservationId
    """)
    suspend fun setConfirmationNumber(
        reservationId: String, 
        confirmationNumber: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Delete a reservation (hard delete)
     */
    @Delete
    suspend fun deleteReservation(reservation: ReservationEntity)
    
    /**
     * Get total reservation count for a user
     */
    @Query("SELECT COUNT(*) FROM reservations WHERE user_id = :userId")
    suspend fun getUserReservationCount(userId: String): Int
    
    /**
     * Get total amount spent by a user
     */
    @Query("SELECT SUM(total_cost) FROM reservations WHERE user_id = :userId AND payment_status = 'COMPLETED'")
    suspend fun getTotalAmountSpent(userId: String): Double?
}

/**
 * Data class for reservation statistics
 */
data class ReservationStats(
    @ColumnInfo(name = "total_reservations") val totalReservations: Int,
    @ColumnInfo(name = "pending_count") val pendingCount: Int,
    @ColumnInfo(name = "upcoming_count") val upcomingCount: Int,
    @ColumnInfo(name = "completed_count") val completedCount: Int,
    @ColumnInfo(name = "cancelled_count") val cancelledCount: Int,
    @ColumnInfo(name = "total_spent") val totalSpent: Double
)


data class Reservation(
    val destination: String,
    val dateDepart: String,
    val dateRetour: String,
    val moyenTransport: String,
    val nombrePersonnes: Int,
    val prixTotal: Double,
    val statut: String // Ex: "Validée", "En attente", "Annulée"
)

val historiqueReservations = listOf(
    Reservation("Paris", "2025-07-01", "2025-07-10", "Avion", 2, 1200.0, "Validée"),
    Reservation("Tokyo", "2025-08-15", "2025-08-30", "Train", 1, 900.0, "En attente"),
    Reservation("Rome", "2025-09-05", "2025-09-15", "Bus", 3, 1500.0, "Annulée")
)

class ReservationAdapter(private val reservations: List<Reservation>) :
    RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDestination: TextView = itemView.findViewById(R.id.txtDestination)
        val txtDates: TextView = itemView.findViewById(R.id.txtDates)
        val txtTransport: TextView = itemView.findViewById(R.id.txtTransport)
        val txtPersonnes: TextView = itemView.findViewById(R.id.txtPersonnes)
        val txtPrix: TextView = itemView.findViewById(R.id.txtPrix)
        val txtStatut: TextView = itemView.findViewById(R.id.txtStatut)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val res = reservations[position]
        holder.txtDestination.text = res.destination
        holder.txtDates.text = "${res.dateDepart} ➡ ${res.dateRetour}"
        holder.txtTransport.text = "Transport : ${res.moyenTransport}"
        holder.txtPersonnes.text = "Personnes : ${res.nombrePersonnes}"
        holder.txtPrix.text = "Total : ${res.prixTotal} €"
        holder.txtStatut.text = "Statut : ${res.statut}"
    }

    override fun getItemCount(): Int = reservations.size
}

