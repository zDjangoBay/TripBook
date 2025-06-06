package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tripbook.ui.screens.UserProfileDemoScreen
import com.android.tripbook.ui.theme.TripBookTheme

/**
 * Mini Profile Embedding Demo Activity
 *
 * This activity demonstrates the UserProfileEmbedding component
 * that can be integrated into any trip catalog.
 *
 * Features:
 * - Circular profile picture with initials
 * - User name and destination display
 * - Multiple size options (Small, Medium, Large)
 * - Material Design 3 styling
 */
class MiniProfileDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserProfileDemoScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MiniProfileDemoPreview() {
    TripBookTheme {
        UserProfileDemoScreen()
    }
}