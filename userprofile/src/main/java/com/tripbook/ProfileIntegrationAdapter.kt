package com.yourpackage.profile

import android.content.Context
import android.content.Intent
import com.yourpackage.profile.ui.ProfileActivity
import com.yourpackage.profile.ui.EditProfileActivity

class ProfileIntegrationAdapter(private val context: Context) : ProfileModuleInterface {
    override fun openUserProfile(userId: String) {
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra("USER_ID", userId)
        context.startActivity(intent)
    }

    override fun editUserProfile(userId: String) {
        val intent = Intent(context, EditProfileActivity::class.java)
        intent.putExtra("USER_ID", userId)
        context.startActivity(intent)
    }
}
