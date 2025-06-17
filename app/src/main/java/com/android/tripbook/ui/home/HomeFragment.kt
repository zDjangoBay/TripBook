package com.android.tripbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import android.util.Log

// data models
import com.android.tripbook.data.models.Hotel
import com.android.tripbook.data.models.Tour
import com.android.tripbook.data.models.CarRental

//  adapters
import com.android.tripbook.adapters.HotelsAdapter
import com.android.tripbook.adapters.ToursAdapter
import com.android.tripbook.adapters.CarRentalsAdapter

class HomeFragment : Fragment() {

    private val tag = "HomeFragmentNavDebug"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.title = getString(R.string.discover_your_next_adventure)

        val searchInputEditText = view.findViewById<EditText>(R.id.editText)
        searchInputEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                (event != null && event.keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.action == android.view.KeyEvent.ACTION_DOWN)) {
                val query = searchInputEditText.text.toString()
                Log.d(tag, "Search query entered: $query")


                val action = HomeFragmentDirections.actionHomeFragmentToServiceListingFragment(query)
                findNavController().navigate(action)

                true
            } else {
                false
            }
        }

        // --- Setup for Hotels RecyclerView ---
        val hotelsRecyclerView: RecyclerView = view.findViewById(R.id.hotels_recycler_view)
        hotelsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dummyHotels = listOf(
            Hotel(
                "h1",
                "Grand Hyatt",
                "Paris, France",
                "$250/night",
                R.drawable.hotel_placeholder,
                rating = 4.5,
                availableDate = "2025-07-01",
                description = "A luxurious hotel in the heart of Paris with stunning views."
            ),
            Hotel(
                "h2",
                "City View Inn",
                "Rome, Italy",
                "$180/night",
                R.drawable.hotel_placeholder,
                rating = 4.2,
                availableDate = "2025-07-15",
                description = "Comfortable and affordable accommodation in Rome."
            ),
            Hotel(
                "h3",
                "Beach Paradise",
                "Bali, Indonesia",
                "$300/night",
                R.drawable.hotel_placeholder,
                rating = 4.8,
                availableDate = "2025-08-01",
                description = "An idyllic beachfront resort in Bali for relaxation."
            ),
            Hotel(
                "h4",
                "Mountain Retreat",
                "Swiss Alps",
                "$400/night",
                R.drawable.hotel_placeholder,
                rating = 4.9,
                availableDate = "2025-08-10",
                description = "Experience the serene beauty of the Swiss Alps in this charming lodge."
            )
        )

        val hotelsAdapter = HotelsAdapter(dummyHotels) { hotel ->
            Log.d(tag, "HomeFragment received click for hotel: ${hotel.name} (ID: ${hotel.id})") 

            val action = HomeFragmentDirections.actionHomeFragmentToHotelDetailFragment(hotel.id)
            findNavController().navigate(action) 
        }
        hotelsRecyclerView.adapter = hotelsAdapter

        // --- Setup for Tours RecyclerView ---
        val toursRecyclerView: RecyclerView = view.findViewById(R.id.tours_recycler_view)
        toursRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dummyTours = listOf(
            Tour("t1", "Eiffel Tower Tour", "2 hours", "$75", R.drawable.tour_placeholder),
            Tour("t2", "Colosseum Experience", "3 hours", "$60", R.drawable.tour_placeholder),
            Tour("t3", "Grand Canyon Hike", "8 hours", "$120", R.drawable.tour_placeholder)
        )
        val toursAdapter = ToursAdapter(dummyTours)
        toursRecyclerView.adapter = toursAdapter

        // --- Setup for Car Rentals RecyclerView ---
        val carRentalsRecyclerView: RecyclerView = view.findViewById(R.id.car_rentals_recycler_view)
        carRentalsRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dummyCarRentals = listOf(
            CarRental("c1", "Economy Sedan", "$40/day", R.drawable.car_placeholder),
            CarRental("c2", "SUV Rental", "$70/day", R.drawable.car_placeholder),
            CarRental("c3", "Luxury Convertible", "$150/day", R.drawable.car_placeholder)
        )
        val carRentalsAdapter = CarRentalsAdapter(dummyCarRentals)
        carRentalsRecyclerView.adapter = carRentalsAdapter
    }
}