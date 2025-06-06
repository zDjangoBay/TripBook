package com.yourpackage.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yourpackage.profile.ProfileIntegrationAdapter

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val profileAdapter = ProfileIntegrationAdapter(this)
        profileAdapter.openUserProfile("12345") // Example user ID
    }
}
