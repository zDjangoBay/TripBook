package com.android.tripbook.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.android.tripbook.data.model.Reminder
import com.android.tripbook.receiver.ReminderBroadcastReceiver
import java.util.Calendar

object ReminderScheduler {

    private const val TAG = "ReminderScheduler"

    /**
     * Schedules a reminder using AlarmManager.
     *
     * @param context The application context.
     * @param reminder The reminder object containing details like time and message.
     */
    fun scheduleReminder(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra(ReminderBroadcastReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderBroadcastReceiver.EXTRA_REMINDER_MESSAGE, reminder.message)
            // Add other relevant data if needed by the receiver
        }

        // Use reminder.id as the request code to ensure uniqueness per reminder
        // Use FLAG_UPDATE_CURRENT so if the same reminder is scheduled again (e.g., updated), it replaces the old one.
        // Use FLAG_IMMUTABLE for security, especially targeting Android S+
        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(), // Using reminder ID as request code
            intent,
            pendingIntentFlag
        )

        // Check if exact alarm permission is granted (important for Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Cannot schedule exact alarms. Permission needed.")
                // TODO: Handle the case where permission is not granted.
                // Maybe notify the user or fallback to inexact alarms (less reliable for reminders).
                // For now, we log a warning and attempt to schedule anyway, which might fail silently or throw SecurityException.
                 // Consider requesting permission: Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            }
        }

        try {
            // Use setExactAndAllowWhileIdle for reliability even in Doze mode
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.reminderTimeMillis,
                pendingIntent
            )
            Log.i(TAG, "Reminder scheduled for ID: ${reminder.id} at ${Calendar.getInstance().apply { timeInMillis = reminder.reminderTimeMillis }.time}")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException scheduling reminder ID: ${reminder.id}. Check exact alarm permissions.", e)
            // Handle error - maybe update reminder status or notify user
        } catch (e: Exception) {
            Log.e(TAG, "Exception scheduling reminder ID: ${reminder.id}", e)
            // Handle other potential errors
        }
    }

    /**
     * Cancels a previously scheduled reminder.
     *
     * @param context The application context.
     * @param reminderId The ID of the reminder to cancel.
     */
    fun cancelReminder(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Recreate the exact same PendingIntent used for scheduling
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // Use NO_CREATE to check existence
        } else {
            PendingIntent.FLAG_NO_CREATE
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            pendingIntentFlag
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel() // Also cancel the PendingIntent itself
            Log.i(TAG, "Cancelled reminder schedule for ID: $reminderId")
        } else {
            Log.w(TAG, "Could not find PendingIntent to cancel for reminder ID: $reminderId. Might have already fired or not been scheduled.")
        }
    }
}
