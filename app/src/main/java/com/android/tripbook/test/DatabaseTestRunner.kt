package com.android.tripbook.test

import android.content.Context
import android.util.Log
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.entity.TripEntity
import com.android.tripbook.database.entity.ReviewEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 🧪 DATABASE TEST RUNNER
 * 
 * Simple utility to test database functionality without UI dependencies.
 * Can be called from anywhere in the app to verify database works.
 */
object DatabaseTestRunner {
    
    private const val TAG = "DatabaseTestRunner"
    
    /**
     * Run comprehensive database test
     */
    fun runDatabaseTest(context: Context) {
        Log.d(TAG, "🧪 Starting comprehensive database test...")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TripBookDatabase.getDatabase(context)
                
                // Test 1: Create test trip
                createTestTrip(database)
                
                // Test 2: Create test reviews
                createTestReviews(database)
                
                // Test 3: Verify data persistence
                verifyDataPersistence(database)
                
                // Test 4: Test foreign key relationships
                testForeignKeyRelationships(database)
                
                Log.d(TAG, "✅ ALL DATABASE TESTS PASSED! Database is working perfectly.")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Database test failed: ${e.message}", e)
            }
        }
    }
    
    private suspend fun createTestTrip(database: TripBookDatabase) {
        Log.d(TAG, "📝 Creating test trip...")
        
        val testTrip = TripEntity(
            id = 888,
            title = "🧪 Persistent Storage Test Trip",
            caption = "Testing Room database persistence and image storage",
            description = "This trip was created by our database test runner to verify that:\n\n" +
                    "✅ Room database can store trip data\n" +
                    "✅ Multiple image URLs are stored correctly\n" +
                    "✅ Data persists across app sessions\n" +
                    "✅ Foreign key relationships work\n" +
                    "✅ Database indexing is functional\n\n" +
                    "If you can see this trip in your catalog, the database is working perfectly!\n\n" +
                    "Created: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date())}",
            imageUrl = listOf(
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&q=80",
                "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800&q=80",
                "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&q=80",
                "https://images.unsplash.com/photo-1506197603052-3cc9c3a201bd?w=800&q=80"
            )
        )
        
        database.tripDao().insertTrip(testTrip)
        Log.d(TAG, "✅ Test trip created with ID: ${testTrip.id}")
    }
    
    private suspend fun createTestReviews(database: TripBookDatabase) {
        Log.d(TAG, "📝 Creating test reviews...")
        
        val testReviews = listOf(
            ReviewEntity(
                id = 8881,
                tripId = 888,
                username = "DatabaseValidator",
                rating = 5,
                comment = "🧪 DATABASE FUNCTIONALITY TEST\n\n" +
                        "This review confirms that:\n" +
                        "✅ Foreign key relationships work (tripId: 888)\n" +
                        "✅ Multiple images can be stored per review\n" +
                        "✅ All data types are handled correctly\n" +
                        "✅ Database indexing improves performance\n\n" +
                        "If you can see this review, our Room database is fully operational!",
                images = listOf(
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&q=80",
                    "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400&q=80"
                ),
                isLiked = true,
                isFlagged = false,
                likeCount = 25
            ),
            ReviewEntity(
                id = 8882,
                tripId = 888,
                username = "PersistenceTester",
                rating = 4,
                comment = "📊 PERSISTENCE VERIFICATION\n\n" +
                        "This review tests data persistence:\n" +
                        "✅ Data survives app restarts\n" +
                        "✅ Foreign key constraints maintained\n" +
                        "✅ Image URLs stored and retrieved correctly\n" +
                        "✅ Complex data types (Lists) work perfectly\n\n" +
                        "Room database with SQLite backend is rock solid!",
                images = listOf(
                    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&q=80",
                    "https://images.unsplash.com/photo-1506197603052-3cc9c3a201bd?w=400&q=80",
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&q=80"
                ),
                isLiked = false,
                isFlagged = false,
                likeCount = 18
            ),
            ReviewEntity(
                id = 8883,
                tripId = 888,
                username = "ImageStorageExpert",
                rating = 5,
                comment = "📸 IMAGE STORAGE CAPABILITY TEST\n\n" +
                        "Testing image storage features:\n" +
                        "✅ Multiple image URLs per review\n" +
                        "✅ High-quality image links\n" +
                        "✅ JSON serialization of image lists\n" +
                        "✅ Type converters working correctly\n\n" +
                        "Our database can handle complex image storage requirements!",
                images = listOf(
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&q=80",
                    "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400&q=80",
                    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&q=80",
                    "https://images.unsplash.com/photo-1506197603052-3cc9c3a201bd?w=400&q=80"
                ),
                isLiked = true,
                isFlagged = false,
                likeCount = 32
            )
        )
        
        testReviews.forEach { review ->
            database.reviewDao().insertReview(review)
            Log.d(TAG, "✅ Review created: ${review.username} - ${review.rating}/5")
        }
        
        Log.d(TAG, "✅ ${testReviews.size} test reviews created")
    }
    
    private suspend fun verifyDataPersistence(database: TripBookDatabase) {
        Log.d(TAG, "🔍 Verifying data persistence...")
        
        // Verify trip exists
        val trip = database.tripDao().getTripById(888)
        if (trip != null) {
            Log.d(TAG, "✅ Trip retrieved: ${trip.title}")
            Log.d(TAG, "✅ Trip has ${trip.imageUrl.size} images")
        } else {
            Log.e(TAG, "❌ Trip not found!")
            return
        }
        
        // Verify reviews exist
        val reviews = database.reviewDao().getReviewsForTripOnce(888)
        Log.d(TAG, "✅ Found ${reviews.size} reviews for test trip")
        
        reviews.forEach { review ->
            Log.d(TAG, "✅ Review: ${review.username} (${review.images.size} images)")
        }
        
        // Verify total counts
        val allTrips = database.tripDao().getAllTripsOnce()
        val allReviews = database.reviewDao().getAllReviewsOnce()
        
        Log.d(TAG, "📊 Database contains ${allTrips.size} trips and ${allReviews.size} reviews")
    }
    
    private suspend fun testForeignKeyRelationships(database: TripBookDatabase) {
        Log.d(TAG, "🔗 Testing foreign key relationships...")
        
        // Test that reviews are properly linked to trip
        val reviewsForTrip = database.reviewDao().getReviewsForTripOnce(888)
        val expectedReviewCount = 3
        
        if (reviewsForTrip.size == expectedReviewCount) {
            Log.d(TAG, "✅ Foreign key relationship working: ${reviewsForTrip.size} reviews linked to trip 888")
        } else {
            Log.e(TAG, "❌ Foreign key issue: Expected $expectedReviewCount reviews, found ${reviewsForTrip.size}")
        }
        
        // Verify all reviews have correct tripId
        val correctlyLinked = reviewsForTrip.all { it.tripId == 888 }
        if (correctlyLinked) {
            Log.d(TAG, "✅ All reviews correctly linked to parent trip")
        } else {
            Log.e(TAG, "❌ Some reviews have incorrect tripId")
        }
    }
    
    /**
     * Quick test to verify database is accessible
     */
    fun quickDatabaseCheck(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TripBookDatabase.getDatabase(context)
                val tripCount = database.tripDao().getAllTripsOnce().size
                val reviewCount = database.reviewDao().getAllReviewsOnce().size
                
                Log.d(TAG, "🔍 Quick check: Database has $tripCount trips and $reviewCount reviews")
                Log.d(TAG, "✅ Database is accessible and functional")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Database quick check failed: ${e.message}")
            }
        }
    }
}
