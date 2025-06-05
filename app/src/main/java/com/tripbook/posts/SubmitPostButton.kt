package com.tripbook.posts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview  // Critical import
import androidx.compose.ui.unit.dp

@Composable
fun SubmitPostButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        enabled = enabled
    ) {
        Text(
            text = "Submit Post",
            color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)  // Now works!
@Composable
fun SubmitPostButtonPreview() {
    MaterialTheme {
        SubmitPostButton(
            onClick = { /* Dummy action */ },
            enabled = true
        )
    }
}

