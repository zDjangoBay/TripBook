package com.android.tripbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.data.models.CarRental

class CarRentalsAdapter(private val carRentals: List<CarRental>) :
    RecyclerView.Adapter<CarRentalsAdapter.CarRentalViewHolder>() {

    // ViewHolder class to hold references to the views in item_car_rental.xml
    class CarRentalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val carModel: TextView = itemView.findViewById(R.id.car_model)
        val carPricePerDay: TextView = itemView.findViewById(R.id.car_price_per_day)
        val carImage: ImageView = itemView.findViewById(R.id.car_image)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarRentalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_car_rental, parent, false) // Inflate item_car_rental.xml
        return CarRentalViewHolder(view)
    }

    // Called by RecyclerView to display the data at the specified position.
    // This method updates the contents of the itemView to reflect the item at the given position.
    override fun onBindViewHolder(holder: CarRentalViewHolder, position: Int) {
        val carRental = carRentals[position]
        holder.carModel.text = carRental.model
        holder.carPricePerDay.text = carRental.price
        holder.carImage.setImageResource(carRental.imageUrl)


    }

    override fun getItemCount(): Int = carRentals.size
}