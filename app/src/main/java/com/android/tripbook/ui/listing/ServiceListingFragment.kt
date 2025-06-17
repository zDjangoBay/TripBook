package com.android.tripbook.ui.listing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
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
import com.android.tripbook.ui.listing.SortOrder

@AndroidEntryPoint
class ServiceListingFragment : Fragment() {

    private val args: ServiceListingFragmentArgs by navArgs()
    private val viewModel: ServiceListingViewModel by viewModels()

    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var noResultsTextView: TextView
    private lateinit var searchQueryTextView: TextView
    private lateinit var filterButton: Button
    private lateinit var sortButton: Button
    private lateinit var filterStatusTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view references
        servicesRecyclerView = view.findViewById(R.id.service_list_recycler_view)
        noResultsTextView = view.findViewById(R.id.no_results_text_view)
        searchQueryTextView = view.findViewById(R.id.search_query_text_view)
        filterButton = view.findViewById(R.id.filter_button)
        sortButton = view.findViewById(R.id.sort_button)
        filterStatusTextView = view.findViewById(R.id.filter_status_text_view)
        // Set up RecyclerView
        servicesRecyclerView.layoutManager = LinearLayoutManager(context)

        // Get the search query from arguments
        val query = args.searchQuery

        searchQueryTextView.text = if (!query.isNullOrBlank()) {
            getString(R.string.search_results_for_query, query)
        } else {
            getString(R.string.all_services_title)
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
                    val action = ServiceListingFragmentDirections.actionServiceListingFragmentToServiceDetailFragment(serviceId = service.id)
                    findNavController().navigate(action)
                }
                servicesRecyclerView.adapter = adapter
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {

                println("ServiceListingFragment ERROR: $errorMessage") 
            }
        }

        // Setup Sort Button
        sortButton.setOnClickListener {
            showSortMenu(it)
        }

        // Setup Filter Button
        filterButton.setOnClickListener {

            println("Filter button clicked!")
        }

        filterStatusTextView.text = getString(R.string.filter_status_none)
        filterStatusTextView.visibility = View.VISIBLE

    }

    private fun showSortMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.menu_sort_options, popupMenu.menu) 

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.sort_price_asc -> {
                    viewModel.sortServices(SortOrder.PRICE_ASC)
                    filterStatusTextView.text = getString(R.string.sort_by_price_asc)
                    true
                }
                R.id.sort_price_desc -> {
                    viewModel.sortServices(SortOrder.PRICE_DESC)
                    filterStatusTextView.text = getString(R.string.sort_by_price_desc)
                    true
                }
                R.id.sort_rating_desc -> {
                    viewModel.sortServices(SortOrder.RATING_DESC)
                    filterStatusTextView.text = getString(R.string.sort_by_rating_desc)
                    true
                }
                R.id.sort_none -> {
                    viewModel.sortServices(SortOrder.NONE)
                    filterStatusTextView.text = getString(R.string.filter_status_none)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}