package com.android.tripbook.datamining.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.datamining.data.model.HeatMapDataPoint
import com.android.tripbook.datamining.data.model.HeatMapDataSet
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A heat map visualization for geographical data
 */
@Composable
fun HeatMapChart(
    dataSet: HeatMapDataSet,
    modifier: Modifier = Modifier,
    lowColor: Color = Color(0xFF4CAF50), // Green
    mediumColor: Color = Color(0xFFFFC107), // Yellow
    highColor: Color = Color(0xFFF44336), // Red
    pointRadius: Float = 50f,
    animationDuration: Int = 1000,
    showLegend: Boolean = true
) {
    val animatedProgress = remember { Animatable(0f) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    
    LaunchedEffect(dataSet) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }
    
    Column(modifier = modifier) {
        // Chart title
        Text(
            text = dataSet.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Chart description
        if (dataSet.description.isNotEmpty()) {
            Text(
                text = dataSet.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Main heat map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                    .pointerInput(dataSet) {
                        detectTapGestures { offset ->
                            // Find the closest point to the tap
                            var closestPointIndex: Int? = null
                            var closestDistance = Float.MAX_VALUE
                            
                            dataSet.dataPoints.forEachIndexed { index, point ->
                                val pointX = point.x * size.width
                                val pointY = point.y * size.height
                                val distance = sqrt(
                                    (offset.x - pointX).pow(2) + (offset.y - pointY).pow(2)
                                )
                                
                                if (distance < closestDistance && distance < pointRadius) {
                                    closestDistance = distance
                                    closestPointIndex = index
                                }
                            }
                            
                            selectedPointIndex = closestPointIndex
                        }
                    }
            ) {
                // Draw map outline if provided
                dataSet.mapOutline?.let { outline ->
                    drawPath(
                        path = outline,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                
                // Draw heat points
                dataSet.dataPoints.forEach { point ->
                    val pointX = point.x * size.width
                    val pointY = point.y * size.height
                    
                    // Normalize intensity between 0 and 1
                    val normalizedIntensity = (point.intensity - dataSet.minIntensity) / 
                                             (dataSet.maxIntensity - dataSet.minIntensity)
                    
                    // Interpolate color based on intensity
                    val color = when {
                        normalizedIntensity < 0.5f -> {
                            // Interpolate between low and medium
                            val t = normalizedIntensity * 2
                            Color(
                                red = lowColor.red * (1 - t) + mediumColor.red * t,
                                green = lowColor.green * (1 - t) + mediumColor.green * t,
                                blue = lowColor.blue * (1 - t) + mediumColor.blue * t,
                                alpha = (0.3f + normalizedIntensity * 0.5f) * animatedProgress.value
                            )
                        }
                        else -> {
                            // Interpolate between medium and high
                            val t = (normalizedIntensity - 0.5f) * 2
                            Color(
                                red = mediumColor.red * (1 - t) + highColor.red * t,
                                green = mediumColor.green * (1 - t) + highColor.green * t,
                                blue = mediumColor.blue * (1 - t) + highColor.blue * t,
                                alpha = (0.3f + normalizedIntensity * 0.5f) * animatedProgress.value
                            )
                        }
                    }
                    
                    // Draw heat point as a radial gradient
                    val radius = pointRadius * (0.5f + normalizedIntensity * 0.5f) * animatedProgress.value
                    drawCircle(
                        color = color,
                        radius = radius,
                        center = Offset(pointX, pointY)
                    )
                }
                
                // Draw point markers
                dataSet.dataPoints.forEachIndexed { index, point ->
                    val pointX = point.x * size.width
                    val pointY = point.y * size.height
                    
                    // Draw a small dot at the exact point location
                    drawCircle(
                        color = if (index == selectedPointIndex) 
                                MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        radius = if (index == selectedPointIndex) 6.dp.toPx() else 3.dp.toPx(),
                        center = Offset(pointX, pointY)
                    )
                }
            }
            
            // Show selected point info
            selectedPointIndex?.let { index ->
                if (index in dataSet.dataPoints.indices) {
                    val point = dataSet.dataPoints[index]
                    
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = point.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Intensity: ${point.intensity.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (point.metadata.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = point.metadata,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Legend
        if (showLegend) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Low intensity
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(16.dp),
                        color = lowColor,
                        shape = MaterialTheme.shapes.small
                    ) {}
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Low",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
                
                // Medium intensity
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(16.dp),
                        color = mediumColor,
                        shape = MaterialTheme.shapes.small
                    ) {}
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Medium",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
                
                // High intensity
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(16.dp),
                        color = highColor,
                        shape = MaterialTheme.shapes.small
                    ) {}
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "High",
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
