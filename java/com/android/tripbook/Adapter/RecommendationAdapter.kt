package com.android.tripbook.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.Model.Place
import com.android.tripbook.databinding.ViewholderrecommendedBinding
import com.bumptech.glide.Glide

class RecommendationAdapter(private val places: List<Place>):
RecyclerView.Adapter<RecommendationAdapter.Viewholder>() {
    private lateinit var context: Context

    class Viewholder (val binding: ViewholderrecommendedBinding):
    RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationAdapter.Viewholder {

        context=parent.context
        val binding=ViewholderrecommendedBinding.inflate(
            LayoutInflater.from(context),
            parent ,
            false
        )
        return Viewholder(binding)

    }

    override fun onBindViewHolder(holder: RecommendationAdapter.Viewholder, position: Int) {
        val trip=places[position]
        Glide.with(context)
            .load(trip.picUrl)
            .into(holder.binding.img)

        holder.binding.titletext.text=trip.title
    }

    override fun getItemCount(): Int =places.size
}

