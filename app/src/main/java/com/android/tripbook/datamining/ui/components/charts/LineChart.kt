package com.android.tripbook.datamining.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.tripbook.datamining.data.model.ChartDataPoint
import com.android.tripbook.datamining.data.model.ChartDataSet
import kotlin.math.roundToInt

/**
 * An animated line chart component with interactive tooltips
 */
@Composable
fun LineChart(
    chartData: ChartDataSet,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    pointColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    showPoints: Boolean = true,
    showGrid: Boolean = true,
    animationDuration: Int = 1000,
    showTooltip: Boolean = true
) {
    val animatedProgress = remember { Animatable(0f) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }
    
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
                .height(200.dp)
                .padding(8.dp)
                .drawBehind {
                    // Draw grid if enabled
                    if (showGrid) {
                        // Horizontal grid lines
                        val gridLineCount = 5
                        val gridLineSpacing = size.height / gridLineCount
                        
                        repeat(gridLineCount + 1) { i ->
                            val y = size.height - (i * gridLineSpacing)
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                        
                        // Vertical grid lines
                        val dataPoints = chartData.dataPoints
                        val xSpacing = size.width / (dataPoints.size - 1)
                        
                        dataPoints.forEachIndexed { index, _ ->
                            val x = index * xSpacing
                            drawLine(
                                color = gridColor,
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        }
                    }
                }
                .pointerInput(chartData) {
                    detectTapGestures { offset ->
                        if (showTooltip && chartData.dataPoints.isNotEmpty()) {
                            val pointSpacing = size.width / (chartData.dataPoints.size - 1)
                            val index = (offset.x / pointSpacing).roundToInt()
                            selectedPointIndex = if (index in chartData.dataPoints.indices) index else null
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxWidth()) {
                val dataPoints = chartData.dataPoints
                if (dataPoints.isEmpty()) return@Canvas
                
                val maxValue = chartData.maxValue
                val minValue = chartData.minValue
                val valueRange = maxValue - minValue
                
                val xSpacing = size.width / (dataPoints.size - 1)
                
                // Draw line
                val path = Path()
                dataPoints.forEachIndexed { index, point ->
                    val x = index * xSpacing
                    val y = size.height - ((point.value - minValue) / valueRange * size.height * animatedProgress.value)
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
                
                // Draw points
                if (showPoints) {
                    dataPoints.forEachIndexed { index, point ->
                        val x = index * xSpacing
                        val y = size.height - ((point.value - minValue) / valueRange * size.height * animatedProgress.value)
                        
                        drawCircle(
                            color = pointColor,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
                
                // Draw selected point tooltip
                selectedPointIndex?.let { index ->
                    if (index in dataPoints.indices) {
                        val point = dataPoints[index]
                        val x = index * xSpacing
                        val y = size.height - ((point.value - minValue) / valueRange * size.height)
                        
                        // Highlight selected point
                        drawCircle(
                            color = MaterialTheme.colorScheme.tertiary,
                            radius = 6.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
            
            // Tooltip
            selectedPointIndex?.let { index ->
                if (index in chartData.dataPoints.indices) {
                    val point = chartData.dataPoints[index]
                    val xPosition = (index.toFloat() / (chartData.dataPoints.size - 1)) * 100
                    
                    Card(
                        modifier = Modifier
                            .align(
                                when {
                                    xPosition < 30 -> Alignment.TopStart
                                    xPosition > 70 -> Alignment.TopEnd
                                    else -> Alignment.TopCenter
                                }
                            )
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = point.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = point.value.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            chartData.dataPoints.forEachIndexed { index, point ->
                Text(
                    text = point.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
