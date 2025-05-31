package com.android.tripbook

import com.android.tripbook.backend.businesslogic.summary_calculator.SummaryCalculator
import com.android.tripbook.data.providers.DummyTripDataProvider
import com.android.tripbook.data.providers.DummyHotelProvider
import com.android.tripbook.data.providers.DummyActivityProvider

import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Unit test for the SummaryCalculator class.
 */

class SummaryCalculatorTest {

    // Create an instance of the calculator to test
    private val calculator = SummaryCalculator()

    /**
     * Tests whether the total cost is correctly calculated
     * based on dummy data for a trip, transport, hotel, and activities.
     */
    @Test
    fun testCalculateTotal_withAllOptionsSelected(){
        // Get the first trip from the dummy data
        val trip = DummyTripDataProvider.getTrips().first()

        // Get the first transport option for the selected trip
        val transportation = DummyTripDataProvider.getTransportOptions(trip.id).first()

        // Get the first hotel option from the dummy data
        val hotel = DummyHotelProvider.getHotels().first()

        // Take the first two activities from the dummy data
        val activities = DummyActivityProvider.getActivities().take(2)

        // Define the number of nights to stay in the hotel
        val hotelNights = 3

        // Manually calculate the expected total cost using the same logic as the calculator
        val expected = trip.basePrice + transportation.price + (hotel.pricePerNight * hotelNights) + activities.sumOf { it.price }

        // Call the calculator's method to compute the total
        val total = calculator.totalCost(trip = trip, transportOption = transportation, hotelOption = hotel, hotelNights = hotelNights, activityOption = activities)

        // Assert that the calculated total matches the expected value, within a small margin of error
        assertEquals(expected, total, 0.01)
    }

    @Test
    fun testCalculateTotal_whenHotelOptionNotSelected(){
        // Get the first trip from the dummy data
        val trip = DummyTripDataProvider.getTrips().first()

        // Get the first transport option for the selected trip
        val transportation = DummyTripDataProvider.getTransportOptions(trip.id).first()

        // Take the first two activities from the dummy data
        val activities = DummyActivityProvider.getActivities().take(2)

        // Manually calculate the expected total cost using the same logic as the calculator (No hotel chosen)
        val expected = trip.basePrice + transportation.price + activities.sumOf { it.price }

        // Call the calculator's method to compute the total
        val total = calculator.totalCost(trip = trip, transportOption = transportation, hotelOption = null, hotelNights = 0, activityOption = activities)

        // Assert that the calculated total matches the expected value, within a small margin of error
        assertEquals(expected, total, 0.01)
    }

    @Test
    fun tesCalculateTotal_whenNoActivitiesSelected(){
        // Get the first trip from the dummy data
        val trip = DummyTripDataProvider.getTrips().first()

        // Get the first transport option for the selected trip
        val transportation = DummyTripDataProvider.getTransportOptions(trip.id).first()

        // Get the first hotel option from the dummy data
        val hotel = DummyHotelProvider.getHotels().first()

        // Define the number of nights to stay in the hotel
        val hotelNights = 2

        // Manually calculate the expected total cost using the same logic as the calculator (No Activities Selected)
        val expected = trip.basePrice + transportation.price + (hotel.pricePerNight * hotelNights)

        // Call the calculator's method to compute the total
        val total = calculator.totalCost(trip = trip, transportOption = transportation, hotelOption = hotel, hotelNights = hotelNights, activityOption = emptyList())

        // Assert that the calculated total matches the expected value, within a small margin of error
        assertEquals(expected, total, 0.01)
    }
}
