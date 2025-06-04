// Replace your entire SampleReviews.kt file with this:
package com.android.tripbook.data

import com.android.tripbook.model.Review

object SampleReviews {
    // Static sample data - these are your initial reviews
    private val sampleReviews = listOf(
        Review(
            id = 6,
            tripId = 2,
            rating = 3.0f,
            username = "Briyand",
            comment = "Absolutely breathtaking!",
            date = "2024-01-15",
            userAvatar = "https://picsum.photos/50/50?random=6",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 9,
            tripId = 2,
            rating = 3.0f,
            username = "Carole",
            comment = "Absolutely breathtaking!",
            date = "2024-01-20",
            userAvatar = "https://picsum.photos/50/50?random=9",
            images = listOf("https://th.bing.com/th/id/OIP.tyIEwOIscevzwLnKw3XuGwHaE7?w=291&h=194&c=7&r=0&o=5&dpr=1.5&pid=1.7", "https://th.bing.com/th/id/OIP.r1tWienW6NOcnZwPQeV8_AHaE8?w=266&h=180&c=7&r=0&o=5&dpr=1.5&pid=1.7")
        ),
        Review(
            id = 8,
            tripId = 2,
            rating = 3.0f,
            username = "Emmanuel",
            comment = "Absolutely breathtaking!",
            date = "2024-01-18",
            userAvatar = "https://picsum.photos/50/50?random=8",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 5,
            tripId = 2,
            rating = 1.0f,
            username = "Marie",
            comment = "Absolutely breathtaking!",
            date = "2024-01-10",
            userAvatar = "https://picsum.photos/50/50?random=5",
            images = listOf("https://media.gettyimages.com/id/803446314/fr/photo/this-photo-taken-on-june-16-2017-shows-the-city-of-bamenda-the-anglophone-capital-of-northwest.jpg?s=1024x1024&w=gi&k=20&c=x_r8SMF62ult9Xps8g4t8HQJwr6eZvgJH3HOBi8rk48=", "https://media.gettyimages.com/id/141863081/fr/photo/kribi-cameroon-africa.jpg?s=1024x1024&w=gi&k=20&c=Pf5obkANbwDIJfAnkEEGf99sv4ykjtrB9PiYHY2LJ4k=")
        ),
        Review(
            id = 3,
            tripId = 1,
            rating = 4.0f,
            username = "Brian",
            comment = "Stepping into this historic quarter is like entering a living museum where centuries of human stories continue to unfold on every corner. The architectural diversity is breathtaking Each narrow alley and hidden courtyard reveals new details that speak to the layered history of this remarkable place.\n\nThe guided tours here are exceptional, led by passionate local historians who bring the past to life with engaging storytelling and little-known anecdotes that you won't find in any guidebook. I learned fascinating details about the daily lives of past residents, the evolution of local industries, and how major historical events shaped the character of these neighborhoods.\n\nThe artisan quarter is particularly enchanting, where traditional workshops still operate using time-honored techniques. Watching a master glassblower shape molten glass into delicate sculptures or observing a weaver create intricate textiles on century-old looms provides a profound appreciation for human skill and patience. The local markets offer authentic handcrafted goods that carry the essence of this place's creative spirit, making them meaningful souvenirs that connect you to the cultural heritage long after your visit ends.",
            date = "2024-01-05",
            userAvatar = "https://picsum.photos/50/50?random=3",
            images = listOf("https://source.unsplash.com/400x300/?temple", "https://source.unsplash.com/400x300/?bali")
        ),
        Review(
            id = 4,
            tripId = 2,
            username = "CulturalBuff",
            rating = 4.0f,
            comment = "Stepping into this historic quarter is like entering a living museum where centuries of human stories continue to unfold on every corner. The architectural diversity is breathtaking - from medieval stone structures with their weathered facades telling tales of ancient craftsmanship, to elegant Victorian buildings that showcase the prosperity and artistic sensibilities of bygone eras. Each narrow alley and hidden courtyard reveals new details that speak to the layered history of this remarkable place.\n\nThe guided tours here are exceptional, led by passionate local historians who bring the past to life with engaging storytelling and little-known anecdotes that you won't find in any guidebook. I learned fascinating details about the daily lives of past residents, the evolution of local industries, and how major historical events shaped the character of these neighborhoods.\n\nThe artisan quarter is particularly enchanting, where traditional workshops still operate using time-honored techniques. Watching a master glassblower shape molten glass into delicate sculptures or observing a weaver create intricate textiles on century-old looms provides a profound appreciation for human skill and patience. The local markets offer authentic handcrafted goods that carry the essence of this place's creative spirit, making them meaningful souvenirs that connect you to the cultural heritage long after your visit ends.",
            date = "2024-01-12",
            userAvatar = "https://picsum.photos/50/50?random=4",
            images = listOf("https://picsum.photos/400/300?random=4")
        ),
        Review(
            id = 1,
            tripId = 3,
            username = "Martine",
            rating = 4.0f,
            comment = "Amazing trip!",
            date = "2024-01-08",
            userAvatar = "https://picsum.photos/50/50?random=1",
            images = listOf("https://picsum.photos/300/200?random=1",
                "https://picsum.photos/300/200?random=2"
            )
        ),
        Review(
            id = 2,
            tripId = 2,
            username = "Anthony",
            rating = 5.0f,
            comment = "Loved every moment!",
            date = "2024-01-25",
            userAvatar = "https://picsum.photos/50/50?random=2",
            images = listOf(
                "https://picsum.photos/300/200?random=3"
            )
        )
    )

