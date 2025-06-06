package com.android.tripbook.repository

import android.util.Log
import com.android.tripbook.data.SupabaseConfig
import com.android.tripbook.data.models.SupabaseReview
import com.android.tripbook.data.models.SupabaseRating
import com.android.tripbook.data.models.SupabaseReviewSummary
import com.android.tripbook.model.Review
import com.android.tripbook.model.Rating
import com.android.tripbook.model.ReviewSummary
import com.android.tripbook.model.ReviewType
import com.android.tripbook.model.ReviewStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseReviewRepository {
    private val supabase = SupabaseConfig.client
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Submit a new review
    suspend fun submitReview(review: Review): Result<Review> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Submitting review for ${review.reviewType} ${review.targetId}")
            
            val supabaseReview = SupabaseReview.fromReview(review)
            val insertedReview = supabase.from(REVIEWS_TABLE)
                .insert(supabaseReview)
                .decodeSingle<SupabaseReview>()
            
            Log.d(TAG, "Review submitted successfully with ID: ${insertedReview.id}")
            Result.success(insertedReview.toReview())
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting review: ${e.message}", e)
            _error.value = "Failed to submit review: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    // Submit a quick rating without full review
    suspend fun submitRating(rating: Rating): Result<Rating> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Submitting rating for ${rating.reviewType} ${rating.targetId}")
            
            val supabaseRating = SupabaseRating.fromRating(rating)
            val insertedRating = supabase.from(RATINGS_TABLE)
                .upsert(supabaseRating) // Use upsert to handle duplicate user ratings
                .decodeSingle<SupabaseRating>()
            
            Log.d(TAG, "Rating submitted successfully")
            Result.success(insertedRating.toRating())
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting rating: ${e.message}", e)
            _error.value = "Failed to submit rating: ${e.message}"
            Result.failure(e)
        } finally {
            _isLoading.value = false
        }
    }

    // Get reviews for a specific target (trip, agency, destination, activity)
    suspend fun getReviewsForTarget(
        reviewType: ReviewType,
        targetId: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<Review> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Loading reviews for $reviewType $targetId")
            
            val supabaseReviews = supabase.from(REVIEWS_TABLE)
                .select() {
                    filter {
                        eq("review_type", reviewType.name)
                        eq("target_id", targetId)
                        eq("status", ReviewStatus.APPROVED.name)
                    }
                }
                .decodeList<SupabaseReview>()
            
            val reviews = supabaseReviews.map { it.toReview() }
                .sortedByDescending { it.createdAt }
                .drop(offset)
                .take(limit)
            Log.d(TAG, "Loaded ${reviews.size} reviews")

            reviews
        } catch (e: Exception) {
            Log.e(TAG, "Error loading reviews: ${e.message}", e)
            _error.value = "Failed to load reviews: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    // Get review summary for a target
    suspend fun getReviewSummary(reviewType: ReviewType, targetId: String): ReviewSummary? {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Loading review summary for $reviewType $targetId")
            
            val summaryData = supabase.from(REVIEW_SUMMARIES_VIEW)
                .select() {
                    filter {
                        eq("review_type", reviewType.name)
                        eq("target_id", targetId)
                    }
                }
                .decodeSingleOrNull<SupabaseReviewSummary>()
            
            val summary = summaryData?.toReviewSummary()
            Log.d(TAG, "Loaded review summary: ${summary?.totalReviews} reviews, avg ${summary?.averageRating}")
            
            summary
        } catch (e: Exception) {
            Log.e(TAG, "Error loading review summary: ${e.message}", e)
            _error.value = "Failed to load review summary: ${e.message}"
            null
        } finally {
            _isLoading.value = false
        }
    }

    // Get user's rating for a specific target
    suspend fun getUserRating(userId: String, reviewType: ReviewType, targetId: String): Rating? {
        return try {
            Log.d(TAG, "Loading user rating for $reviewType $targetId")
            
            val supabaseRating = supabase.from(RATINGS_TABLE)
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("review_type", reviewType.name)
                        eq("target_id", targetId)
                    }
                }
                .decodeSingleOrNull<SupabaseRating>()
            
            supabaseRating?.toRating()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading user rating: ${e.message}", e)
            null
        }
    }

    // Get user's review for a specific target
    suspend fun getUserReview(userId: String, reviewType: ReviewType, targetId: String): Review? {
        return try {
            Log.d(TAG, "Loading user review for $reviewType $targetId")
            
            val supabaseReview = supabase.from(REVIEWS_TABLE)
                .select() {
                    filter {
                        eq("user_id", userId)
                        eq("review_type", reviewType.name)
                        eq("target_id", targetId)
                    }
                }
                .decodeSingleOrNull<SupabaseReview>()
            
            supabaseReview?.toReview()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading user review: ${e.message}", e)
            null
        }
    }

    // Mark review as helpful/not helpful
    suspend fun markReviewHelpful(reviewId: String, userId: String, isHelpful: Boolean): Result<Boolean> {
        return try {
            Log.d(TAG, "Marking review $reviewId as ${if (isHelpful) "helpful" else "not helpful"}")
            
            val helpfulnessData = mapOf(
                "review_id" to reviewId,
                "user_id" to userId,
                "is_helpful" to isHelpful
            )
            
            supabase.from(REVIEW_HELPFULNESS_TABLE)
                .upsert(helpfulnessData)
            
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking review helpfulness: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Get recent reviews across all types (for community feed)
    suspend fun getRecentReviews(limit: Int = 10): List<Review> {
        return try {
            _isLoading.value = true
            _error.value = null
            
            Log.d(TAG, "Loading recent reviews")
            
            val supabaseReviews = supabase.from(REVIEWS_TABLE)
                .select() {
                    filter {
                        eq("status", ReviewStatus.APPROVED.name)
                    }
                }
                .decodeList<SupabaseReview>()
            
            val reviews = supabaseReviews.map { it.toReview() }
                .sortedByDescending { it.createdAt }
                .take(limit)
            Log.d(TAG, "Loaded ${reviews.size} recent reviews")

            reviews
        } catch (e: Exception) {
            Log.e(TAG, "Error loading recent reviews: ${e.message}", e)
            _error.value = "Failed to load recent reviews: ${e.message}"
            emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        private const val TAG = "SupabaseReviewRepository"
        private const val REVIEWS_TABLE = "reviews"
        private const val RATINGS_TABLE = "ratings"
        private const val REVIEW_HELPFULNESS_TABLE = "review_helpfulness"
        private const val REVIEW_SUMMARIES_VIEW = "review_summaries"

        @Volatile
        private var INSTANCE: SupabaseReviewRepository? = null

        fun getInstance(): SupabaseReviewRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SupabaseReviewRepository().also { INSTANCE = it }
            }
        }
    }
}
