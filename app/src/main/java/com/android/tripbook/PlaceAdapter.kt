package com.android.tripbook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.android.tripbook.model.Place

class PlaceAdapter(
    private val context: Context,
    private val places: List<Place>,
    private val tagSelector: TagSelector
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeImage: ImageView = itemView.findViewById(R.id.placeImage)
        val placeName: TextView = itemView.findViewById(R.id.placeName)
        val placeLocation: TextView = itemView.findViewById(R.id.placeLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]

        holder.placeName.text = place.name
        holder.placeLocation.text = place.location

        Glide.with(context)
            .load(place.imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.placeImage)

        // Show selection state
        holder.itemView.alpha = if (tagSelector.getSelectedPlaces().contains(place)) 0.5f else 1.0f

        // Handle clicks
        holder.itemView.setOnClickListener {
            tagSelector.togglePlaceSelection(place)
            notifyItemChanged(position)  // Refresh that item’s UI
        }
    }

    override fun getItemCount(): Int = places.size
}
