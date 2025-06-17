package com.android.tripbook.ui.servicelisting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.adapters.ServiceAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceListingFragment : Fragment() {

    // Using Safe Args to retrieve the search query
    private val args: ServiceListingFragmentArgs by navArgs()
    // Injecting the ViewModel using Hilt
    private val viewModel: ServiceListingViewModel by viewModels()

    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var noResultsTextView: TextView
    private lateinit var searchQueryTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        servicesRecyclerView = view.findViewById(R.id.service_list_recycler_view)
        noResultsTextView = view.findViewById(R.id.no_results_text_view)
        searchQueryTextView = view.findViewById(R.id.search_query_text_view)

        // Set up RecyclerView
        servicesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Get the search query from arguments
        val query = args.query

        // Display the search query (or "All Services" if empty)
        searchQueryTextView.text = if (!query.isNullOrBlank()) {
            getString(R.string.search_results_for_query, query)
        } else {
            getString(R.string.all_services_title) // Define this string in strings.xml
        }

        // Observe services from the ViewModel
        viewModel.services.observe(viewLifecycleOwner) { services ->
            if (services.isNullOrEmpty()) {
                noResultsTextView.visibility = View.VISIBLE
                servicesRecyclerView.visibility = View.GONE
            } else {
                noResultsTextView.visibility = View.GONE
                servicesRecyclerView.visibility = View.VISIBLE
                val adapter = ServiceAdapter(services) { service ->
                    // Handle service item click: Navigate to ServiceDetailFragment
                    val action = ServiceListingFragmentDirections.actionServiceListingFragmentToServiceDetailFragment(service.id)
                    findNavController().navigate(action)
                }
                servicesRecyclerView.adapter = adapter
            }
        }

        // Observe error messages from the ViewModel
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                // Display error message (e.g., with a Toast)
                // Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                println("ServiceListingFragment ERROR: $errorMessage") // For debugging
            }
        }

        // The ViewModel's init block already loads services based on args.query
        // If you need to re-fetch based on a different query, call viewModel.loadServices(newQuery)
    }
}