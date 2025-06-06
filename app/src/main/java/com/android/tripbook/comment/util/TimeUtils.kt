package com.android.tripbook.comment.util

import java.util.concurrent.TimeUnit

object TimeUtils {
    fun getRelativeTimeSpan(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
            diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
            diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
            else -> {
                val date = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(timestamp))
                date
            }
        }
    }
}
