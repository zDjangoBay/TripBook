package com.android.tripbook.ui.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.tripbook.R

class MediaGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_gallery)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MediaGalleryFragment.newInstance())
                .commit()
        }
    }
} 