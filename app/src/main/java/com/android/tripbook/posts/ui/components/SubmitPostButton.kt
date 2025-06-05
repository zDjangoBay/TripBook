package com.android.tripbook.posts.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme

@Composable
fun SubmitPostButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSubmit,
        enabled = isEnabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
                Text("Publishing...")
            }
        } else {
            Text(
                text = "Publish Post",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSubmitPostButtonEnabled() {
    TripBookTheme {
        SubmitPostButton(isEnabled = true, isLoading = false, onSubmit = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSubmitPostButtonLoading() {
    TripBookTheme {
        SubmitPostButton(isEnabled = false, isLoading = true, onSubmit = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSubmitPostButtonDisabled() {
    TripBookTheme {
        SubmitPostButton(isEnabled = false, isLoading = false, onSubmit = {})
    }
}