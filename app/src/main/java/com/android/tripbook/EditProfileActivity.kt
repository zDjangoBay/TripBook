package com.android.tripbook

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        findViewById<Button>(R.id.saveProfileButton).setOnClickListener {
            Toast.makeText(this, "Changes saved (demo)", Toast.LENGTH_SHORT).show()
            finish() // Go back to the previous screen
        }
    }
}
