package com.android.tripbook.ui.components.travel


import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.ui.theme.TripBookTheme

@Composable
fun BookingButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Text("Réserver maintenant")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookingButton() {
    TripBookTheme {
        BookingButton(onClick = {})
    }
}