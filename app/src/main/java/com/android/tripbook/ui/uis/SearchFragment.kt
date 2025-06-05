package com.android.tripbook.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tripbook.R
import com.android.tripbook.viewmodel.TripSearchViewModel
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.coroutines.flow.collect

class SearchFragment : Fragment() {

    private val viewModel: TripSearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view)

        searchButton.setOnClickListener {
            val destination = destinationInput.text.toString()
            val budgetMin = budgetMinInput.text.toString().toDoubleOrNull()
            val budgetMax = budgetMaxInput.text.toString().toDoubleOrNull()

            viewModel.searchTrips(
                destination = destination,
                budgetRange = if (budgetMin != null && budgetMax != null) Pair(budgetMin, budgetMax) else null
            )
        }

        lifecycleScope.launchWhenStarted {
            viewModel.filteredTrips.collect { trips ->
                // Update the RecyclerView with the filtered trips
                updateRecyclerView(trips)
            }
        }
    }

    private fun updateRecyclerView(trips: List<Trip>) {
        // Set up your RecyclerView adapter here to display the filtered trips
    }
}

