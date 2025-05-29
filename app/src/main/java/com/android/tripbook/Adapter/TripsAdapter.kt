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
        val trip=trips[position]
        Glide.with(context)
            .load(trip.imageUrl)
            .into(holder.binding.logo)

        holder.binding.companyTxt.text=trip.title
        holder.binding.fromTxt.text="Trip #${trip.id}"
        holder.binding.fromshortTxt.text="T${trip.id}"
        holder.binding.toTxt.text=trip.description
        holder.binding.toShortTxt.text="DESC"
        holder.binding.arrivalTxt.text="Available"
        holder.binding.scoreTxt.text="N/A"
        holder.binding.priceTxt.text="Price TBD"
    }

    override fun getItemCount(): Int =trips.size

}