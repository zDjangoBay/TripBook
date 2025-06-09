package com.android.tripbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.tripbook.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        val searchButton = view.findViewById<Button>(R.id.search_button)

        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            val action = HomeFragmentDirections.actionHomeFragmentToServiceListingFragment(query)
            findNavController().navigate(action)
        }
    }
}