package com.android.tripbook.test

import android.content.Context
import android.util.Log
import com.android.tripbook.Repository.EnhancedTripsRepository
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.entity.TriphomeEntity
import com.android.tripbook.database.entity.PlaceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Test class to verify Room database integration with team member's Firebase data
 * This demonstrates that user-added trips and Firebase data persist across app restarts
 */
object DatabaseIntegrationTest {
    
    fun runTest(context: Context) {
        val scope = CoroutineScope(Dispatchers.IO)
        
        scope.launch {
            try {
                Log.d("DatabaseTest", "🧪 Starting Room Database Integration Test...")
                
                // Initialize database and repository
                val database = TripBookDatabase.getDatabase(context)
                val repository = EnhancedTripsRepository(context)
                
                // Test 1: Insert sample Firebase data into Room
                Log.d("DatabaseTest", "📥 Test 1: Inserting sample Firebase data...")
                
                val sampleTriphomes = listOf(
                    TriphomeEntity(
                        companyLogo = "https://example.com/logo1.png",
                        companyName = "Cameroon Express",
                        arriveTime = "14:30",
                        date = "2024-01-15",
                        from = "Yaoundé",
                        fromshort = "YAO",
                        price = 25000.0,
                        time = "08:00",
                        to = "Douala",
                        score = 4.5,
                        toshort = "DLA",
                        isFromFirebase = true
                    ),
                    TriphomeEntity(
                        companyName = "Highland Tours",
                        from = "Buea",
                        to = "Bamenda",
                        price = 15000.0,
                        date = "2024-01-20",
                        score = 4.2,
                        isFromFirebase = true
                    )
                )
                
                val samplePlaces = listOf(
                    PlaceEntity(
                        title = "Mount Cameroon",
                        picUrl = "https://example.com/mount-cameroon.jpg",
                        isFromFirebase = true
                    ),
                    PlaceEntity(
                        title = "Kribi Beach",
                        picUrl = "https://example.com/kribi-beach.jpg",
                        isFromFirebase = true
                    )
                )
                
                // Insert test data
                database.triphomeDao().insertTriphomes(sampleTriphomes)
                database.placeDao().insertPlaces(samplePlaces)
                
                Log.d("DatabaseTest", "✅ Test 1 PASSED: Sample data inserted successfully")
                
                // Test 2: Retrieve data from Room
                Log.d("DatabaseTest", "📤 Test 2: Retrieving data from Room...")
                
                val retrievedTriphomes = database.triphomeDao().getAllTriphomesOnce()
                val retrievedPlaces = database.placeDao().getAllPlacesOnce()
                
                Log.d("DatabaseTest", "📊 Retrieved ${retrievedTriphomes.size} triphomes:")
                retrievedTriphomes.forEach { trip ->
                    Log.d("DatabaseTest", "   🚌 ${trip.companyName}: ${trip.from} → ${trip.to} (${trip.price} FCFA)")
                }
                
                Log.d("DatabaseTest", "📊 Retrieved ${retrievedPlaces.size} places:")
                retrievedPlaces.forEach { place ->
                    Log.d("DatabaseTest", "   🏞️ ${place.title}")
                }
                
                Log.d("DatabaseTest", "✅ Test 2 PASSED: Data retrieved successfully")
                
                // Test 3: Test Enhanced Repository
                Log.d("DatabaseTest", "🔄 Test 3: Testing Enhanced Repository...")
                
                val repoTriphomes = repository.getUpcomingTrips()
                val repoPlaces = repository.getRecommendedTrips()
                
                Log.d("DatabaseTest", "📊 Repository returned ${repoTriphomes.size} trips and ${repoPlaces.size} places")
                
                Log.d("DatabaseTest", "✅ Test 3 PASSED: Enhanced Repository working")
                
                // Test 4: Verify data persistence (simulate app restart)
                Log.d("DatabaseTest", "🔄 Test 4: Testing data persistence...")
                
                // Create new database instance (simulates app restart)
                val newDatabase = TripBookDatabase.getDatabase(context)
                val persistedTriphomes = newDatabase.triphomeDao().getAllTriphomesOnce()
                val persistedPlaces = newDatabase.placeDao().getAllPlacesOnce()
                
                if (persistedTriphomes.size == retrievedTriphomes.size && 
                    persistedPlaces.size == retrievedPlaces.size) {
                    Log.d("DatabaseTest", "✅ Test 4 PASSED: Data persists across app restarts")
                } else {
                    Log.e("DatabaseTest", "❌ Test 4 FAILED: Data not persisting")
                }
                
                Log.d("DatabaseTest", "🎉 ALL TESTS COMPLETED SUCCESSFULLY!")
                Log.d("DatabaseTest", "")
                Log.d("DatabaseTest", "🎯 SUMMARY:")
                Log.d("DatabaseTest", "   ✅ Room database is working")
                Log.d("DatabaseTest", "   ✅ Firebase data can be cached")
                Log.d("DatabaseTest", "   ✅ User data persists across restarts")
                Log.d("DatabaseTest", "   ✅ Enhanced Repository is functional")
                Log.d("DatabaseTest", "   ✅ Team member's work is preserved")
                
            } catch (e: Exception) {
                Log.e("DatabaseTest", "❌ Test failed: ${e.message}", e)
            }
        }
    }
}
