package com.android.tripbook.data.models

import com.android.tripbook.model.Trip
import com.android.tripbook.model.TravelCompanion
import com.android.tripbook.model.TripCategory
import com.android.tripbook.model.TripStatus
import com.android.tripbook.model.ItineraryItem
import com.android.tripbook.model.ItineraryType
import com.android.tripbook.model.Location
import com.android.tripbook.model.Review
import com.android.tripbook.model.Rating
import com.android.tripbook.model.ReviewSummary
import com.android.tripbook.model.ReviewType
import com.android.tripbook.model.ReviewStatus
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Serializable models for Supabase database operations

@Serializable
data class SupabaseTravelCompanion(
    val id: String? = null,
    val trip_id: String,
    val name: String,
    val email: String = "",
    val phone: String = "",
    val created_at: String? = null
) {
    fun toTravelCompanion(): TravelCompanion {
        return TravelCompanion(
            name = name,
            email = email,
            phone = phone
        )
    }
    
    companion object {
        fun fromTravelCompanion(companion: TravelCompanion, tripId: String): SupabaseTravelCompanion {
            return SupabaseTravelCompanion(
                trip_id = tripId,
                name = companion.name,
                email = companion.email,
                phone = companion.phone
            )
        }
    }
}

@Serializable
data class SupabaseTrip(
    val id: String? = null,
    val name: String,
    val start_date: String, // ISO date string
    val end_date: String,   // ISO date string
    val destination: String,
    val travelers: Int,
    val budget: Int,
    val status: String,     // TripStatus as string
    val category: String,   // TripCategory as string
    val description: String = "",
    val created_at: String? = null,
    val updated_at: String? = null
) {
    fun toTrip(companions: List<TravelCompanion> = emptyList()): Trip {
        return Trip(
            id = id ?: "",
            name = name,
            startDate = LocalDate.parse(start_date),
            endDate = LocalDate.parse(end_date),
            destination = destination,
            travelers = travelers,
            budget = budget,
            status = TripStatus.valueOf(status),
            category = TripCategory.valueOf(category),
            description = description,
            companions = companions
        )
    }
    
    companion object {
        fun fromTrip(trip: Trip): SupabaseTrip {
            return SupabaseTrip(
                id = trip.id.takeIf { it.isNotEmpty() },
                name = trip.name,
                start_date = trip.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                end_date = trip.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                destination = trip.destination,
                travelers = trip.travelers,
                budget = trip.budget,
                status = trip.status.name,
                category = trip.category.name,
                description = trip.description
            )
        }
    }
}

@Serializable
data class SupabaseItineraryItem(
    val id: String? = null,
    val trip_id: String,
    val date: String, // ISO date string
    val time: String,
    val title: String,
    val location: String,
    val type: String, // ItineraryType as string
    val notes: String = "",
    val description: String = "",
    val duration: String = "",
    val cost: Double = 0.0,
    val is_completed: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String = "",
    val place_id: String = "",
    val created_at: String? = null,
    val updated_at: String? = null
) {
    fun toItineraryItem(): ItineraryItem {
        return ItineraryItem(
            id = id ?: "",
            tripId = trip_id,
            date = LocalDate.parse(date),
            time = time,
            title = title,
            location = location,
            type = ItineraryType.valueOf(type),
            notes = notes,
            description = description,
            duration = duration,
            cost = cost,
            isCompleted = is_completed,
            coordinates = if (latitude != null && longitude != null) {
                Location(latitude, longitude, address, place_id)
            } else null
        )
    }

    companion object {
        fun fromItineraryItem(item: ItineraryItem): SupabaseItineraryItem {
            return SupabaseItineraryItem(
                id = item.id.takeIf { it.isNotEmpty() },
                trip_id = item.tripId,
                date = item.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                time = item.time,
                title = item.title,
                location = item.location,
                type = item.type.name,
                notes = item.notes,
                description = item.description,
                duration = item.duration,
                cost = item.cost,
                is_completed = item.isCompleted,
                latitude = item.coordinates?.latitude,
                longitude = item.coordinates?.longitude,
                address = item.coordinates?.address ?: "",
                place_id = item.coordinates?.placeId ?: ""
            )
        }
    }
}

