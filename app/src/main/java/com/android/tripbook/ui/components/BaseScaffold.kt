package com.android.tripbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BaseScaffold(
    currentRoute: String,
    title: String,
    onNavigate: (String) -> Unit,
    isLoading: Boolean = false,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = { TopBar(title = title) },
        bottomBar = {
            BottomNavigationBar(current = currentRoute, onNavigate = onNavigate)
        },
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                content(innerPadding)
                ScreenLoader(isLoading)
            }
        }
    )
}
