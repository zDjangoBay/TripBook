package com.android.tripbook.presentation.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistrationFooter(
    currentStep: Int,
    totalSteps: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    isLastStep: Boolean,
    isNextEnabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Back button (hidden on first step)
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
        }

        // Next/Finish button
        Button(
            onClick = onNext,
            modifier = Modifier.weight(1f),
            enabled = isNextEnabled  // Disable button if validation fails
        ) {
            Text(if (isLastStep) "Create Account" else "Next")
        }
    }
}
