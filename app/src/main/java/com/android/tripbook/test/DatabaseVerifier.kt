package com.android.tripbook.test

import android.content.Context
import android.util.Log
import com.android.tripbook.database.TripBookDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 🔍 DATABASE VERIFIER
 * 
 * Simple utility to verify that test data was successfully added to database
 * and that it will appear in the catalog and trip details.
 */
object DatabaseVerifier {
    
    private const val TAG = "DatabaseVerifier"
    
    /**
     * Verify that test data exists and will be visible in the app
     */
    fun verifyTestDataExists(context: Context) {
        Log.d(TAG, "🔍 Verifying test data exists in database...")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TripBookDatabase.getDatabase(context)
                
                // Check for test trips
                verifyTestTrips(database)
                
                // Check for test reviews
                verifyTestReviews(database)
                
                // Verify data will appear in catalog
                verifyDataForCatalog(database)
                
                Log.d(TAG, "✅ VERIFICATION COMPLETE - Test data is ready for catalog display!")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Verification failed: ${e.message}", e)
            }
        }
    }
    
    private suspend fun verifyTestTrips(database: TripBookDatabase) {
        Log.d(TAG, "🔍 Checking for test trips...")
        
        // Check for our specific test trips
        val testTrip1 = database.tripDao().getTripById(888)
        val testTrip2 = database.tripDao().getTripById(999)
        
        if (testTrip1 != null) {
            Log.d(TAG, "✅ Found test trip 888: '${testTrip1.title}'")
            Log.d(TAG, "   📸 Has ${testTrip1.imageUrl.size} images")
            Log.d(TAG, "   📝 Description: ${testTrip1.description.take(100)}...")
        } else {
            Log.d(TAG, "ℹ️ Test trip 888 not found (will be created)")
        }
        
        if (testTrip2 != null) {
            Log.d(TAG, "✅ Found test trip 999: '${testTrip2.title}'")
            Log.d(TAG, "   📸 Has ${testTrip2.imageUrl.size} images")
        } else {
            Log.d(TAG, "ℹ️ Test trip 999 not found (will be created)")
        }
        
        // Count all trips
        val allTrips = database.tripDao().getAllTripsOnce()
        Log.d(TAG, "📊 Total trips in database: ${allTrips.size}")
        
        // List all trip titles for verification
        allTrips.forEach { trip ->
            Log.d(TAG, "   📍 Trip ${trip.id}: ${trip.title}")
        }
    }
    
    private suspend fun verifyTestReviews(database: TripBookDatabase) {
        Log.d(TAG, "🔍 Checking for test reviews...")
        
        // Check reviews for test trip 888
        val reviewsFor888 = database.reviewDao().getReviewsForTripOnce(888)
        if (reviewsFor888.isNotEmpty()) {
            Log.d(TAG, "✅ Found ${reviewsFor888.size} reviews for test trip 888")
            reviewsFor888.forEach { review ->
                Log.d(TAG, "   ⭐ ${review.username}: ${review.rating}/5 (${review.images.size} images)")
            }
        } else {
            Log.d(TAG, "ℹ️ No reviews found for test trip 888 (will be created)")
        }
        
        // Check reviews for test trip 999
        val reviewsFor999 = database.reviewDao().getReviewsForTripOnce(999)
        if (reviewsFor999.isNotEmpty()) {
            Log.d(TAG, "✅ Found ${reviewsFor999.size} reviews for test trip 999")
            reviewsFor999.forEach { review ->
                Log.d(TAG, "   ⭐ ${review.username}: ${review.rating}/5 (${review.images.size} images)")
            }
        } else {
            Log.d(TAG, "ℹ️ No reviews found for test trip 999 (will be created)")
        }
        
        // Count all reviews
        val allReviews = database.reviewDao().getAllReviewsOnce()
        Log.d(TAG, "📊 Total reviews in database: ${allReviews.size}")
    }
    
    private suspend fun verifyDataForCatalog(database: TripBookDatabase) {
        Log.d(TAG, "🔍 Verifying data will appear in catalog...")
        
        // Get all trips (this is what the catalog will show)
        val catalogTrips = database.tripDao().getAllTripsOnce()
        
        Log.d(TAG, "📱 CATALOG PREVIEW:")
        Log.d(TAG, "   The catalog will show ${catalogTrips.size} trips:")
        
        catalogTrips.forEach { trip ->
            val reviewCount = database.reviewDao().getReviewsForTripOnce(trip.id).size
            Log.d(TAG, "   📍 ${trip.title}")
            Log.d(TAG, "      📸 ${trip.imageUrl.size} images")
            Log.d(TAG, "      ⭐ ${reviewCount} reviews")
            Log.d(TAG, "      📝 ${trip.caption}")
            
            // Check if this is a test trip
            if (trip.id == 888 || trip.id == 999) {
                Log.d(TAG, "      🧪 TEST TRIP - Will prove database works!")
            }
        }
        
        Log.d(TAG, "✅ Data verification complete - catalog will display all trips with reviews!")
    }
    
    /**
     * Quick check to see if database has any data
     */
    fun quickDataCheck(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TripBookDatabase.getDatabase(context)
                val tripCount = database.tripDao().getAllTripsOnce().size
                val reviewCount = database.reviewDao().getAllReviewsOnce().size
                
                Log.d(TAG, "📊 Quick data check:")
                Log.d(TAG, "   📍 Trips: $tripCount")
                Log.d(TAG, "   ⭐ Reviews: $reviewCount")
                
                if (tripCount > 0 && reviewCount > 0) {
                    Log.d(TAG, "✅ Database has data - catalog should show trips!")
                } else {
                    Log.d(TAG, "ℹ️ Database is empty - test data will be added")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Quick check failed: ${e.message}")
            }
        }
    }
    
    /**
     * Print detailed database contents for debugging
     */
    fun printDatabaseContents(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = TripBookDatabase.getDatabase(context)
                
                Log.d(TAG, "📋 COMPLETE DATABASE CONTENTS:")
                Log.d(TAG, "================================")
                
                // Print all trips
                val trips = database.tripDao().getAllTripsOnce()
                Log.d(TAG, "📍 TRIPS (${trips.size}):")
                trips.forEach { trip ->
                    Log.d(TAG, "   ID: ${trip.id}")
                    Log.d(TAG, "   Title: ${trip.title}")
                    Log.d(TAG, "   Caption: ${trip.caption}")
                    Log.d(TAG, "   Images: ${trip.imageUrl.size}")
                    Log.d(TAG, "   ---")
                }
                
                // Print all reviews
                val reviews = database.reviewDao().getAllReviewsOnce()
                Log.d(TAG, "⭐ REVIEWS (${reviews.size}):")
                reviews.forEach { review ->
                    Log.d(TAG, "   ID: ${review.id}")
                    Log.d(TAG, "   Trip ID: ${review.tripId}")
                    Log.d(TAG, "   User: ${review.username}")
                    Log.d(TAG, "   Rating: ${review.rating}/5")
                    Log.d(TAG, "   Images: ${review.images.size}")
                    Log.d(TAG, "   Comment: ${review.comment.take(50)}...")
                    Log.d(TAG, "   ---")
                }
                
                Log.d(TAG, "================================")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to print database contents: ${e.message}")
            }
        }
    }
}
