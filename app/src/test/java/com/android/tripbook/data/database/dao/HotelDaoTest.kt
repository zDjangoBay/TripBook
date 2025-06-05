package com.android.tripbook.data.database.dao

import com.android.tripbook.data.database.entities.HotelEntity
import com.android.tripbook.data.models.HotelOption
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for HotelEntity and related functionality
 *
 * Tests hotel entity operations including:
 * - Domain model conversion
 * - Availability checking
 * - Amenities parsing
 * - Business logic validation
 */
class HotelDaoTest {

    // Test data
    private val testHotels = listOf(
        HotelEntity(
            id = "hotel_1",
            name = "Luxury Safari Lodge",
            rating = 5,
            roomType = "Deluxe Suite",
            pricePerNight = 250.0,
            imageUrl = "https://example.com/hotel1.jpg",
            amenities = "WiFi,Pool,Spa,Restaurant,Bar",
            description = "Luxury lodge with stunning safari views",
            location = "Serengeti",
            address = "123 Safari Road",
            phone = "+255123456789",
            email = "info@luxurylodge.com",
            isAvailable = true,
            totalRooms = 50,
            availableRooms = 25
        ),
        HotelEntity(
            id = "hotel_2",
            name = "Budget Backpacker Inn",
            rating = 3,
            roomType = "Shared Dorm",
            pricePerNight = 25.0,
            imageUrl = "https://example.com/hotel2.jpg",
            amenities = "WiFi,Kitchen,Lounge",
            description = "Affordable accommodation for backpackers",
            location = "Arusha",
            isAvailable = true,
            totalRooms = 20,
            availableRooms = 10
        ),
        HotelEntity(
            id = "hotel_3",
            name = "Mountain View Resort",
            rating = 4,
            roomType = "Standard Room",
            pricePerNight = 120.0,
            imageUrl = "https://example.com/hotel3.jpg",
            amenities = "WiFi,Pool,Gym,Restaurant",
            description = "Beautiful resort with mountain views",
            location = "Kilimanjaro",
            isAvailable = true,
            totalRooms = 30,
            availableRooms = 15
        ),
        HotelEntity(
            id = "hotel_4",
            name = "Unavailable Hotel",
            rating = 4,
            roomType = "Standard Room",
            pricePerNight = 100.0,
            imageUrl = "https://example.com/hotel4.jpg",
            amenities = "WiFi,Pool",
            description = "Currently unavailable hotel",
            location = "Dar es Salaam",
            isAvailable = false,
            totalRooms = 40,
            availableRooms = 0
        ),
        HotelEntity(
            id = "hotel_5",
            name = "Beach Paradise Hotel",
            rating = 5,
            roomType = "Ocean View Suite",
            pricePerNight = 300.0,
            imageUrl = "https://example.com/hotel5.jpg",
            amenities = "WiFi,Pool,Beach Access,Spa,Restaurant,Bar",
            description = "Luxury beachfront hotel with ocean views",
            location = "Zanzibar",
            isAvailable = true,
            totalRooms = 60,
            availableRooms = 30
        ) 
    )

    @Test
    fun hotelEntity_toDomainModel_convertsCorrectly() {
        // Given
        val hotel = testHotels[0]

        // When
        val domainModel = hotel.toDomainModel()

        // Then
        assertEquals(hotel.id, domainModel.id)
        assertEquals(hotel.name, domainModel.name)
        assertEquals(hotel.rating, domainModel.rating)
        assertEquals(hotel.roomType, domainModel.roomType)
        assertEquals(hotel.pricePerNight, domainModel.pricePerNight, 0.01)
        assertEquals(hotel.imageUrl, domainModel.imageUrl)
        assertEquals(hotel.description, domainModel.description)

        // Check amenities conversion
        val expectedAmenities = listOf("WiFi", "Pool", "Spa", "Restaurant", "Bar")
        assertEquals(expectedAmenities, domainModel.amenities)
    }

    @Test
    fun hotelEntity_hasAvailableRooms_worksCorrectly() {
        // Given
        val availableHotel = testHotels[0].copy(isAvailable = true, availableRooms = 5)
        val unavailableHotel = testHotels[1].copy(isAvailable = false, availableRooms = 5)
        val noRoomsHotel = testHotels[2].copy(isAvailable = true, availableRooms = 0)
        val nullRoomsHotel = testHotels[3].copy(isAvailable = true, availableRooms = null)

        // Then
        assertTrue(availableHotel.hasAvailableRooms())
        assertFalse(unavailableHotel.hasAvailableRooms())
        assertFalse(noRoomsHotel.hasAvailableRooms())
        assertTrue(nullRoomsHotel.hasAvailableRooms()) // null means unlimited
    }

    @Test
    fun hotelEntity_getAmenitiesList_worksCorrectly() {
        // Given
        val hotel = testHotels[0]

        // When
        val amenities = hotel.getAmenitiesList()

        // Then
        val expectedAmenities = listOf("WiFi", "Pool", "Spa", "Restaurant", "Bar")
        assertEquals(expectedAmenities, amenities)
    }

    @Test
    fun hotelEntity_fromDomainModel_convertsCorrectly() {
        // Given
        val hotel = testHotels[0]
        val domainModel = hotel.toDomainModel()

        // When
        val convertedEntity = HotelEntity.fromDomainModel(domainModel, "Test Location")

        // Then
        assertEquals(domainModel.id, convertedEntity.id)
        assertEquals(domainModel.name, convertedEntity.name)
        assertEquals(domainModel.rating, convertedEntity.rating)
        assertEquals(domainModel.roomType, convertedEntity.roomType)
        assertEquals(domainModel.pricePerNight, convertedEntity.pricePerNight, 0.01)
        assertEquals(domainModel.imageUrl, convertedEntity.imageUrl)
        assertEquals(domainModel.description, convertedEntity.description)
        assertEquals("Test Location", convertedEntity.location)
        assertEquals("WiFi,Pool,Spa,Restaurant,Bar", convertedEntity.amenities)
    }
}
