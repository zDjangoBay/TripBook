package com.android.tripbook

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class SouvenirAdapter(
    private val souvenirs: MutableList<Souvenir>,
    private val onDeleteClick: (Souvenir) -> Unit,
    private val onImageClick: (Uri) -> Unit
) : RecyclerView.Adapter<SouvenirAdapter.SouvenirViewHolder>() {

    class SouvenirViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.savedImageView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.savedDescriptionTextView)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)
        val deleteButton: MaterialButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SouvenirViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_souvenir, parent, false)
        return SouvenirViewHolder(view)
    }

    override fun onBindViewHolder(holder: SouvenirViewHolder, position: Int) {
        val souvenir = souvenirs[position]
        // Parse String to Uri for imageView
        val imageUri: Uri? = souvenir.imageUri.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
        holder.imageView.setImageURI(imageUri)
        holder.descriptionTextView.text = souvenir.description
        holder.timestampTextView.text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(souvenir.timestamp))
        holder.deleteButton.setOnClickListener { onDeleteClick(souvenir) }
        // Parse String to Uri for onImageClick, ensure non-null Uri
        holder.imageView.setOnClickListener {
            imageUri?.let { onImageClick(it) } // Only call if Uri is valid
        }
        // Apply animation
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_in_left)
        holder.itemView.startAnimation(animation)
    }

    override fun getItemCount(): Int = souvenirs.size

    fun updateSouvenirs(newSouvenirs: List<Souvenir>) {
        souvenirs.clear()
        souvenirs.addAll(newSouvenirs)
        notifyDataSetChanged()
    }
}