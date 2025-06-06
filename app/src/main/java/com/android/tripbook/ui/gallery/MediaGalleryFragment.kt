package com.android.tripbook.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.tripbook.R
import com.android.tripbook.data.MediaItem
import com.android.tripbook.data.MediaType
import com.android.tripbook.databinding.FragmentMediaGalleryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip

class MediaGalleryFragment : Fragment() {

    private var _binding: FragmentMediaGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaAdapter: MediaGalleryAdapter
    private var currentFilter: MediaType? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediaGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFilterChips()
        setupViewToggle()
        loadMedia()
    }

    private fun setupRecyclerView() {
        mediaAdapter = MediaGalleryAdapter { mediaItem ->
            // Handle media item click
            MediaViewerActivity.start(requireContext(), mediaItem)
        }

        binding.mediaRecyclerView.apply {
            adapter = mediaAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            currentFilter = when (checkedId) {
                R.id.chipPhotos -> MediaType.PHOTO
                R.id.chipVideos -> MediaType.VIDEO
                else -> null
            }
            loadMedia()
        }
    }

    private fun setupViewToggle() {
        binding.viewToggleFab.setOnClickListener {
            mediaAdapter.toggleViewType()
            binding.mediaRecyclerView.layoutManager = if (mediaAdapter.isGridView) {
                GridLayoutManager(context, 3)
            } else {
                LinearLayoutManager(context)
            }
        }
    }

    private fun loadMedia() {
        // TODO: Implement media loading from storage
        // This is where you would load media items from storage
        // and filter them based on currentFilter
        val mediaItems = listOf<MediaItem>() // Replace with actual media loading
        mediaAdapter.submitList(mediaItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MediaGalleryFragment()
    }
} 