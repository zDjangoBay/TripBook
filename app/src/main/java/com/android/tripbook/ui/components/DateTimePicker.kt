package com.android.tripbook.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDateTime
import java.util.*

@Composable
fun DateTimePicker(
    onDateSelected: (LocalDateTime) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var selectedDate by remember { mutableStateOf<LocalDateTime?>(null) }

    Button(onClick = {
        showDateTimePicker(context) { dateTime ->
            selectedDate = dateTime
            onDateSelected(dateTime)
        }
    }) {
        Text(text = selectedDate?.toString() ?: "Pick Date & Time")
    }
}

private fun showDateTimePicker(
    context: Context,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val selectedDateTime = LocalDateTime.of(year, month + 1, day, hour, minute)
                    onDateTimeSelected(selectedDateTime)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}
