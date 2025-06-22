package com.android.tripbook.ViewModel

m.yourpackage.R
import com.google.android.material.button.MaterialButton

class TransportSelectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transport_selection, container, false)

        val planeBtn = view.findViewById<MaterialButton>(R.id.btnPlane)
        val shipBtn = view.findViewById<MaterialButton>(R.id.btnShip)
        val carBtn = view.findViewById<MaterialButton>(R.id.btnCar)

        planeBtn.setOnClickListener {
            // Navigate to airline options fragment
        }

        shipBtn.setOnClickListener {
            // Navigate to ship options fragment
        }

        carBtn.setOnClickListener {
            // Navigate to car options fragment
        }

        return view
    }
}