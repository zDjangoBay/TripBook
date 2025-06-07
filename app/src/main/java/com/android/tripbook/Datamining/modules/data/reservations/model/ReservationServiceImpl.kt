package com.android.reservations.model

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.and
import org.litote.kmongo.setValue
import org.litote.kmongo.*
import redis.clients.jedis.Jedis
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.util.UUID

object ReservationCacheKeys {
    fun reservationById(reservationId: String) = "reservation:$reservationId"
    
    fun userReservationsPage(userId: String, page: Int, pageSize: Int) = "reservations:user:$userId:page:$page:size:$pageSize"

}

const val RESERVATION_CACHE_TTL_SECONDS: Long = 1800 // 30 minutes

class ReservationServiceImpl(
    private val mongoDb: CoroutineDatabase,
    private val redis: Jedis,
    private val jsonMapper: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false

    }
) : ReservationsService {

    private val reservationsCollection: CoroutineCollection<Reservation> = mongoDb.getCollection("reservations")


    private fun parseDateTime(dateTimeString: String?): LocalDateTime? {
        return try {
            dateTimeString?.let { LocalDateTime.parse(it) }
        } catch (e: DateTimeParseException) {
            null
        }
    }

    override suspend fun createReservation(userId: String, request: CreateReservationRequest): Reservation? {
        val startDate = parseDateTime(request.startDate) ?: return null // Basic validation
        val endDate = parseDateTime(request.endDate) ?: return null
        if (endDate.isBefore(startDate)) return null

        val newReservation = Reservation(
            userId = userId,
            
            title = request.title,

            
            destination = request.destination,
            startDate = startDate,
            endDate = endDate,
            
            status = request.status,
            imageUrl = request.imageUrl,
            price = request.price,
            currency = request.currency ?: "FCFA", // Ou should we put Dollars since it is acknowledged globally ?
            
            bookingReference = request.bookingReference,
            
            notes = request.notes,
            accommodationName = request.accommodationName,
            accommodationAddress = request.accommodationAddress,
            transportInfo = request.transportInfo,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        try {
            val insertResult = reservationsCollection.insertOne(newReservation)
            if (!insertResult.wasAcknowledged()) {
                println("MongoDB insert failed for reservation: ${newReservation.title}")
                return null
            }

            val reservationJson = jsonMapper.encodeToString(newReservation)
            redis.setex(ReservationCacheKeys.reservationById(newReservation.id), RESERVATION_CACHE_TTL_SECONDS, reservationJson)

            redis.del(ReservationCacheKeys.userReservationsPage(userId, 1, 20))

            return newReservation
        } catch (e: Exception) {
            println("Error creating reservation for user $userId: ${e.message}")
            return null
        }
    }

    override suspend fun getReservationById(reservationId: String, userId: String): Reservation? {
        val cacheKey = ReservationCacheKeys.reservationById(reservationId)
        try {
            val cachedJson = redis.get(cacheKey)
            if (cachedJson != null) {
                val reservation = jsonMapper.decodeFromString<Reservation>(cachedJson)

                return if (reservation.userId == userId) reservation else null
            }

            val dbReservation = reservationsCollection.findOne(
                and(
                    Reservation::id eq reservationId,
                    Reservation::userId eq userId
                )
            )

            if (dbReservation != null) {
                redis.setex(cacheKey, RESERVATION_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbReservation))
            }
            return dbReservation
        } catch (e: Exception) {
            println("Error getting reservation by ID $reservationId for user $userId: ${e.message}")
            return null
        }
    }

    override suspend fun getUserReservations(
        userId: String,
        filter: ReservationFilter?,
        page: Int,
        pageSize: Int
    ): List<Reservation> {

        val cacheKey = if (filter == null && page == 1) ReservationCacheKeys.userReservationsPage(userId, page, pageSize) else null

        try {
            if (cacheKey != null) {
                val cachedJson = redis.get(cacheKey)
                if (cachedJson != null) {
                    return jsonMapper.decodeFromString<List<Reservation>>(cachedJson)
                }
            }

            val skip = (page - 1) * pageSize
            val queryFilters = mutableListOf(Reservation::userId eq userId)

            filter?.let { f ->
                f.status?.takeIf { it.isNotEmpty() }?.let { queryFilters.add(Reservation::status `in` it) }
                f.destination?.let { queryFilters.add(Reservation::destination regex it options "i") } // Case-insensitive regex
                f.startDateFrom?.let { queryFilters.add(Reservation::startDate gte it) }
                f.startDateTo?.let { queryFilters.add(Reservation::startDate lte it) }
                f.priceMin?.let { queryFilters.add(Reservation::price gte it) }
                f.priceMax?.let { queryFilters.add(Reservation::price lte it) }
                f.searchQuery?.let {
                    queryFilters.add(
                        or( // We are searching in the titles , the destinations , the notes and even the accomodationName
                            Reservation::title regex it options "i",
                            Reservation::destination regex it options "i",
                            Reservation::notes regex it options "i",
                            Reservation::accommodationName regex it options "i"
                        )
                    )
                }
            }

            val dbReservations = reservationsCollection.find(and(queryFilters))
                .descendingSort(Reservation::startDate) // Example sort
                .skip(skip)
                .limit(pageSize)
                .toList()

            if (cacheKey != null && dbReservations.isNotEmpty()) {
                redis.setex(cacheKey, RESERVATION_CACHE_TTL_SECONDS, jsonMapper.encodeToString(dbReservations))
            }
            return dbReservations
        } catch (e: Exception) {
            println("Error getting reservations for user $userId: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun updateReservation(
        reservationId: String,
        userId: String,
        request: UpdateReservationRequest
    ): Reservation? {
        try {
            val existingReservation = reservationsCollection.findOne(
                and(
                    Reservation::id eq reservationId,
                    Reservation::userId eq userId
                )
            ) ?: return null // Not found or not owner

            // Build updates selectively
            val updates = mutableListOf<Bson>()
            request.title?.let { updates.add(setValue(Reservation::title, it)) }
            
            request.destination?.let { updates.add(setValue(Reservation::destination, it)) }
            
            parseDateTime(request.startDate)?.let { updates.add(setValue(Reservation::startDate, it)) }
            
            parseDateTime(request.endDate)?.let { updates.add(setValue(Reservation::endDate, it)) }
            
            request.status?.let { updates.add(setValue(Reservation::status, it)) }
            
            request.imageUrl?.let { updates.add(setValue(Reservation::imageUrl, it)) }
            
            request.price?.let { updates.add(setValue(Reservation::price, it)) }
            request.currency?.let { updates.add(setValue(Reservation::currency, it)) }
            
            request.bookingReference?.let { updates.add(setValue(Reservation::bookingReference, it)) }
            request.notes?.let { updates.add(setValue(Reservation::notes, it)) }
            request.accommodationName?.let { updates.add(setValue(Reservation::accommodationName, it)) }
            
            request.accommodationAddress?.let { updates.add(setValue(Reservation::accommodationAddress, it)) }
            request.transportInfo?.let { updates.add(setValue(Reservation::transportInfo, it)) }

            if (updates.isEmpty()) return existingReservation

            updates.add(setValue(Reservation::updatedAt, LocalDateTime.now()))

            val updateResult = reservationsCollection.updateOne(
                Reservation::id eq reservationId,
                
                    combine(updates)
            )

            if (updateResult.modifiedCount == 0L) {

                return reservationsCollection.findOne(Reservation::id eq reservationId)
            }

            val updatedReservation = reservationsCollection.findOne(Reservation::id eq reservationId)
            if (updatedReservation != null) {
                redis.setex(
                    
                    ReservationCacheKeys.reservationById(reservationId),
                            RESERVATION_CACHE_TTL_SECONDS,
                    
                    jsonMapper.encodeToString(updatedReservation)
                )
                // Supposed to invalidate list caches 
                redis.del(ReservationCacheKeys.userReservationsPage(userId, 1, 20))
            }
            return updatedReservation

        } catch (e: Exception) {
            println("Error updating reservation $reservationId for user $userId: ${e.message}")
            return null
        }
    }

    override suspend fun updateReservationStatus(
        reservationId: String,
        userId: String,
        newStatus: ReservationStatus
    ): Reservation? {
        try {
            val updateResult = reservationsCollection.updateOne(
                and(
                    Reservation::id eq reservationId,
                    Reservation::userId eq userId
                ),
                combine(
                    setValue(Reservation::status, newStatus),
                    setValue(Reservation::updatedAt, LocalDateTime.now())
                )
            )

            if (updateResult.modifiedCount == 0L) {

                return reservationsCollection.findOne(and(Reservation::id eq reservationId, Reservation::userId eq userId))
            }

            val updatedReservation = reservationsCollection.findOne(Reservation::id eq reservationId)
            
            if (updatedReservation != null) {
                redis.setex(
                    
                    ReservationCacheKeys.reservationById(reservationId),
                    
                    RESERVATION_CACHE_TTL_SECONDS,
                    
                    
                    
                    jsonMapper.encodeToString(updatedReservation)
                )

                redis.del(ReservationCacheKeys.userReservationsPage(userId, 1, 20))
            }
            return updatedReservation
        } catch (e: Exception) {
            println("Error updating status for reservation $reservationId: ${e.message}")
            return null
        }
    }

    override suspend fun deleteReservation(reservationId: String, userId: String): Boolean {
        try {    
                val deleteResult = reservationsCollection.deleteOne(
                and(
                    Reservation::id eq reservationId,
                        Reservation::userId eq userId
                )
                
    )
            if (deleteResult.deletedCount > 0)
            {
                
                redis.del(ReservationCacheKeys.reservationById(reservationId))

                redis.del(ReservationCacheKeys.userReservationsPage(userId, 1, 20))
                return true
            }
            return false
        } catch (e: Exception) {
            println("Error deleting reservation $reservationId for user $userId: ${e.message}")
            return false
        }
    }
}