// Response models for database operations
@Serializable
data class TripWithCompanions(
    val trip: SupabaseTrip,
    val companions: List<SupabaseTravelCompanion> = emptyList()
) {
    fun toTrip(): Trip {
        return trip.toTrip(companions.map { it.toTravelCompanion() })
    }
}

@Serializable
data class TripWithDetails(
    val trip: SupabaseTrip,
    val companions: List<SupabaseTravelCompanion> = emptyList(),
    val itinerary: List<SupabaseItineraryItem> = emptyList()
) {
    fun toTrip(): Trip {
        return trip.toTrip(companions.map { it.toTravelCompanion() })
            .copy(itinerary = itinerary.map { it.toItineraryItem() })
    }
}

// Review and Rating Supabase Models
@Serializable
data class SupabaseReview(
    val id: String? = null,
    val user_id: String,
    val user_name: String,
    val user_avatar: String = "",
    val review_type: String, // ReviewType as string
    val target_id: String,
    val target_name: String,
    val rating: Float,
    val title: String,
    val content: String,
    val pros: List<String> = emptyList(),
    val cons: List<String> = emptyList(),
    val photos: List<String> = emptyList(),
    val helpful_count: Int = 0,
    val is_verified: Boolean = false,
    val status: String = "PENDING", // ReviewStatus as string
    val created_at: String? = null,
    val updated_at: String? = null
) {
    fun toReview(): Review {
        return Review(
            id = id ?: "",
            userId = user_id,
            userName = user_name,
            userAvatar = user_avatar,
            reviewType = ReviewType.valueOf(review_type),
            targetId = target_id,
            targetName = target_name,
            rating = rating,
            title = title,
            content = content,
            pros = pros,
            cons = cons,
            photos = photos,
            helpfulCount = helpful_count,
            isVerified = is_verified,
            status = ReviewStatus.valueOf(status),
            createdAt = created_at?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now(),
            updatedAt = updated_at?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
        )
    }

    companion object {
        fun fromReview(review: Review): SupabaseReview {
            return SupabaseReview(
                id = review.id.takeIf { it.isNotEmpty() },
                user_id = review.userId,
                user_name = review.userName,
                user_avatar = review.userAvatar,
                review_type = review.reviewType.name,
                target_id = review.targetId,
                target_name = review.targetName,
                rating = review.rating,
                title = review.title,
                content = review.content,
                pros = review.pros,
                cons = review.cons,
                photos = review.photos,
                helpful_count = review.helpfulCount,
                is_verified = review.isVerified,
                status = review.status.name
            )
        }
    }
}

@Serializable
data class SupabaseRating(
    val id: String? = null,
    val user_id: String,
    val review_type: String, // ReviewType as string
    val target_id: String,
    val rating: Float,
    val created_at: String? = null
) {
    fun toRating(): Rating {
        return Rating(
            id = id ?: "",
            userId = user_id,
            reviewType = ReviewType.valueOf(review_type),
            targetId = target_id,
            rating = rating,
            createdAt = created_at?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
        )
    }

    companion object {
        fun fromRating(rating: Rating): SupabaseRating {
            return SupabaseRating(
                id = rating.id.takeIf { it.isNotEmpty() },
                user_id = rating.userId,
                review_type = rating.reviewType.name,
                target_id = rating.targetId,
                rating = rating.rating
            )
        }
    }
}

@Serializable
data class SupabaseReviewSummary(
    val review_type: String,
    val target_id: String,
    val total_reviews: Int,
    val average_rating: Float,
    val five_star_count: Int = 0,
    val four_star_count: Int = 0,
    val three_star_count: Int = 0,
    val two_star_count: Int = 0,
    val one_star_count: Int = 0
) {
    fun toReviewSummary(): ReviewSummary {
        val ratingDistribution = mapOf(
            5 to five_star_count,
            4 to four_star_count,
            3 to three_star_count,
            2 to two_star_count,
            1 to one_star_count
        )

        return ReviewSummary(
            targetId = target_id,
            reviewType = ReviewType.valueOf(review_type),
            averageRating = average_rating,
            totalReviews = total_reviews,
            ratingDistribution = ratingDistribution
        )
    }
}
