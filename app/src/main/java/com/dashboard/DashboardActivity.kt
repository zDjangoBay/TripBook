package com.dasboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var tripRecyclerView: RecyclerView
    private lateinit var tripAdapter: TripAdapter
    private lateinit var tripList: List<Trip>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tripList = listOf(
            Trip("1", R.drawable.sample_trip1, "Yaoundé", "Douala", "9:00 AM", 15000.0, "Bus"),
            Trip("2", R.drawable.sample_trip2, "Bamenda", "Buea", "11:00 AM", 20000.0, "Car")
        )

        tripRecyclerView = findViewById(R.id.tripRecyclerView)
        tripRecyclerView.layoutManager = LinearLayoutManager(this)
        tripAdapter = TripAdapter(tripList) // ✅ Fixed: 'TripAdaptera' → 'TripAdapter'
        tripRecyclerView.adapter = tripAdapter
    }
}
