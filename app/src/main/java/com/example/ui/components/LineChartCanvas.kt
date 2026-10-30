package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxPurple
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxTextMuted
import com.example.ui.theme.VxTextSecondary
import com.example.ui.theme.VxTheme

data class ChartPoint(
    val label: String,
    val chamber1: Double,
    val chamber2: Double,
    val outside: Double
)

/**
 * Reusable Temperature & Sensor Overview Line Chart with Time Range selector and touch tooltip.
 */
@Composable
fun TemperatureOverviewChart(
    title: String = "Temperature Overview",
    selectedInterval: String = "24H",
    onIntervalSelected: (String) -> Unit = {},
    showChamber1: Boolean = true,
    showChamber2: Boolean = true,
    showOutside: Boolean = true,
    modifier: Modifier = Modifier
) {
    // Generate realistic data series based on interval
    val points = remember(selectedInterval) {
        when (selectedInterval) {
            "7D" -> listOf(
                ChartPoint("02 Oct", 11.9, 3.8, 27.2),
                ChartPoint("03 Oct", 12.1, 4.0, 28.5),
                ChartPoint("04 Oct", 12.0, 4.2, 29.1),
                ChartPoint("05 Oct", 12.3, 3.9, 27.8),
                ChartPoint("06 Oct", 12.0, 4.1, 28.0),
                ChartPoint("07 Oct", 12.1, 4.0, 28.4),
                ChartPoint("08 Oct", 12.0, 4.0, 28.0)
            )
            "30D" -> listOf(
                ChartPoint("W1", 12.1, 4.0, 26.5),
                ChartPoint("W2", 12.0, 3.9, 27.8),
                ChartPoint("W3", 12.2, 4.1, 28.6),
                ChartPoint("W4", 12.0, 4.0, 28.1)
            )
            else -> listOf( // 24H (1D)
                ChartPoint("00:00", 11.8, 3.9, 24.2),
                ChartPoint("04:00", 12.0, 4.0, 23.8),
                ChartPoint("08:00", 12.1, 4.1, 26.5),
                ChartPoint("12:00", 12.2, 4.2, 29.8),
                ChartPoint("16:00", 12.1, 4.1, 29.1),
                ChartPoint("20:00", 12.0, 4.0, 27.5),
                ChartPoint("24:00", 12.0, 4.0, 25.8)
            )
        }
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("temperature_overview_chart_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with Title and Interval Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Interval Pills: 24H, 7D, 30D
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(VxTheme.surfaceSubtle)
                        .padding(2.dp)
                ) {
                    listOf("24H", "7D", "30D").forEach { interval ->
                        val isSelected = interval == selectedInterval || (interval == "24H" && selectedInterval == "1D")
                        Surface(
                            onClick = { onIntervalSelected(interval) },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) VxTheme.primaryBlue else Color.Transparent,
                            modifier = Modifier.testTag("chart_interval_$interval")
                        ) {
                            Text(
                                text = interval,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Series Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showChamber1) {
                    LegendItem(color = VxTheme.primaryBlue, label = "Chamber 1")
                    Spacer(modifier = Modifier.width(16.dp))
                }
                if (showChamber2) {
                    LegendItem(color = VxTheme.emeraldDark, label = "Chamber 2")
                    Spacer(modifier = Modifier.width(16.dp))
                }
                if (showOutside) {
                    LegendItem(color = VxTheme.purple, label = "Outside")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tooltip if selected
            if (selectedIndex != null && selectedIndex in points.indices) {
                val pt = points[selectedIndex!!]
                Surface(
                    color = VxTheme.surfaceSubtle,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = pt.label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row {
                            if (showChamber1) {
                                Text(
                                    text = "C1: ${pt.chamber1}°C  ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxTheme.primaryBlue
                                )
                            }
                            if (showChamber2) {
                                Text(
                                    text = "C2: ${pt.chamber2}°C  ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxTheme.emeraldDark
                                )
                            }
                            if (showOutside) {
                                Text(
                                    text = "Out: ${pt.outside}°C",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxTheme.purple
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Canvas Line Chart
            val minTemp = 0.0
            val maxTemp = 32.0

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .pointerInput(points) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val step = width / (points.size - 1).coerceAtLeast(1)
                                val index = ((offset.x / step).toInt()).coerceIn(0, points.size - 1)
                                selectedIndex = index
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 30f
                    val topPadding = 15f
                    val chartHeight = h - bottomPadding - topPadding

                    // Horizontal Gridlines (0°C, 10°C, 20°C, 30°C)
                    val gridValues = listOf(0.0, 10.0, 20.0, 30.0)
                    for (gv in gridValues) {
                        val y = topPadding + (chartHeight * (1.0 - (gv - minTemp) / (maxTemp - minTemp))).toFloat()
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    fun getY(temp: Double): Float {
                        val norm = ((temp - minTemp) / (maxTemp - minTemp)).coerceIn(0.0, 1.0)
                        return (topPadding + chartHeight * (1.0 - norm)).toFloat()
                    }

                    val stepX = w / (points.size - 1).coerceAtLeast(1)

                    fun drawSeries(values: List<Double>, color: Color) {
                        val path = Path()
                        values.forEachIndexed { i, v ->
                            val x = i * stepX
                            val y = getY(v)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        drawPath(
                            path = path,
                            color = color,
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )

                        // Data Points
                        values.forEachIndexed { i, v ->
                            val x = i * stepX
                            val y = getY(v)
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = Offset(x, y)
                            )
                            drawCircle(
                                color = color,
                                radius = 2.5.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                    }

                    if (showOutside) {
                        drawSeries(points.map { it.outside }, VxPurple)
                    }
                    if (showChamber1) {
                        drawSeries(points.map { it.chamber1 }, VxPrimaryBlue)
                    }
                    if (showChamber2) {
                        drawSeries(points.map { it.chamber2 }, VxEmerald)
                    }

                    // Vertical highlight if user tapped
                    selectedIndex?.let { idx ->
                        if (idx in points.indices) {
                            val x = idx * stepX
                            drawLine(
                                color = VxPrimaryBlue.copy(alpha = 0.5f),
                                start = Offset(x, topPadding),
                                end = Offset(x, h - bottomPadding),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }
                    }
                }
            }

            // X-Axis Labels Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                points.forEach { pt ->
                    Text(
                        text = pt.label,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
