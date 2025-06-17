package com.android.tripbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.data.models.Tour

class ToursAdapter(private val tours: List<Tour>) :
    RecyclerView.Adapter<ToursAdapter.TourViewHolder>() {

    // ViewHolder class to hold references to the views in item_tour.xml
    class TourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tourName: TextView = itemView.findViewById(R.id.tour_name)
        val tourDuration: TextView = itemView.findViewById(R.id.tour_duration)
        val tourPrice: TextView = itemView.findViewById(R.id.tour_price)
        val tourImage: ImageView = itemView.findViewById(R.id.tour_image)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view)
    }

    // Called by RecyclerView to display the data at the specified position.
    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val tour = tours[position]
        holder.tourName.text = tour.name
        holder.tourDuration.text = tour.duration
        holder.tourPrice.text = tour.price
        holder.tourImage.setImageResource(tour.imageUrl)


    }

    // Returns the total number of items in the data set held by the adapter.
    override fun getItemCount(): Int = tours.size
}