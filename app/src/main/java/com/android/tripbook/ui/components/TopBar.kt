package com.android.tripbook.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tripbook.ui.theme.Purple40
import com.android.tripbook.ui.theme.WhiteSmoke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(onClick = { /* Handle notification click */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
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
