// ui/detail/ServiceDetailFragment.kt
package com.android.tripbook.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.android.tripbook.R
import com.android.tripbook.data.repositories.MockServiceRepository

class ServiceDetailFragment : Fragment() {

    private val args: ServiceDetailFragmentArgs by navArgs()
    private val mockServiceRepository = MockServiceRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serviceId = args.serviceId
        val service = mockServiceRepository.getServiceById(serviceId)

        service?.let {
            view.findViewById<TextView>(R.id.detail_service_name_text_view).text = it.name
            view.findViewById<TextView>(R.id.detail_agency_name_text_view).text = "Agency: ${it.agency.name}"
            view.findViewById<TextView>(R.id.detail_price_text_view).text = "Price: $${it.price}"
            view.findViewById<TextView>(R.id.detail_rating_text_view).text = "Rating: ${it.rating} ★"
            view.findViewById<TextView>(R.id.detail_description_text_view).text = it.description
            // Set image: In a real app, use a library like Glide to load from URL
            view.findViewById<ImageView>(R.id.detail_service_image_view).setImageResource(R.drawable.ic_placeholder_image)

            view.findViewById<Button>(R.id.book_now_button).setOnClickListener {
                val action = ServiceDetailFragmentDirections.actionServiceDetailFragmentToBookingConfirmationFragment()
                findNavController().navigate(action)
            }
        } ?: run {
            // Handle case where service is not found (e.g., show error message)
            findNavController().popBackStack() // Go back if service not found
        }
    }
}