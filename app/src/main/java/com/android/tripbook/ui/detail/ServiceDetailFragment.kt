package com.android.tripbook.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.android.tripbook.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiceDetailFragment : Fragment() {

    private val args: ServiceDetailFragmentArgs by navArgs()
    private val viewModel: ServiceDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_service_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serviceId = args.serviceId // Get the serviceId from arguments

        val serviceNameTextView: TextView = view.findViewById(R.id.service_detail_name)
        val serviceTypeTextView: TextView = view.findViewById(R.id.service_detail_type)
        val serviceDescriptionTextView: TextView = view.findViewById(R.id.service_detail_description)
        val servicePriceTextView: TextView = view.findViewById(R.id.service_detail_price)
        val serviceImageView: ImageView = view.findViewById(R.id.service_detail_image)
        val serviceRatingTextView: TextView = view.findViewById(R.id.service_detail_rating)
        val serviceAgencyTextView: TextView = view.findViewById(R.id.service_detail_agency)
        val bookNowButton: Button = view.findViewById(R.id.book_now_button)

        viewModel.service.observe(viewLifecycleOwner) { service ->
            service?.let {
                serviceNameTextView.text = it.name
                serviceTypeTextView.text = getString(R.string.service_type_label, it.type)
                serviceDescriptionTextView.text = it.description
                servicePriceTextView.text = it.price
                serviceImageView.load(it.imageUrl) {
                    placeholder(R.drawable.service_placeholder)
                    error(R.drawable.ic_error_image)
                    crossfade(true)
                }
                serviceRatingTextView.text = getString(R.string.service_rating_label, it.rating)
                serviceAgencyTextView.text = getString(R.string.service_agency_label, it.agency)

                bookNowButton.setOnClickListener {
                    val action = ServiceDetailFragmentDirections.actionServiceDetailFragmentToBookingConfirmationFragment(serviceId = it.id.toString())
                    findNavController().navigate(action)
                }
            } ?: run {
                serviceNameTextView.text = getString(R.string.service_not_found)
            }
        }

        // Trigger loading service details
        viewModel.loadServiceDetails(serviceId)
    }
}