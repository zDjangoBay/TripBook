package com.yourpackage.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yourpackage.profile.ProfileIntegrationAdapter
import com.yourpackage.profile.ProfileNavigator
import com.yourpackage.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val profileAdapter = ProfileIntegrationAdapter(this)
        profileAdapter.openUserProfile("12345") // Example user ID
        // Register the ProfileNavigator
        ProfileIntegrationAdapter.registerInterface(ProfileNavigator(this))

        // Example usage (replace with actual user logic)
        val userId = "user_123"
        ProfileIntegrationAdapter.navigateToProfile(userId)
    }
}
