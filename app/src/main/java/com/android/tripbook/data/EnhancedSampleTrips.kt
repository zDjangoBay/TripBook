package com.android.tripbook.data

import android.content.Context
import android.util.Log
import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.entity.TripEntity
import com.android.tripbook.model.Trip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Enhanced SampleTrips that integrates with Room Database
 * - Preserves existing in-memory functionality for team compatibility
 * - Adds persistent storage for user-added trips
 * - Seamless integration between memory and database
 */
object EnhancedSampleTrips {
    
    private var database: TripBookDatabase? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    
    // Original in-memory trips (for team compatibility)
    private val originalTrips = mutableListOf(
        Trip(
            id = 1,
            title = "Yaounde Capital",
            caption = "Discover the heart of Cameroon.",
            description = """
                Yaoundé, the political and administrative capital of Cameroon, is a city that gracefully blends modernity with tradition. Set across lush hills, it offers a panoramic view of urban sprawl peppered with colonial-era buildings, bustling markets, and tree-lined boulevards. As you walk through the city, the rhythm of local life pulsates through every corner — from roadside vendors selling grilled fish and plantains to students debating politics in open cafés.

                The city also offers a rich cultural experience with attractions like the National Museum, the Reunification Monument, and the Mvog-Betsi Zoo. Whether you're exploring its historical sites, enjoying the diverse local cuisine, or simply soaking in the atmosphere, Yaoundé presents an authentic slice of Cameroon's soul. It's a dynamic entry point for any traveler wishing to dive deep into the country's culture.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
            )
        ),
        Trip(
            id = 2,
            title = "Buea Mountain",
            caption = "Climb the legendary Mount Cameroon.",
            description = """
                Nestled at the foot of Mount Cameroon, Buea offers an unmatched blend of natural beauty and adventure. The mountain — the highest in West Africa — looms in the background, drawing hikers, researchers, and nature lovers alike. A trek up the slopes rewards adventurers with stunning views, volcanic landscapes, and encounters with rare flora and fauna. The cool, misty air is a refreshing break from the tropical lowlands.

                Beyond its mountain appeal, Buea is a historical town with colonial German architecture, vibrant university life, and friendly locals. It's the perfect base for exploring Cameroon's diverse ecosystems, from dense rainforests to lava flows. Whether you're here to conquer the peak or relax in its scenic ambiance, Buea is an unforgettable destination for explorers.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                "https://source.unsplash.com/800x600/?mountains"
            )
        ),
        Trip(
            id = 3,
            title = "Kribi",
            caption = "Soak in paradise by the sea.",
            description = """
                Kribi is Cameroon's coastal gem, renowned for its golden beaches, crystal-clear waters, and laid-back charm. The town is a popular retreat for locals and tourists seeking sun, sea, and serenity. Among its highlights is the Lobe Waterfall — a rare natural wonder where the river plunges directly into the Atlantic Ocean, creating a surreal backdrop for boat rides and photo opportunities.

                Kribi is not just about scenic beauty; it's a place where culture and relaxation merge seamlessly. Fresh seafood is a local specialty, with grilled shrimp and fish served fresh by the beach. The friendly communities and tranquil atmosphere make it a perfect spot for romantic getaways, solo retreats, or family vacations. Kribi invites you to unplug and reconnect with nature in the most soothing way.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k="
            )
        ),
        Trip(
            id = 4,
            title = "Bamenda Town",
            caption = "Experience the soul of the highlands.",
            description = """
                Set amidst rolling hills and cool highland air, Bamenda offers a unique blend of tradition and progress. Known for its rich cultural heritage, the town is home to vibrant festivals, traditional dances, and bustling markets filled with colorful crafts and textiles. As the unofficial capital of the English-speaking northwest, Bamenda has a distinct identity that sets it apart from other Cameroonian cities.

                The landscape around Bamenda is equally captivating. Terraced hills, hidden waterfalls, and scenic drives through the countryside make it a haven for hikers and photographers. Despite recent social challenges, the spirit of the city remains welcoming and resilient. Visiting Bamenda provides not only an escape into nature but also a deep cultural immersion that leaves a lasting impression.
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                "https://source.unsplash.com/800x600/?mountains"
            )
        )
    )
    
    // User-added trips (in-memory for immediate access)
    private val userTrips = mutableListOf<Trip>()
    
    /**
     * Initialize database connection
     */
    fun initialize(context: Context) {
        database = TripBookDatabase.getDatabase(context)
        Log.d("EnhancedSampleTrips", "🚀 Database initialized")
    }
    
    /**
     * Get all trips (original + user-added + database)
     * Compatible with existing SampleTrips.get() calls
     */
    fun get(): List<Trip> {
        return originalTrips + userTrips
    }
    
    /**
     * Add trip with Room database persistence
     * Compatible with existing SampleTrips.addTrip() calls
     */
    fun addTrip(trip: Trip) {
        // Add to memory immediately (for UI responsiveness)
        userTrips.add(trip)
        Log.d("EnhancedSampleTrips", "➕ Added trip to memory: ${trip.title}")
        
        // Persist to database asynchronously
        database?.let { db ->
            scope.launch {
                try {
                    val entity = TripEntity(
                        id = trip.id,
                        title = trip.title,
                        description = trip.description,
                        imageUrl = trip.imageUrl,
                        caption = trip.caption
                    )
                    db.tripDao().insertTrip(entity)
                    Log.d("EnhancedSampleTrips", "💾 Persisted trip to database: ${trip.title}")
                } catch (e: Exception) {
                    Log.e("EnhancedSampleTrips", "❌ Failed to persist trip: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Generate unique ID
     * Compatible with existing SampleTrips.generateId() calls
     */
    fun generateId(): Int {
        val allTrips = get()
        return (allTrips.maxOfOrNull { it.id } ?: 0) + 1
    }
    
    /**
     * Load user trips from database on app start
     */
    suspend fun loadUserTripsFromDatabase() {
        database?.let { db ->
            try {
                val entities = withContext(Dispatchers.IO) {
                    db.tripDao().getAllTripsOnce()
                }
                
                val dbTrips = entities.map { entity ->
                    Trip(
                        id = entity.id,
                        title = entity.title,
                        description = entity.description,
                        imageUrl = entity.imageUrl,
                        caption = entity.caption
                    )
                }
                
                // Add database trips to memory (avoid duplicates)
                dbTrips.forEach { dbTrip ->
                    if (userTrips.none { it.id == dbTrip.id }) {
                        userTrips.add(dbTrip)
                    }
                }
                
                Log.d("EnhancedSampleTrips", "📥 Loaded ${dbTrips.size} trips from database")
            } catch (e: Exception) {
                Log.e("EnhancedSampleTrips", "❌ Failed to load trips from database: ${e.message}")
            }
        }
    }
    
    /**
     * Get reactive Flow of all trips
     */
    fun getTripsFlow(): Flow<List<Trip>>? {
        return database?.tripDao()?.getAllTrips()?.map { entities ->
            val dbTrips = entities.map { entity ->
                Trip(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    imageUrl = entity.imageUrl,
                    caption = entity.caption
                )
            }
            originalTrips + dbTrips
        }
    }
}
