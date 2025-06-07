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
import com.android.tripbook.datamining.data.model.ChartDataPoint
import com.android.tripbook.datamining.data.model.ChartDataSet
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * An animated pie chart component with interactive selection
 */
@Composable
fun PieChart(
    chartData: ChartDataSet,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.errorContainer
    ),
    animationDuration: Int = 1000,
    showLegend: Boolean = true,
    showPercentage: Boolean = true,
    donutHolePercentage: Float = 0.5f // 0 for pie, >0 for donut
) {
    val animatedProgress = remember { Animatable(0f) }
    var selectedSliceIndex by remember { mutableStateOf<Int?>(null) }
    val totalValue = chartData.dataPoints.sumOf { it.value.toDouble() }.toFloat()
    
    LaunchedEffect(chartData) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animationDuration)
        )
    }
    
    Column(modifier = modifier) {
        // Chart title
        Text(
            text = chartData.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Chart description
        if (chartData.description.isNotEmpty()) {
            Text(
                text = chartData.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Main chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .pointerInput(chartData) {
                        detectTapGestures { offset ->
                            // Calculate which slice was tapped
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = minOf(size.width, size.height) / 2
                            
                            // Check if tap is within the chart
                            val distanceFromCenter = sqrt(
                                (offset.x - center.x).pow(2) + (offset.y - center.y).pow(2)
                            )
                            
                            if (distanceFromCenter <= radius && distanceFromCenter >= radius * donutHolePercentage) {
                                // Calculate angle of tap
                                val angle = (atan2(
                                    offset.y - center.y,
                                    offset.x - center.x
                                ) * 180 / PI).toFloat()
                                
                                // Convert to 0-360 range
                                val normalizedAngle = if (angle < 0) angle + 360 else angle
                                
                                // Find which slice this angle belongs to
                                var startAngle = -90f // Start from top
                                chartData.dataPoints.forEachIndexed { index, point ->
                                    val sweepAngle = (point.value / totalValue) * 360f
                                    val endAngle = startAngle + sweepAngle
                                    
                                    if (normalizedAngle in startAngle..endAngle) {
                                        selectedSliceIndex = index
                                        return@detectTapGestures
                                    }
                                    
                                    startAngle = endAngle
                                }
                            } else {
                                selectedSliceIndex = null
                            }
                        }
                    }
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = minOf(size.width, size.height) / 2
                
                var startAngle = -90f // Start from top
                
                chartData.dataPoints.forEachIndexed { index, point ->
                    val sweepAngle = (point.value / totalValue) * 360f * animatedProgress.value
                    val color = colors[index % colors.size]
                    
                    // Draw slice
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(
                            center.x - radius,
                            center.y - radius
                        ),
                        size = Size(radius * 2, radius * 2)
                    )
                    
                    // If this is a donut chart, draw the hole
                    if (donutHolePercentage > 0) {
                        drawArc(
                            color = MaterialTheme.colorScheme.background,
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = true,
                            topLeft = Offset(
                                center.x - radius * donutHolePercentage,
                                center.y - radius * donutHolePercentage
                            ),
                            size = Size(radius * 2 * donutHolePercentage, radius * 2 * donutHolePercentage)
                        )
                    }
                    
                    // Highlight selected slice
                    if (selectedSliceIndex == index) {
                        val highlightRadius = radius * 1.05f
                        drawArc(
                            color = color.copy(alpha = 0.7f),
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            topLeft = Offset(
                                center.x - highlightRadius,
                                center.y - highlightRadius
                            ),
                            size = Size(highlightRadius * 2, highlightRadius * 2),
                            style = Stroke(width = 8.dp.toPx())
                        )
                    }
                    
                    // Draw percentage in the middle of the slice if enabled
                    if (showPercentage && animatedProgress.value == 1f) {
                        val percentage = (point.value / totalValue) * 100
                        val sliceMiddleAngle = startAngle + (sweepAngle / 2)
                        val textRadius = radius * 0.75f
                        
                        // Calculate position for text
                        val textX = center.x + cos(Math.toRadians(sliceMiddleAngle.toDouble())).toFloat() * textRadius
                        val textY = center.y + sin(Math.toRadians(sliceMiddleAngle.toDouble())).toFloat() * textRadius
                        
                        // Only show percentage for larger slices
                        if (sweepAngle > 30) {
                            drawContext.canvas.nativeCanvas.drawText(
                                "${percentage.toInt()}%",
                                textX,
                                textY,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.WHITE
                                    textAlign = android.graphics.Paint.Align.CENTER
                                    textSize = 12.sp.toPx()
                                }
                            )
                        }
                    }
                    
                    startAngle += sweepAngle
                }
            }
            
            // Show selected slice info
            selectedSliceIndex?.let { index ->
                if (index in chartData.dataPoints.indices) {
                    val point = chartData.dataPoints[index]
                    val percentage = (point.value / totalValue) * 100
                    
                    Card(
                        modifier = Modifier
                            .align(Alignment.Center)
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
                                text = "${percentage.toInt()}%",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Value: ${point.value}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // Legend
        if (showLegend) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                chartData.dataPoints.forEachIndexed { index, point ->
                    val color = colors[index % colors.size]
                    val percentage = (point.value / totalValue) * 100
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(12.dp),
                            color = color,
                            shape = MaterialTheme.shapes.small
                        ) {}
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = point.label,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Text(
                            text = "${percentage.toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun Float.pow(exponent: Int): Float {
    var result = 1f
    repeat(exponent) { result *= this }
    return result
}
