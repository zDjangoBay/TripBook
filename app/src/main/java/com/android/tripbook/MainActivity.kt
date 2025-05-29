package com.android.tripbook

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.tripbook.ui.components.DateTimePicker
import com.android.tripbook.ui.components.ItineraryBuilder
import com.android.tripbook.ui.components.LocationSelector
import com.android.tripbook.ui.theme.TripBookTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ Now we're inside the class — 'this' works
        requestLocationPermission()

        setContent {
            TripBookTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    ScheduleCreationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    // ✅ This is now inside MainActivity, so 'this' is valid
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}
@Composable
fun ScheduleCreationScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DateTimePicker { }
        LocationSelector { }
        ItineraryBuilder { }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleCreationScreenPreview() {
    TripBookTheme {
        ScheduleCreationScreen()
    }
}
