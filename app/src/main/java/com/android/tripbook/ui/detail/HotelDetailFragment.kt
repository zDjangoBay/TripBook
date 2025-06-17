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
import coil.load
import com.android.tripbook.R
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import com.android.tripbook.data.models.Hotel

@AndroidEntryPoint
class HotelDetailFragment : Fragment() {

    private val args: HotelDetailFragmentArgs by navArgs()
    private val viewModel: HotelDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hotel_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hotelId = args.hotelId

        val locationNameTextView: TextView = view.findViewById(R.id.detail_location_name)
        val ratingTextView: TextView = view.findViewById(R.id.detail_rating_text)
        val availableDateTextView: TextView = view.findViewById(R.id.detail_available_date_text)
        val priceRangeTextView: TextView = view.findViewById(R.id.detail_price_range_text)
        val aboutDescriptionTextView: TextView = view.findViewById(R.id.detail_about_description)
        val mainImageView: ImageView = view.findViewById(R.id.detail_main_image)
        val bookNowButton: Button = view.findViewById(R.id.detail_book_now_button)

        view.findViewById<ImageView>(R.id.detail_back_button).setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.hotel.observe(viewLifecycleOwner) { hotelData ->
            hotelData?.let { hotel ->
                locationNameTextView.text = hotel.location
                ratingTextView.text = String.format("%.1f", hotel.rating)
                availableDateTextView.text = hotel.availableDate
                priceRangeTextView.text = hotel.priceRange
                aboutDescriptionTextView.text = hotel.description

                mainImageView.load(hotel.imageUrl) {
                    placeholder(R.drawable.hotel_placeholder)
                    error(R.drawable.ic_error_image)
                    crossfade(true)
                }
                mainImageView.contentDescription = getString(R.string.detail_main_image_description)

                view.findViewById<ImageView>(R.id.detail_gallery_image_1).load(R.drawable.img1)
                view.findViewById<ImageView>(R.id.detail_gallery_image_2).load(R.drawable.img2)
                view.findViewById<ImageView>(R.id.detail_gallery_image_3).load(R.drawable.img3)
                view.findViewById<ImageView>(R.id.detail_gallery_image_1).contentDescription = getString(R.string.gallery_image_description)
                view.findViewById<ImageView>(R.id.detail_gallery_image_2).contentDescription = getString(R.string.gallery_image_description)
                view.findViewById<ImageView>(R.id.detail_gallery_image_3).contentDescription = getString(R.string.gallery_image_description)

                bookNowButton.setOnClickListener {
                    val action = HotelDetailFragmentDirections.actionHotelDetailFragmentToBookingConfirmationFragment(serviceId = hotel.id)
                    findNavController().navigate(action)
                }
            } ?: run {
                findNavController().popBackStack()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                println("HotelDetailFragment ERROR: $errorMessage")
                // Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadHotelDetails(args.hotelId)
    }
}