package com.android.tripbook.datamining.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.android.tripbook.datamining.data.model.RadarChartData
import com.android.tripbook.datamining.data.model.RadarChartDataSet
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A radar chart for comparing multiple dimensions across different categories
 */
@Composable
fun RadarChart(
    dataSet: RadarChartDataSet,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error
    ),
    showLegend: Boolean = true,
    showGrid: Boolean = true,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    animationDuration: Int = 1000
) {
    val animatedProgress = remember { Animatable(0f) }
    
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
        
        // Main radar chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = minOf(size.width, size.height) / 2 * 0.8f
                val numberOfAxes = dataSet.axes.size
                
                // Draw grid
                if (showGrid) {
                    // Draw concentric circles
                    val gridLevels = 5
                    for (i in 1..gridLevels) {
                        val gridRadius = radius * i / gridLevels
                        drawCircle(
                            color = gridColor,
                            radius = gridRadius,
                            center = center,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                            )
                        )
                    }
                    
                    // Draw axes
                    for (i in 0 until numberOfAxes) {
                        val angle = 2 * PI * i / numberOfAxes - PI / 2
                        val x = center.x + cos(angle).toFloat() * radius
                        val y = center.y + sin(angle).toFloat() * radius
                        
                        drawLine(
                            color = gridColor,
                            start = center,
                            end = Offset(x, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                        )
                        
                        // Draw axis label
                        val labelRadius = radius * 1.1f
                        val labelX = center.x + cos(angle).toFloat() * labelRadius
                        val labelY = center.y + sin(angle).toFloat() * labelRadius
                        
                        drawContext.canvas.nativeCanvas.drawText(
                            dataSet.axes[i],
                            labelX,
                            labelY,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.BLACK
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 12.dp.toPx()
                            }
                        )
                    }
                }
                
                // Draw data
                dataSet.data.forEachIndexed { dataIndex, data ->
                    val color = colors[dataIndex % colors.size]
                    val path = Path()
                    
                    // Draw polygon
                    for (i in 0 until numberOfAxes) {
                        val normalizedValue = (data.values[i] - dataSet.minValue) / 
                                            (dataSet.maxValue - dataSet.minValue)
                        val scaledValue = normalizedValue * animatedProgress.value
                        val angle = 2 * PI * i / numberOfAxes - PI / 2
                        val x = center.x + cos(angle).toFloat() * radius * scaledValue
                        val y = center.y + sin(angle).toFloat() * radius * scaledValue
                        
                        if (i == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }
                    
                    // Close the path
                    path.close()
                    
                    // Draw filled polygon with transparency
                    drawPath(
                        path = path,
                        color = color.copy(alpha = 0.3f)
                    )
                    
                    // Draw outline
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    
                    // Draw points at each vertex
                    for (i in 0 until numberOfAxes) {
                        val normalizedValue = (data.values[i] - dataSet.minValue) / 
                                            (dataSet.maxValue - dataSet.minValue)
                        val scaledValue = normalizedValue * animatedProgress.value
                        val angle = 2 * PI * i / numberOfAxes - PI / 2
                        val x = center.x + cos(angle).toFloat() * radius * scaledValue
                        val y = center.y + sin(angle).toFloat() * radius * scaledValue
                        
                        drawCircle(
                            color = color,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }
        
        // Legend
        if (showLegend && dataSet.data.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                dataSet.data.forEachIndexed { index, data ->
                    val color = colors[index % colors.size]
                    
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
                            text = data.label,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
