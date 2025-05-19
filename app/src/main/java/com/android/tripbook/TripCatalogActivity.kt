package com.android.tripbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.android.tripbook.ui.navigation.TripNavGraph
import com.android.tripbook.ui.theme.TripBookTheme

class TripCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripBookTheme {
                TripNavGraph()
            }
        }
    }
}
