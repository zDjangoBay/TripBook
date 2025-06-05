package com.android.tripbook.utils

import android.content.Context
import android.content.Intent
import com.android.tripbook.ui.chat.GroupChatActivity

object ChatLauncher {

    fun openGroupChat(context: Context, tripId: String, tripName: String) {
        val intent = Intent(context, GroupChatActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
            putExtra("TRIP_NAME", tripName)
        }
        context.startActivity(intent)
    }

    fun openGroupChatWithUserId(context: Context, tripId: String, tripName: String, userId: String) {
        val intent = Intent(context, GroupChatActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
            putExtra("TRIP_NAME", tripName)
            putExtra("USER_ID", userId)
        }
        context.startActivity(intent)
    }

    fun openGroupChatWithExtras(context: Context, tripId: String, tripName: String, extras: Map<String, String>) {
        val intent = Intent(context, GroupChatActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
            putExtra("TRIP_NAME", tripName)
            extras.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        context.startActivity(intent)
    }
}