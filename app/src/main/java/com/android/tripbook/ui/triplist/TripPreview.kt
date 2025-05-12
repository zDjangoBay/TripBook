package com.android.tripbook.ui.triplist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Preview(showBackground = true)
@Composable
fun TripCardPreview() {
    TripCard(
        trip = Trip(
            title = "Trip to Ghana",
            destination = "Accra, Ghana",
            dateRange = "June 10 - June 20, 2025",
            status = "Upcoming"
        ),
        modifier = Modifier
    )
}