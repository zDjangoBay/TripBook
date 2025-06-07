package com.android.tripbook.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.android.tripbook.ui.chat.GroupChatActivity

object ChatLauncher {

    fun openGroupChat(context: Context, tripId: String?, tripName: String?) {
        if (tripId.isNullOrEmpty() || tripName.isNullOrEmpty()) {
            Toast.makeText(context, "Missing trip information", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, GroupChatActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
            putExtra("TRIP_NAME", tripName)
        }
        context.startActivity(intent)
    }

    fun openGroupChatWithUserId(context: Context, tripId: String?, tripName: String?, userId: String?) {
        if (tripId.isNullOrEmpty() || tripName.isNullOrEmpty()) {
            Toast.makeText(context, "Missing trip information", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, GroupChatActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
            putExtra("TRIP_NAME", tripName)
            if (!userId.isNullOrEmpty()) putExtra("USER_ID", userId)
        }
        context.startActivity(intent)
    }

    fun openGroupChatWithExtras(context: Context, tripId: String?, tripName: String?, extras: Map<String, String>) {
        if (tripId.isNullOrEmpty() || tripName.isNullOrEmpty()) {
            Toast.makeText(context, "Missing trip information", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, GroupChatActivity::class.java).apply {
            putExtra("TRIP_ID", tripId)
            putExtra("TRIP_NAME", tripName)
            extras.forEach { (key, value) -> putExtra(key, value) }
        }
        context.startActivity(intent)
    }
}
