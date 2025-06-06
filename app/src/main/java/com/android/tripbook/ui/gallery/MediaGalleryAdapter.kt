package com.android.tripbook.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.tripbook.R
import com.android.tripbook.data.MediaItem
import com.android.tripbook.data.MediaType
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class MediaGalleryAdapter(
    private val onItemClick: (MediaItem) -> Unit
) : ListAdapter<MediaItem, MediaGalleryAdapter.MediaViewHolder>(MediaDiffCallback()) {

    var isGridView = true

    fun toggleViewType() {
        isGridView = !isGridView
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.mediaThumbnail)
        private val videoIndicator: ImageView = itemView.findViewById(R.id.videoIndicator)
        private val dateText: TextView = itemView.findViewById(R.id.mediaDate)
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(item: MediaItem) {
            // Load thumbnail
            Glide.with(itemView.context)
                .load(item.path)
                .centerCrop()
                .into(thumbnail)

            // Show/hide video indicator
            videoIndicator.visibility = if (item.type == MediaType.VIDEO) View.VISIBLE else View.GONE

            // Set date
            dateText.text = dateFormat.format(item.dateAdded)

            // Set click listener
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    private class MediaDiffCallback : DiffUtil.ItemCallback<MediaItem>() {
        override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem): Boolean {
            return oldItem == newItem
        }
    }
} 