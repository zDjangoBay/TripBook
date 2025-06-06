package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.adapters.TripAdapter
import com.android.tripbook.data.Trip
import com.android.tripbook.data.TripDatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class TripCalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var recyclerViewTrips: RecyclerView
    private lateinit var spinnerFilter: Spinner
    private lateinit var tvNoTrips: TextView
    private lateinit var fabAddTrip: FloatingActionButton

    private lateinit var tripAdapter: TripAdapter
    private lateinit var dbHelper: TripDatabaseHelper

    private var selectedDate = Date()
    private var allTripsForDate = listOf<Trip>()

    companion object {
        private const val ADD_TRIP_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_calendar)

        initViews()
        setupDatabase()
        setupRecyclerView()
        setupCalendar()
        setupFilter()
        setupFab()
        insertSampleData()
    }

    private fun initViews() {
        calendarView = findViewById(R.id.calendar_view)
        recyclerViewTrips = findViewById(R.id.recycler_view_trips)
        spinnerFilter = findViewById(R.id.spinner_filter)
        tvNoTrips = findViewById(R.id.tv_no_trips)
        fabAddTrip = findViewById(R.id.fab_add_trip)
    }

    private fun setupDatabase() {
        dbHelper = TripDatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        tripAdapter = TripAdapter()
        recyclerViewTrips.adapter = tripAdapter
        recyclerViewTrips.layoutManager = LinearLayoutManager(this)
    }

    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
            loadTripsForDate()
        }

        // Load trips for today initially
        loadTripsForDate()
    }

    private fun setupFilter() {
        val filterOptions =
            arrayOf("All", "Adventure", "Leisure", "Business", "1 day", "3 days", "7 days")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                applyFilter(filterOptions[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupFab() {
        fabAddTrip.setOnClickListener {
            val intent = Intent(this, AddTripActivity::class.java)
            // Pass the selected date to pre-fill the start date
            intent.putExtra("selected_date", selectedDate.time)
            startActivityForResult(intent, ADD_TRIP_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_TRIP_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the trip list when returning from AddTripActivity
            loadTripsForDate()
            Toast.makeText(this, "Trip added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTripsForDate() {
        allTripsForDate = dbHelper.getTripsByDate(selectedDate)
        updateTripList(allTripsForDate)
    }

    private fun applyFilter(filter: String) {
        val filteredTrips = when (filter) {
            "All" -> allTripsForDate
            "Adventure", "Leisure", "Business" -> allTripsForDate.filter { it.type == filter }
            "1 day" -> allTripsForDate.filter { it.duration == 1 }
            "3 days" -> allTripsForDate.filter { it.duration == 3 }
            "7 days" -> allTripsForDate.filter { it.duration == 7 }
            else -> allTripsForDate
        }
        updateTripList(filteredTrips)
    }

    private fun updateTripList(trips: List<Trip>) {
        if (trips.isEmpty()) {
            recyclerViewTrips.visibility = View.GONE
            tvNoTrips.visibility = View.VISIBLE
        } else {
            recyclerViewTrips.visibility = View.VISIBLE
            tvNoTrips.visibility = View.GONE
            tripAdapter.updateTrips(trips)
        }
    }

    private fun insertSampleData() {
        // Check if we already have data
        if (dbHelper.getAllTrips().isNotEmpty()) {
            return
        }

        // Insert sample trips
        val calendar = Calendar.getInstance()

        // Trip 1: Today
        val trip1 = Trip(
            title = "Safari Adventure",
            destination = "Serengeti, Tanzania",
            startDate = Date(),
            endDate = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L),
            duration = 7,
            type = "Adventure",
            description = "Amazing wildlife safari experience",
            agency = "African Safari Tours"
        )

        // Trip 2: Tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val trip2 = Trip(
            title = "Beach Relaxation",
            destination = "Zanzibar, Tanzania",
            startDate = calendar.time,
            endDate = Date(calendar.timeInMillis + 3 * 24 * 60 * 60 * 1000L),
            duration = 3,
            type = "Leisure",
            description = "Peaceful beach getaway",
            agency = "Island Paradise Tours"
        )

        // Trip 3: Next week
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val trip3 = Trip(
            title = "Conference Trip",
            destination = "Lagos, Nigeria",
            startDate = calendar.time,
            endDate = Date(calendar.timeInMillis + 1 * 24 * 60 * 60 * 1000L),
            duration = 1,
            type = "Business",
            description = "Tech conference attendance",
            agency = null
        )

        dbHelper.insertTrip(trip1)
        dbHelper.insertTrip(trip2)
        dbHelper.insertTrip(trip3)
    }
}
