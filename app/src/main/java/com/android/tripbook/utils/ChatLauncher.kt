package com.yourpackage.tripbook.utils

import android.content.Context
import android.content.Intent
import com.yourpackage.tripbook.ui.chat.GroupChatActivity

object ChatLauncher {

    fun openGroupChat(context: Context, tripId: String, tripName: String) {
        val intent = Intent(context, GroupChatActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
            putExtra("TRIP_NAME", tripName)
        }
        context.startActivity(intent)
    }
}