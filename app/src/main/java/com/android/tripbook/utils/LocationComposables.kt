package com.android.tripbook.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

/**
 * LocationComposables provides Compose utilities for location services
 */
object LocationComposables {

    /**
     * Remembers location manager instance
     */
    @Composable
    fun rememberLocationManager(): LocationManager {
        val context = LocalContext.current
        return remember { LocationManager(context) }
    }

    /**
     * Remembers permission handler instance
     */
    @Composable
    fun rememberPermissionHandler(): PermissionHandler {
        val context = LocalContext.current
        return remember { PermissionHandler(context) }
    }

    /**
     * Lifecycle-aware location manager
     */
    @Composable
    fun rememberLifecycleAwareLocationManager(): LocationManager {
        val context = LocalContext.current
        val locationManager = remember { LocationManager(context) }
        val lifecycleOwner = LocalLifecycleOwner.current

        DisposableEffect(locationManager, lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        locationManager.stopLocationUpdates()
                    }
                    else -> {}
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                locationManager.stopLocationUpdates()
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        return locationManager
    }
}
