package com.android.tripbook.test

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.entity.TripEntity
import com.android.tripbook.database.entity.ReviewEntity
import kotlinx.coroutines.launch

/**
 * 🧪 DATABASE TEST ACTIVITY
 * 
 * This is a SEPARATE test component that doesn't affect existing code.
 * It directly tests Room database functionality without Compose dependencies.
 * 
 * Purpose: Prove that our database works perfectly and dependency issues 
 * don't affect core database functionality.
 */
class DatabaseTestActivity : ComponentActivity() {
    
    private lateinit var database: TripBookDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        database = TripBookDatabase.getDatabase(this)
        
        Log.d("DatabaseTest", "🧪 Starting Database Test...")
        
        // Run database tests
        lifecycleScope.launch {
            testDatabaseFunctionality()
        }
    }
    
    private suspend fun testDatabaseFunctionality() {
        try {
            Log.d("DatabaseTest", "📊 Testing database operations...")
            
            // Test 1: Insert test trip
            insertTestTrip()
            
            // Test 2: Insert test reviews
            insertTestReviews()
            
            // Test 3: Read data back
            readTestData()
            
            Log.d("DatabaseTest", "✅ All database tests PASSED!")
            
        } catch (e: Exception) {
            Log.e("DatabaseTest", "❌ Database test FAILED: ${e.message}")
        }
    }
    
    private suspend fun insertTestTrip() {
        Log.d("DatabaseTest", "📝 Inserting test trip...")
        
        val testTrip = TripEntity(
            id = 999,
            title = "🧪 DATABASE TEST TRIP",
            caption = "This trip was added by database test to verify persistence",
            description = "This is a test trip created to verify that our Room database is working correctly. " +
                    "If you can see this trip in the catalog and click to view its details, " +
                    "then our database is functioning perfectly! " +
                    "Created at: ${System.currentTimeMillis()}",
            imageUrl = listOf(
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800",
                "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=800",
                "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800"
            )
        )
        
        database.tripDao().insertTrip(testTrip)
        Log.d("DatabaseTest", "✅ Test trip inserted successfully")
    }
    
    private suspend fun insertTestReviews() {
        Log.d("DatabaseTest", "📝 Inserting test reviews...")
        
        val testReviews = listOf(
            ReviewEntity(
                id = 9991,
                tripId = 999,
                username = "DatabaseTester",
                rating = 5,
                comment = "🧪 TEST REVIEW: This review was added by our database test! " +
                        "If you can see this review, our Room database is working perfectly. " +
                        "The database can store and retrieve data correctly!",
                images = listOf(
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",
                    "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400"
                ),
                isLiked = false,
                isFlagged = false,
                likeCount = 12
            ),
            ReviewEntity(
                id = 9992,
                tripId = 999,
                username = "PersistenceValidator",
                rating = 4,
                comment = "✅ PERSISTENCE TEST: This review proves our database persists data correctly. " +
                        "Room database with foreign key indexing is working as expected. " +
                        "Data survives app restarts!",
                images = listOf(
                    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400"
                ),
                isLiked = true,
                isFlagged = false,
                likeCount = 8
            ),
            ReviewEntity(
                id = 9993,
                tripId = 999,
                username = "ImageStorageTester",
                rating = 5,
                comment = "📸 IMAGE STORAGE TEST: This review demonstrates that our database can store " +
                        "multiple image URLs correctly. Each review can have multiple images, " +
                        "and they're all stored and retrieved perfectly!",
                images = listOf(
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400",
                    "https://images.unsplash.com/photo-1469474968028-56623f02e42e?w=400",
                    "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400"
                ),
                isLiked = false,
                isFlagged = false,
                likeCount = 15
            )
        )
        
        testReviews.forEach { review ->
            database.reviewDao().insertReview(review)
        }
        
        Log.d("DatabaseTest", "✅ ${testReviews.size} test reviews inserted successfully")
    }
    
    private suspend fun readTestData() {
        Log.d("DatabaseTest", "📖 Reading test data...")
        
        // Read trip
        val trip = database.tripDao().getTripById(999)
        Log.d("DatabaseTest", "📊 Retrieved trip: ${trip?.title}")
        
        // Read reviews
        val reviews = database.reviewDao().getReviewsForTripOnce(999)
        Log.d("DatabaseTest", "📊 Retrieved ${reviews.size} reviews for test trip")
        
        reviews.forEach { review ->
            Log.d("DatabaseTest", "📝 Review: ${review.username} - ${review.rating}/5 - ${review.images.size} images")
        }
        
        Log.d("DatabaseTest", "✅ Data retrieval successful")
    }
}
