// ui/booking/BookingConfirmationFragment.kt
package com.android.tripbook.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.tripbook.R

class BookingConfirmationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_booking_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.confirmation_message_text_view).text = "Your booking is confirmed!"
        view.findViewById<TextView>(R.id.booking_id_text_view).text = "Booking ID: ABC-12345" // Mock ID

        view.findViewById<Button>(R.id.go_to_home_button).setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false) // Go back to home fragment
        }
    }
}