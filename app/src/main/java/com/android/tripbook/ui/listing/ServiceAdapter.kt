// ui/listing/ServiceAdapter.kt
package com.android.tripbook.ui.listing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.data.models.TravelService

class ServiceAdapter(private val onItemClicked: (TravelService) -> Unit) :
    ListAdapter<TravelService, ServiceAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = getItem(position)
        holder.bind(service, onItemClicked)
    }

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceName: TextView = itemView.findViewById(R.id.service_name_text_view)
        private val agencyName: TextView = itemView.findViewById(R.id.agency_name_text_view)
        private val price: TextView = itemView.findViewById(R.id.price_text_view)
        private val rating: TextView = itemView.findViewById(R.id.rating_text_view)
        private val serviceImage: ImageView = itemView.findViewById(R.id.service_image_view)

        fun bind(service: TravelService, onItemClicked: (TravelService) -> Unit) {
            serviceName.text = service.name
            agencyName.text = service.agency.name
            price.text = "$${service.price}"
            rating.text = "${service.rating} ★"
            // For a real app, load image from URL (e.g., with Glide or Coil)
            // serviceImage.setImageResource(R.drawable.placeholder_image) // Use a placeholder for now

            itemView.setOnClickListener { onItemClicked(service) }
        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<TravelService>() {
        override fun areItemsTheSame(oldItem: TravelService, newItem: TravelService): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TravelService, newItem: TravelService): Boolean {
            return oldItem == newItem
        }
    }
}