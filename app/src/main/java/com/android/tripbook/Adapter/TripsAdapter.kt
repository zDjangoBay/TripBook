package com.android.tripbook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.Model.Triphome
import com.android.tripbook.R

class TripsAdapter(private val trips: List<Triphome>) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fromText: TextView = itemView.findViewById(R.id.tv_from)
        val toText: TextView = itemView.findViewById(R.id.tv_to)
        val timeText: TextView = itemView.findViewById(R.id.tv_time)
        val priceText: TextView = itemView.findViewById(R.id.tv_price)
        val companyText: TextView = itemView.findViewById(R.id.tv_company)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.fromText.text = trip.from
        holder.toText.text = trip.to
        holder.timeText.text = trip.time
        holder.priceText.text = "$${trip.price}"
        holder.companyText.text = trip.companyName
    }

    override fun getItemCount(): Int = trips.size
}