package com.android.tripbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.data.Trip
import java.text.SimpleDateFormat
import java.util.Locale

class TripAdapter(private var trips: List<Trip> = emptyList()) :
    RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.tv_trip_title)
        val destinationText: TextView = itemView.findViewById(R.id.tv_trip_destination)
        val dateText: TextView = itemView.findViewById(R.id.tv_trip_date)
        val durationText: TextView = itemView.findViewById(R.id.tv_trip_duration)
        val typeText: TextView = itemView.findViewById(R.id.tv_trip_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.titleText.text = trip.title
        holder.destinationText.text = trip.destination
        holder.dateText.text =
            "${dateFormat.format(trip.startDate)} - ${dateFormat.format(trip.endDate)}"
        holder.durationText.text = "${trip.duration} days"
        holder.typeText.text = trip.type
    }

    override fun getItemCount(): Int = trips.size

    fun updateTrips(newTrips: List<Trip>) {
        trips = newTrips
        notifyDataSetChanged()
    }
}