package com.android.tripbook.presentation.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tripbook.util.ValidationResult
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDetailsPage(
    birthDate: String,
    onBirthDateChanged: (String) -> Unit,
    bio: String,
    bioValidation: ValidationResult,
    onBioChanged: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var bioState by remember { mutableStateOf(bio) }
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Tell us more about you, would you?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Birth date field
        OutlinedTextField(
            value = birthDate,
            onValueChange = { /* Read-only field */ },
            label = { Text("Birth Date") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            },
            placeholder = { Text("Select your birth date") }
        )


        Spacer(modifier = Modifier.height(24.dp))

        // Bio field with validation
        OutlinedTextField(
            value = bioState,
            onValueChange = {
                bioState = it
                onBioChanged(it)
            },
            label = { Text("About You") },
            placeholder = { Text("Share a bit about yourself and your travel passions...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(24.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            maxLines = 4,
            isError = !bioValidation.isValid,
            supportingText = {
                if (!bioValidation.isValid) {
                    Text(
                        text = bioValidation.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("${bioState.length}/150 characters")
                }
            }
        )

    }

    Spacer(modifier = Modifier.height(24.dp))
    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (birthDate.isNotEmpty()) {
                try {
                    dateFormatter.parse(birthDate)?.time
                } catch (e: Exception) {
                    null
                }
            } else null
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        onBirthDateChanged(dateFormatter.format(date))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
