//package com.android.tripbook.adapters
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import coil.load
//import com.android.tripbook.R
//import com.android.tripbook.data.models.Service
//
//class ServiceAdapter(
//    private val services: List<Service>,
//    private val onItemClicked: (Service) -> Unit
//) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {
//
//    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val serviceImage: ImageView = itemView.findViewById(R.id.service_image)
//        val serviceName: TextView = itemView.findViewById(R.id.service_name)
//        val serviceType: TextView = itemView.findViewById(R.id.service_type)
//        val servicePrice: TextView = itemView.findViewById(R.id.service_price)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_service, parent, false)
//        return ServiceViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
//        val service = services[position]
//        holder.serviceName.text = service.name
//        holder.serviceType.text = holder.itemView.context.getString(R.string.service_type_label, service.type)
//        holder.servicePrice.text = service.price
//        holder.serviceImage.load(service.imageUrl) {
//            placeholder(R.drawable.service_placeholder)
//            error(R.drawable.ic_error_image)
//            crossfade(true)
//        }
//
//        holder.itemView.setOnClickListener {
//            onItemClicked(service)
//        }
//    }
//
//    override fun getItemCount(): Int = services.size
//}