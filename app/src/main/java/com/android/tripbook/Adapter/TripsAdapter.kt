package com.android.tripbook.Adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.model.Trip
import com.android.tripbook.databinding.ViewholderTripBinding
import com.bumptech.glide.Glide

class TripsAdapter (private val trips: List<Trip>) : RecyclerView.Adapter<TripsAdapter.Viewholder>() {

    private lateinit var context: Context

    class Viewholder(val binding: ViewholderTripBinding) :
        RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripsAdapter.Viewholder {
        context=parent.context
        val binding = ViewholderTripBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: TripsAdapter.Viewholder, position: Int) {
        val trip = trips[position]

        // Use the first image from imageUrl list for the logo/main image
        if (trip.imageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(trip.imageUrl[0])
                .into(holder.binding.logo)
        }

        // Map existing Trip properties to the UI elements
        holder.binding.companyTxt.text = trip.title
        holder.binding.fromTxt.text = trip.caption
        holder.binding.fromshortTxt.text = trip.title.take(3).uppercase() // First 3 chars as short code
        holder.binding.toTxt.text = "Destination"
        holder.binding.toShortTxt.text = "DEST"
        holder.binding.arrivalTxt.text = "Available"
        holder.binding.scoreTxt.text = "4.5" // Default score
        holder.binding.priceTxt.text = "Contact for pricing"
    }

    override fun getItemCount(): Int =trips.size

}