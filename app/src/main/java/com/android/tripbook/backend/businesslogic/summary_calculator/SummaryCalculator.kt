package com.android.tripbook.backend.businesslogic.summary_calculator

import com.android.tripbook.data.models.ActivityOption
import com.android.tripbook.data.models.HotelOption
import com.android.tripbook.data.models.TransportOption
import com.android.tripbook.data.models.Trip

/**
 * Calculates the total cost of a trip reservation based on selected options.
 */

class SummaryCalculator{

    /**
     * Computes the full cost of a reservation, including base trip price,
     * transport, hotel stay, and selected activities.
     *
     * @param trip The selected trip, containing the base price.
     * @param transportOption The selected mode of transport (e.g., flight, bus).
     * @param hotelOption The chosen hotel option (nullable in case the user doesn't book a hotel).
     * @param hotelNights The number of nights the hotel is booked for.
     * @param activityOption A list of selected activities for the trip.
     *
     * @return The total calculated cost as a Double.
     */

    fun totalCost(trip: Trip, transportOption: TransportOption,hotelOption: HotelOption?, hotelNights: Int, activityOption: List<ActivityOption>): Double {
        // Start with the base price of the trip
        var calculateTotal = trip.basePrice

        // Add the price of the selected transport option
        calculateTotal += transportOption.price

        // If a hotel was selected, add the cost for the number of nights
        if(hotelOption != null){
            calculateTotal += hotelOption.pricePerNight * hotelNights
        }

        // Add the total cost of all selected activities
        calculateTotal += activityOption.sumOf { it.price }

        // Return the final calculated total cost
        return calculateTotal
    }
}