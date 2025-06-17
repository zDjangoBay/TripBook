package com.android.tripbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.tripbook.R
import com.android.tripbook.data.models.Hotel
import android.util.Log

class HotelsAdapter(
    private val hotels: List<Hotel>,
    private val onItemClicked: (Hotel) -> Unit
) : RecyclerView.Adapter<HotelsAdapter.HotelViewHolder>() {

    class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Ensure these IDs match your item_hotel.xml layout
        val hotelImage: ImageView = itemView.findViewById(R.id.hotel_image)
        val hotelName: TextView = itemView.findViewById(R.id.hotel_name)
        val hotelLocation: TextView = itemView.findViewById(R.id.hotel_location)
        val hotelPrice: TextView = itemView.findViewById(R.id.hotel_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hotel, parent, false)
        return HotelViewHolder(view)
    }

    override fun getItemCount(): Int = hotels.size

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel = hotels[position]
        holder.hotelName.text = hotel.name
        holder.hotelLocation.text = hotel.location
        holder.hotelPrice.text = hotel.priceRange

        holder.hotelImage.load(hotel.imageUrl) {
            placeholder(R.drawable.hotel_placeholder)
            error(R.drawable.ic_error_image)
        }

        holder.itemView.setOnClickListener {
            Log.d("HotelsAdapterDebug", "Click detected on item: ${hotel.name}, calling onItemClicked.")
            onItemClicked(hotel)
    }
}}