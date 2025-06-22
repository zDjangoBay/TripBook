package com.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dashboard.databinding.FragmentTransportSelectionBinding

class TransportSelectionFragment : Fragment() {

    private var _binding: FragmentTransportSelectionBinding? = null
    private val binding get() = _binding!!

            override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransportSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlane.setOnClickListener {
            Toast.makeText(requireContext(), "Plane selected", Toast.LENGTH_SHORT).show()
            // findNavController().navigate(R.id.action_transportSelectionFragment_to_airlineSelectionFragment)
        }

        binding.btnShip.setOnClickListener {
            Toast.makeText(requireContext(), "Ship selected", Toast.LENGTH_SHORT).show()
            // findNavController().navigate(R.id.action_transportSelectionFragment_to_shipSelectionFragment)
        }

        binding.btnCar.setOnClickListener {
            Toast.makeText(requireContext(), "Car selected", Toast.LENGTH_SHORT).show()
            // findNavController().navigate(R.id.action_transportSelectionFragment_to_carSelectionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
