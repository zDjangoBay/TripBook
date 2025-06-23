package com.android.tripbook.test

import com.android.tripbook.database.TripBookDatabase
import com.android.tripbook.database.entity.TripEntity
import com.android.tripbook.database.entity.ReviewEntity

/**
 * 🧪 SIMPLE DATABASE TEST
 * 
 * This is a minimal test that doesn't depend on Android components.
 * It directly tests Room database functionality to prove it works.
 */
object SimpleDatabaseTest {
    
    /**
     * Add test data to database
     */
    suspend fun addTestData(database: TripBookDatabase) {
        println("🧪 Adding test data to database...")
        
        // 🇨🇲 CAMEROON DATABASE TRIP - Real Cameroon destination
        val testTrip = TripEntity(
            id = 777,
            title = "Limbe Coastal Paradise",
            caption = "Experience the stunning black sand beaches of Cameroon.",
            description = """
                Limbe, formerly known as Victoria, is a coastal city in Cameroon that offers one of the most unique beach experiences in West Africa. Famous for its dramatic black volcanic sand beaches, Limbe sits at the foot of Mount Cameroon, creating a spectacular backdrop of mountain meets ocean that few destinations can match.

                The city's beaches are truly extraordinary - the black sand, formed from volcanic activity of nearby Mount Cameroon, creates a striking contrast against the blue Atlantic waters. Limbe Beach and Down Beach are popular spots for swimming, sunbathing, and enjoying fresh seafood from local vendors. The warm tropical climate makes it perfect for year-round beach activities.

                Beyond the beaches, Limbe is home to the Limbe Wildlife Centre, a sanctuary for rescued primates including chimpanzees, gorillas, and various monkey species. The Limbe Botanic Garden showcases Cameroon's incredible biodiversity with over 1,500 plant species, making it a paradise for nature lovers and researchers.

                The city also serves as a gateway to Mount Cameroon, West Africa's highest peak. Adventure enthusiasts can embark on challenging hikes to the summit, passing through diverse ecosystems from tropical rainforest to alpine vegetation. The nearby Buea offers additional mountain experiences and cooler highland climate.

                Limbe's vibrant fishing culture is evident in its bustling fish markets and traditional fishing boats dotting the coastline. Local restaurants serve excellent fresh fish, prawns, and the famous Cameroonian pepper soup that perfectly complements the coastal atmosphere.

                Database ID: 777 | Verified Cameroon destination
            """.trimIndent(),
            imageUrl = listOf(
                "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=",
                "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA=",
                "https://source.unsplash.com/800x600/?mountains"
            )
        )
        
        // Insert test trip
        database.tripDao().insertTrip(testTrip)
        println("✅ Test trip inserted: ${testTrip.title}")
        
        // 🇨🇲 CAMEROON REVIEWS - Real reviews for Limbe
        val testReviews = listOf(
            ReviewEntity(
                id = 7771,
                tripId = 777,
                username = "CameroonExplorer",
                rating = 5,
                comment = "🇨🇲 AMAZING LIMBE EXPERIENCE!\n\n" +
                        "Just returned from an incredible week in Limbe and I'm blown away! The black sand beaches are absolutely stunning - unlike anything I've seen before. The contrast between the dark volcanic sand and the blue Atlantic is breathtaking.\n\n" +
                        "Highlights:\n" +
                        "🏖️ Down Beach - perfect for swimming and relaxing\n" +
                        "🐵 Limbe Wildlife Centre - saw rescued chimpanzees up close\n" +
                        "🌺 Botanic Garden - incredible biodiversity\n" +
                        "🍤 Fresh seafood at local restaurants\n" +
                        "🏔️ Amazing views of Mount Cameroon\n\n" +
                        "The locals are incredibly friendly and the food is delicious. Highly recommend the pepper soup and grilled fish by the beach!",
                images = listOf(
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                    "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48="
                ),
                isLiked = true,
                isFlagged = false,
                likeCount = 89
            ),
            ReviewEntity(
                id = 7772,
                tripId = 777,
                username = "AfricaTraveler",
                rating = 4,
                comment = "🌊 UNIQUE BLACK SAND BEACHES\n\n" +
                        "Limbe offers something truly special with its volcanic black sand beaches. As someone who has traveled extensively across Africa, I can say this is one of the most unique coastal experiences on the continent.\n\n" +
                        "What I loved:\n" +
                        "• The dramatic landscape with Mount Cameroon backdrop\n" +
                        "• Excellent seafood - try the grilled prawns!\n" +
                        "• Wildlife Centre with rescued primates\n" +
                        "• Friendly local fishing community\n" +
                        "• Great base for Mount Cameroon hiking\n\n" +
                        "Only minor downside was some areas could use better maintenance, but the natural beauty more than makes up for it. Perfect for nature lovers and adventure seekers!",
                images = listOf(
                    "https://media.gettyimages.com/id/953837336/photo/cameroon-politics-unrest-police.jpg?s=2048x2048&w=gi&k=20&c=FB3Qus6FKDhoijiBjUfBNKEoBNmmdjaDntYX2ZxRSlA="
                ),
                isLiked = true,
                isFlagged = false,
                likeCount = 67
            ),
            ReviewEntity(
                id = 7773,
                tripId = 777,
                username = "BeachLover2024",
                rating = 5,
                comment = "🏖️ PARADISE FOUND IN CAMEROON!\n\n" +
                        "I never expected to find such an amazing beach destination in Cameroon! Limbe completely exceeded my expectations. The black volcanic sand is so unique and beautiful.\n\n" +
                        "Perfect day itinerary:\n" +
                        "🌅 Morning: Sunrise walk on Limbe Beach\n" +
                        "🐒 Mid-morning: Visit Wildlife Centre\n" +
                        "🌿 Afternoon: Explore Botanic Garden\n" +
                        "🍽️ Evening: Fresh seafood dinner by the beach\n" +
                        "🌙 Night: Local bars with live music\n\n" +
                        "The combination of beach, wildlife, and mountain views makes this a perfect destination. Already planning my return trip!",
                images = listOf(
                    "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=",
                    "https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48="
                ),
                isLiked = true,
                isFlagged = false,
                likeCount = 134
            )
        )
        
        // Insert test reviews
        testReviews.forEach { review ->
            database.reviewDao().insertReview(review)
            println("✅ Test review inserted: ${review.username}")
        }
        
        println("✅ All test data inserted successfully!")
    }
    
