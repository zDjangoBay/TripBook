package com.android.tripbook

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.Button // Added for potential button
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.Adapter.RecommendationAdapter
import com.android.tripbook.Adapter.TripsAdapter
import com.android.tripbook.ViewModel.MainviewModel
import com.android.tripbook.databinding.ActivityTripCatalogBinding
import com.android.tripbook.util.NewsletterHelper // Import the helper

class TripCatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripCatalogBinding
    private val viewModel: MainviewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTripCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()

        // --- Newsletter Button Setup (Example) ---
        // Assuming a button with id 'generateNewsletterButton' is added to activity_trip_catalog.xml
        // val generateButton: Button = findViewById(R.id.generateNewsletterButton) // Replace with actual ID
        // generateButton.setOnClickListener {
        //     triggerNewsletterGeneration()
        // }
        // If using a menu item, set up the listener in onCreateOptionsMenu/onOptionsItemSelected

        // For demonstration, let's add a temporary trigger (e.g., clicking a profile icon if available)
        // Replace this with the actual UI element trigger
        binding.root.findViewById<View>(R.id.profile)?.setOnClickListener { // Example: Trigger on profile icon click
             triggerNewsletterGeneration()
        }
        // Or add a dedicated button/menu item in the layout/menu XML and set its listener here.

    }

    private fun observeViewModel() {
        viewModel.upcomingTrips.observe(this) { list ->
            binding.progressBarUpcomming.visibility = View.GONE
            binding.viewUpcoming.apply {
                layoutManager = LinearLayoutManager(this@TripCatalogActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = TripsAdapter(list)
            }
        }

        viewModel.recommendedPlaces.observe(this) { list ->
            binding.progressBarReconmendated.visibility = View.GONE
            binding.viewReconmendated.apply {
                layoutManager = LinearLayoutManager(this@TripCatalogActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = RecommendationAdapter(list)
            }
        }
    }

    // --- Newsletter Generation Trigger Function ---
    private fun triggerNewsletterGeneration() {
        val (subject, body) = NewsletterHelper.generateNewsletterContent(this)
        NewsletterHelper.launchEmailIntent(this, subject, body)
    }

    // Add onCreateOptionsMenu and onOptionsItemSelected if using an ActionBar/Toolbar menu
    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.your_menu_file, menu) // Create a menu XML file
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_generate_newsletter_menu_item -> { // Add this ID to your menu XML
                triggerNewsletterGeneration()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    */
}
