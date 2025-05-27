package com.android.tripbook.data

import com.android.tripbook.model.Trip

object TripRepository {
    fun getSampleTrips(): List<Trip> = listOf(
        Trip(
            id = "1",
            title = "Explore Yaounde",
            location = "Yaounde",
            rating = 4.5,
            price = 50000.0,
            imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=400&q=80"
        ),
        Trip(
            id = "2",
            title = "Beach in Douala",
            location = "Douala",
            rating = 4.0,
            price = 75000.0,
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=400&q=80"
        ),
        Trip(
            id = "3",
            title = "Garoua Safari",
            location = "Garoua",
            rating = 3.5,
            price = 100000.0,
            imageUrl = "https://images.unsplash.com/photo-1549887538-7a3a51c44203?auto=format&fit=crop&w=400&q=80"
        ),
        // Add more trips as needed...
    )
}