    // Dynamic reviews that get added during app execution
    private val dynamicReviews = mutableListOf<Review>()

    // Counter for generating unique IDs for new reviews
    private var nextId = 100 // Start from 100 to avoid conflicts with sample data

    /**
     * Get all reviews (sample + dynamic)
     */
    fun get(): List<Review> {
        return sampleReviews + dynamicReviews
    }

    /**
     * Get reviews for a specific trip
     */
    fun getByTripId(tripId: Int): List<Review> {
        return get().filter { it.tripId == tripId }
    }

    /**
     * Add a new review dynamically during app execution
     */
    fun addReview(
        tripId: Int,
        rating: Float,
        username: String,
        comment: String,
        date: String = getCurrentDate(),
        userAvatar: String = "https://picsum.photos/50/50?random=${nextId}",
        images: List<String> = emptyList()
    ): Review {
        val newReview = Review(
            id = nextId++,
            tripId = tripId,
            rating = rating,
            username = username,
            comment = comment,
            date = date,
            userAvatar = userAvatar,
            images = images
        )
        dynamicReviews.add(newReview)
        return newReview
    }

    /**
     * Add a pre-built review object
     */
    fun addReview(review: Review): Review {
        val reviewWithId = if (review.id == 0) {
            review.copy(id = nextId++)
        } else {
            review
        }
        dynamicReviews.add(reviewWithId)
        return reviewWithId
    }

    /**
     * Get current date in YYYY-MM-DD format
     */
    private fun getCurrentDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }

    /**
     * Remove a dynamic review (sample reviews cannot be removed)
     */
    fun removeReview(reviewId: Int): Boolean {
        return dynamicReviews.removeAll { it.id == reviewId }
    }

    /**
     * Clear all dynamic reviews (keeps sample data)
     */
    fun clearDynamicReviews() {
        dynamicReviews.clear()
    }

    /**
     * Get only the sample reviews
     */
    fun getSampleReviews(): List<Review> = sampleReviews

    /**
     * Get only the dynamic reviews
     */
    fun getDynamicReviews(): List<Review> = dynamicReviews.toList()

    /**
     * Get total count of all reviews
     */
    fun getCount(): Int = sampleReviews.size + dynamicReviews.size
}