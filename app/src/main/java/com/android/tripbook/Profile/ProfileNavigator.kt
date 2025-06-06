package com.yourpackage.profile

import android.content.Context
import android.content.Intent
import com.yourpackage.profile.ui.ProfileActivity

class ProfileNavigator(private val context: Context) : ProfileModuleInterface {
    override fun openProfile(userId: String) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("USER_ID", userId)
        context.startActivity(intent)
    }
}
