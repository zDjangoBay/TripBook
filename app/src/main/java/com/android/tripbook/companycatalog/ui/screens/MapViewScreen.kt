package com.android.tripbook.companycatalog.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.android.tripbook.companycatalog.data.CompanyData
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.PermissionStatus
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapViewScreen(
    navController: NavHostController,
    companies: List<CompanyData>,
    userLocation: GeoPoint = GeoPoint(37.7749, -122.4194) // Default to San Francisco
) {
    var selectedCompany by remember { mutableStateOf<CompanyData?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // Request location permission
    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    
    // Initialize OSMDroid configuration
    Configuration.getInstance().apply {
        userAgentValue = context.packageName
        osmdroidTileCache = context.getExternalFilesDir(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Map View",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (locationPermissionState.status) {
                is PermissionStatus.Granted -> {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(12.0)
                                mapView = this
                            }
                        },
                        update = { view ->
                            // Clear existing overlays
                            view.overlays.clear()

                            // Add location overlay
                            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), view).apply {
                                enableMyLocation()
                                enableFollowLocation()
                                isDrawAccuracyEnabled = true
                            }
                            view.overlays.add(locationOverlay)

                            // Add company markers
                            companies.forEach { company ->
                                val companyLocation = GeoPoint(company.location.first, company.location.second)
                                val marker = Marker(view).apply {
                                    position = companyLocation
                                    title = company.name
                                    snippet = company.description
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    
                                    // Create custom marker with company name
                                    val markerDrawable = createCompanyMarker(context, company.name)
                                    icon = markerDrawable
                                    
                                    setOnMarkerClickListener { _, _ ->
                                        selectedCompany = company
                                        true
                                    }
                                }
                                view.overlays.add(marker)
                            }

                            // Center map on user's location if available
                            if (locationOverlay.myLocation != null) {
                                view.controller.animateTo(locationOverlay.myLocation)
                            } else {
                                view.controller.setCenter(userLocation)
                            }
                            
                            // Set loading to false after map is ready
                            isLoading = false
                        }
                    )

                    // Recenter button
                    FloatingActionButton(
                        onClick = {
                            mapView?.let { view ->
                                val locationOverlay = view.overlays.firstOrNull { it is MyLocationNewOverlay } as? MyLocationNewOverlay
                                if (locationOverlay?.myLocation != null) {
                                    view.controller.animateTo(locationOverlay.myLocation)
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Recenter map"
                        )
                    }

                    // Selected company card
                    selectedCompany?.let { company ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = company.name,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = company.description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Location permission is required to show the map")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                            Text("Grant Permission")
                        }
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading Map...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    // Handle lifecycle events
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun createCompanyMarker(context: Context, companyName: String): Drawable {
    // Create a bitmap for the marker with increased size
    val size = 180  // Increased from 120 to 180
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    // Draw the building icon
    val paint = Paint().apply {
        color = Color.parseColor("#FF6200EE") // Primary color
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    // Draw building shape with increased size
    val buildingWidth = size * 0.7f  // Increased from 0.6f to 0.7f
    val buildingHeight = size * 0.6f  // Increased from 0.5f to 0.6f
    val buildingLeft = (size - buildingWidth) / 2
    val buildingTop = size * 0.05f  // Reduced top margin to accommodate larger building
    
    // Main building rectangle
    canvas.drawRect(
        buildingLeft,
        buildingTop,
        buildingLeft + buildingWidth,
        buildingTop + buildingHeight,
        paint
    )
    
    // Draw windows with increased size
    val windowPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    val windowSize = buildingWidth * 0.25f  // Increased from 0.2f to 0.25f
    val windowSpacing = buildingWidth * 0.1f
    val windowTop = buildingTop + buildingHeight * 0.15f  // Adjusted for better window placement
    
    // Draw two rows of windows
    for (row in 0..1) {
        for (col in 0..1) {
            canvas.drawRect(
                buildingLeft + windowSpacing + (windowSize + windowSpacing) * col,
                windowTop + (windowSize + windowSpacing) * row,
                buildingLeft + windowSpacing + (windowSize + windowSpacing) * col + windowSize,
                windowTop + (windowSize + windowSpacing) * row + windowSize,
                windowPaint
            )
        }
    }
    
    // Draw company name with increased size
    val textPaint = Paint().apply {
        color = Color.parseColor("#FF6200EE") // Primary color
        isAntiAlias = true
        textSize = 36f  // Increased from 24f to 36f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    
    // Draw company name below the building
    canvas.drawText(companyName, size / 2f, size - 15f, textPaint)  // Adjusted position for larger text
    
    return BitmapDrawable(context.resources, bitmap)
} 