    /**
     * Verify test data exists
     */
    suspend fun verifyTestData(database: TripBookDatabase): Boolean {
        println("🔍 Verifying test data...")
        
        try {
            // Check trip exists
            val trip = database.tripDao().getTripById(777)
            if (trip == null) {
                println("❌ Test trip not found")
                return false
            }
            
            println("✅ Test trip found: ${trip.title}")
            println("   📸 Images: ${trip.imageUrl.size}")
            
            // Check reviews exist
            val reviews = database.reviewDao().getReviewsForTripOnce(777)
            if (reviews.isEmpty()) {
                println("❌ Test reviews not found")
                return false
            }
            
            println("✅ Test reviews found: ${reviews.size}")
            reviews.forEach { review ->
                println("   ⭐ ${review.username}: ${review.rating}/5 (${review.images.size} images)")
            }
            
            // Check total counts
            val allTrips = database.tripDao().getAllTripsOnce()
            val allReviews = database.reviewDao().getAllReviewsOnce()
            
            println("📊 Database summary:")
            println("   📍 Total trips: ${allTrips.size}")
            println("   ⭐ Total reviews: ${allReviews.size}")
            
            println("✅ Database verification PASSED!")
            return true
            
        } catch (e: Exception) {
            println("❌ Database verification FAILED: ${e.message}")
            return false
        }
    }
    
    /**
     * Run complete database test
     */
    suspend fun runCompleteTest(database: TripBookDatabase): Boolean {
        println("🧪 Starting complete database test...")
        
        return try {
            // Add test data
            addTestData(database)
            
            // Verify data
            val success = verifyTestData(database)
            
            if (success) {
                println("🎉 COMPLETE DATABASE TEST PASSED!")
                println("   Your Room database is working perfectly!")
                println("   Test trip and reviews should now appear in your catalog!")
            } else {
                println("❌ Database test failed")
            }
            
            success
            
        } catch (e: Exception) {
            println("❌ Database test error: ${e.message}")
            false
        }
    }
    
    /**
     * Print database contents for debugging
     */
    suspend fun printDatabaseContents(database: TripBookDatabase) {
        println("📋 DATABASE CONTENTS:")
        println("====================")
        
        try {
            val trips = database.tripDao().getAllTripsOnce()
            val reviews = database.reviewDao().getAllReviewsOnce()
            
            println("📍 TRIPS (${trips.size}):")
            trips.forEach { trip ->
                println("   ${trip.id}: ${trip.title}")
                println("      📸 ${trip.imageUrl.size} images")
                println("      📝 ${trip.caption}")
            }
            
            println("\n⭐ REVIEWS (${reviews.size}):")
            reviews.forEach { review ->
                println("   ${review.id}: ${review.username} (Trip ${review.tripId})")
                println("      ⭐ ${review.rating}/5")
                println("      📸 ${review.images.size} images")
                println("      💬 ${review.comment.take(50)}...")
            }
            
            println("====================")
            
        } catch (e: Exception) {
            println("❌ Error reading database: ${e.message}")
        }
    }
}
