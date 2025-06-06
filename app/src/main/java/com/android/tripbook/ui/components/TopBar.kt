package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tripbook.model.Trip
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.ui.theme.WhiteSmoke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    currentRoute: String,
    trips: List<Trip> = emptyList(),
    onTripSelected: (Trip) -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            if (currentRoute.startsWith("catalog")) {
                SurpriseMeButton(
                    trips = trips,
                    onTripSelected = onTripSelected,
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else {
                IconButton(onClick = { /* Handle notification click */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }
        },
        modifier = Modifier,
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Purple40,
            titleContentColor = WhiteSmoke,
            actionIconContentColor = WhiteSmoke
        )
    )
}
