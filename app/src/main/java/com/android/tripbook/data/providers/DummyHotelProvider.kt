package com.android.tripbook.data.providers

import com.android.tripbook.data.models.HotelOption

/**
 * Provides dummy hotel data for the application
 */
object DummyHotelProvider {
    
    fun getHotels(): List<HotelOption> = listOf(
        HotelOption(
            id = "hotel_1",
            name = "Safari Lodge Deluxe",
            rating = 5,
            roomType = "Luxury Suite",
            pricePerNight = 250.0,
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945",
            amenities = listOf("Pool", "Spa", "Restaurant", "WiFi", "Game Drives"),
            description = "Luxury lodge with stunning views of the savanna"
        ),
        HotelOption(
            id = "hotel_2",
            name = "Mara River Camp",
            rating = 4,
            roomType = "Standard Tent",
            pricePerNight = 180.0,
            imageUrl = "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4",
            amenities = listOf("Restaurant", "WiFi", "Campfire", "Game Drives"),
            description = "Authentic safari experience in comfortable tents"
        ),
        HotelOption(
            id = "hotel_3",
            name = "Acacia Tree Hotel",
            rating = 4,
            roomType = "Deluxe Room",
            pricePerNight = 200.0,
            imageUrl = "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa",
            amenities = listOf("Pool", "Restaurant", "WiFi", "Gym", "Spa"),
            description = "Modern hotel with traditional African decor"
        ),
        HotelOption(
            id = "hotel_4",
            name = "Budget Safari Inn",
            rating = 3,
            roomType = "Standard Room",
            pricePerNight = 120.0,
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96",
            amenities = listOf("Restaurant", "WiFi", "Parking"),
            description = "Comfortable and affordable accommodation"
        ),
        HotelOption(
            id = "hotel_5",
            name = "Serengeti View Resort",
            rating = 5,
            roomType = "Presidential Suite",
            pricePerNight = 400.0,
            imageUrl = "https://images.unsplash.com/photo-1571896349842-33c89424de2d",
            amenities = listOf("Pool", "Spa", "Restaurant", "WiFi", "Butler Service", "Private Game Drives"),
            description = "Ultimate luxury with panoramic views of the Serengeti"
        ),
        HotelOption(
            id = "hotel_6",
            name = "Riverside Lodge",
            rating = 4,
            roomType = "River View Room",
            pricePerNight = 220.0,
            imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b",
            amenities = listOf("Pool", "Restaurant", "WiFi", "Boat Rides", "Fishing"),
            description = "Peaceful lodge by the river with excellent wildlife viewing"
        )
    )
    
    fun getHotelById(id: String): HotelOption? = getHotels().find { it.id == id }
    
    fun getHotelsByRating(minRating: Int): List<HotelOption> {
        return getHotels().filter { it.rating >= minRating }
    }
    
    fun getHotelsByPriceRange(minPrice: Double, maxPrice: Double): List<HotelOption> {
        return getHotels().filter { it.pricePerNight in minPrice..maxPrice }
    }
}
