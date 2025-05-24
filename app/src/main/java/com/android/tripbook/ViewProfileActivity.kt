package com.android.tripbook

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ViewProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }
}
