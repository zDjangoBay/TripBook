package com.android.tripbook.ui.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.tripbook.R
import com.android.tripbook.data.MediaItem
import com.android.tripbook.data.MediaType
import com.android.tripbook.databinding.ActivityMediaViewerBinding
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem as ExoMediaItem
import com.google.android.exoplayer2.Player

class MediaViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaViewerBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mediaItem = intent.getParcelableExtra<MediaItem>(EXTRA_MEDIA_ITEM)
            ?: throw IllegalArgumentException("MediaItem is required")

        setupToolbar()
        setupMediaView(mediaItem)
        setupBottomBar()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupMediaView(mediaItem: MediaItem) {
        when (mediaItem.type) {
            MediaType.PHOTO -> {
                binding.viewPager.visibility = View.VISIBLE
                binding.playerView.visibility = View.GONE
                // TODO: Implement ViewPager adapter for photos
            }
            MediaType.VIDEO -> {
                binding.viewPager.visibility = View.GONE
                binding.playerView.visibility = View.VISIBLE
                setupVideoPlayer(mediaItem)
            }
        }
    }

    private fun setupVideoPlayer(mediaItem: MediaItem) {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            val mediaItem = ExoMediaItem.fromUri(mediaItem.path)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    private fun setupBottomBar() {
        binding.btnShare.setOnClickListener {
            // TODO: Implement share functionality
        }

        binding.btnDownload.setOnClickListener {
            // TODO: Implement download functionality
        }

        binding.btnDelete.setOnClickListener {
            // TODO: Implement delete functionality
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    companion object {
        private const val EXTRA_MEDIA_ITEM = "extra_media_item"

        fun start(context: Context, mediaItem: MediaItem) {
            val intent = Intent(context, MediaViewerActivity::class.java).apply {
                putExtra(EXTRA_MEDIA_ITEM, mediaItem)
            }
            context.startActivity(intent)
        }
    }
} 