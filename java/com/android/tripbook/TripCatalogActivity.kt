package com.android.tripbook

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.Adapter.RecommendationAdapter
import com.android.tripbook.Adapter.TripsAdapter
import com.android.tripbook.ViewModel.MainviewModel
import com.android.tripbook.databinding.ActivityTripCatalogBinding

class TripCatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripCatalogBinding
    private val viewModel: MainviewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityTripCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)
       observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.upcomingTrips.observe(this){
            list->
            binding.progressBarUpcomming.visibility= View.GONE
            binding.viewUpcoming.apply {
                layoutManager=LinearLayoutManager(this@TripCatalogActivity,LinearLayoutManager.HORIZONTAL,false)
                adapter=TripsAdapter(list)
            }

        }

        viewModel.recommendedPlaces.observe(this){list->
            binding.progressBarReconmendated.visibility= View.GONE
            binding.viewReconmendated.apply {
                layoutManager=LinearLayoutManager(this@TripCatalogActivity,LinearLayoutManager.HORIZONTAL,false)
                adapter= RecommendationAdapter(list)
            }
    }}
}