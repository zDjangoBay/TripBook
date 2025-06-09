// ui/listing/ServiceListingFragment.kt
package com.android.tripbook.ui.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.data.models.TravelService
import com.android.tripbook.data.repositories.MockServiceRepository

class ServiceListingFragment : Fragment() {

    private val args: ServiceListingFragmentArgs by navArgs()
    private lateinit var serviceAdapter: ServiceAdapter
    private val mockServiceRepository = MockServiceRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.service_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        serviceAdapter = ServiceAdapter { service ->
            // Handle service item click
            val action = ServiceListingFragmentDirections.actionServiceListingFragmentToServiceDetailFragment(service.id)
            findNavController().navigate(action)
        }
        recyclerView.adapter = serviceAdapter

        val initialQuery = args.searchQuery
        val services = mockServiceRepository.getMockServices()
        serviceAdapter.submitList(services) // Display all mock services initially

        // Set up filter/sort buttons (their actual logic would be more complex)
        val filterButton = view.findViewById<Button>(R.id.filter_button)
        val sortButton = view.findViewById<Button>(R.id.sort_button)
        val filterStatusTextView = view.findViewById<TextView>(R.id.filter_status_text_view)

        filterButton.setOnClickListener {
            // Implement a dialog or another fragment for filter options
            filterStatusTextView.text = "Filters applied: Price Range, 4+ Stars"
        }

        sortButton.setOnClickListener {
            // Implement a dialog for sort options
            filterStatusTextView.text = "Sorted by: Price (Low to High)"
        }
    }
}