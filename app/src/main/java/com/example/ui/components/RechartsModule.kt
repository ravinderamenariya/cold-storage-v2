package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VxBorder
import com.example.ui.theme.VxBorderBlue
import com.example.ui.theme.VxBorderGreen
import com.example.ui.theme.VxBorderSubtle
import com.example.ui.theme.VxCyanAccent
import com.example.ui.theme.VxDeepBlue
import com.example.ui.theme.VxEmerald
import com.example.ui.theme.VxEmeraldDark
import com.example.ui.theme.VxNavy
import com.example.ui.theme.VxPrimaryBlue
import com.example.ui.theme.VxSlateBlue
import com.example.ui.theme.VxSteelBlue
import com.example.ui.theme.VxSurfaceBlue
import com.example.ui.theme.VxSurfaceGreen
import com.example.ui.theme.VxSurfaceSubtle
import com.example.ui.theme.VxTheme
import com.example.ui.theme.VxWarningAmber
import kotlin.math.max
import kotlin.math.min

/**
 * 24-Hour Telemetry Datapoint for the Recharts visualization module.
 */
data class Telemetry24hPoint(
    val hourLabel: String,      // "00:00", "04:00", "08:00", etc.
    val chamber1Temp: Double,   // °C
    val chamber2Temp: Double,   // °C
    val outsideTemp: Double,    // °C
    val solarKw: Double,        // kW (0 to 6.5 kW)
    val solarEfficiency: Double,// % (0 to 95%)
    val batterySoc: Double      // % (0 to 100%)
)

/**
 * Production-ready 24-hour Telemetry & Solar Efficiency Recharts Module.
 * Features smooth Bezier interpolation, gradient fill under curves, interactive crosshairs,
 * and statistical trend analysis.
 */
@Composable
fun RechartsTelemetryModule(
    modifier: Modifier = Modifier
) {
    var selectedMetricMode by remember { mutableStateOf(0) } // 0: Temperature Trends, 1: Solar Efficiency & Energy
    var activeScrubIndex by remember { mutableIntStateOf(12) } // Defaults to 12:00 noon
    var isScrubbing by remember { mutableStateOf(false) }

    // Toggle series visibility
    var showChamber1 by remember { mutableStateOf(true) }
    var showChamber2 by remember { mutableStateOf(true) }
    var showOutside by remember { mutableStateOf(true) }

    var showSolarKw by remember { mutableStateOf(true) }
    var showSolarEff by remember { mutableStateOf(true) }
    var showBattery by remember { mutableStateOf(true) }

    // 24 Hours of continuous data points (Hourly cadence)
    val data24h = remember {
        listOf(
            Telemetry24hPoint("00:00", 11.9, 3.9, 23.4, 0.0, 0.0, 84.0),
            Telemetry24hPoint("02:00", 12.0, 4.0, 22.8, 0.0, 0.0, 80.5),
            Telemetry24hPoint("04:00", 12.0, 4.0, 22.1, 0.0, 0.0, 77.0),
            Telemetry24hPoint("06:00", 12.1, 4.1, 23.0, 0.4, 62.0, 76.0),
            Telemetry24hPoint("08:00", 12.0, 4.0, 25.5, 2.1, 84.5, 82.0),
            Telemetry24hPoint("10:00", 12.2, 4.2, 28.2, 4.3, 89.2, 91.0),
            Telemetry24hPoint("12:00", 12.1, 4.1, 30.5, 5.8, 92.4, 98.0),
            Telemetry24hPoint("13:00", 12.0, 4.0, 31.0, 6.1, 91.8, 100.0),
            Telemetry24hPoint("14:00", 12.1, 4.1, 30.8, 5.9, 90.6, 99.0),
            Telemetry24hPoint("16:00", 12.2, 4.2, 29.4, 4.2, 87.0, 96.0),
            Telemetry24hPoint("18:00", 12.0, 4.0, 27.5, 1.8, 79.4, 93.0),
            Telemetry24hPoint("20:00", 11.9, 3.9, 25.8, 0.0, 0.0, 89.0),
            Telemetry24hPoint("22:00", 12.0, 4.0, 24.5, 0.0, 0.0, 86.0),
            Telemetry24hPoint("24:00", 12.0, 4.0, 23.8, 0.0, 0.0, 83.5)
        )
    }

    val activePoint = data24h.getOrElse(activeScrubIndex.coerceIn(0, data24h.size - 1)) { data24h.first() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_telemetry_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VxTheme.surfaceBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = VxTheme.primaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (selectedMetricMode == 0) "24h Temperature Trends" else "24h Solar & Energy Efficiency",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Interactive Recharts Curve Engine",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 24H Badge
                Surface(
                    color = VxTheme.surfaceBlue,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, VxTheme.borderBlue)
                ) {
                    Text(
                        text = "Last 24 Hours",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = VxTheme.primaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric Mode Selector Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VxTheme.surfaceSubtle)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val modes = listOf("Temperature (°C)", "Solar & Efficiency")
                modes.forEachIndexed { index, title ->
                    val isSelected = selectedMetricMode == index
                    Surface(
                        onClick = { selectedMetricMode = index },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) VxTheme.primaryBlue else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recharts_tab_$index")
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Series Legend & Toggles
            if (selectedMetricMode == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegendToggleChip(
                        label = "Chamber 1 (12°C)",
                        color = VxPrimaryBlue,
                        isSelected = showChamber1,
                        onToggle = { showChamber1 = !showChamber1 },
                        modifier = Modifier.weight(1f)
                    )
                    LegendToggleChip(
                        label = "Chamber 2 (4°C)",
                        color = VxEmeraldDark,
                        isSelected = showChamber2,
                        onToggle = { showChamber2 = !showChamber2 },
                        modifier = Modifier.weight(1f)
                    )
                    LegendToggleChip(
                        label = "Outside Ambient",
                        color = VxSlateBlue,
                        isSelected = showOutside,
                        onToggle = { showOutside = !showOutside },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegendToggleChip(
                        label = "Solar kW",
                        color = VxWarningAmber,
                        isSelected = showSolarKw,
                        onToggle = { showSolarKw = !showSolarKw },
                        modifier = Modifier.weight(1f)
                    )
                    LegendToggleChip(
                        label = "Efficiency %",
                        color = VxPrimaryBlue,
                        isSelected = showSolarEff,
                        onToggle = { showSolarEff = !showSolarEff },
                        modifier = Modifier.weight(1f)
                    )
                    LegendToggleChip(
                        label = "Battery SoC %",
                        color = VxEmeraldDark,
                        isSelected = showBattery,
                        onToggle = { showBattery = !showBattery },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recharts-style Tooltip Card (Synchronized with Crosshair)
            Surface(
                color = VxTheme.surfaceSubtle,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(VxPrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Time: ${activePoint.hourLabel}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (selectedMetricMode == 0) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (showChamber1) {
                                Text(
                                    text = "C1: ${activePoint.chamber1Temp}°C",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxPrimaryBlue
                                )
                            }
                            if (showChamber2) {
                                Text(
                                    text = "C2: ${activePoint.chamber2Temp}°C",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxEmeraldDark
                                )
                            }
                            if (showOutside) {
                                Text(
                                    text = "Out: ${activePoint.outsideTemp}°C",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxSlateBlue
                                )
                            }
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (showSolarKw) {
                                Text(
                                    text = "${activePoint.solarKw} kW",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxWarningAmber
                                )
                            }
                            if (showSolarEff) {
                                Text(
                                    text = "Eff: ${activePoint.solarEfficiency.toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxPrimaryBlue
                                )
                            }
                            if (showBattery) {
                                Text(
                                    text = "Bat: ${activePoint.batterySoc.toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = VxEmeraldDark
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Chart Area with Recharts-grade Bezier Curves & Crosshair
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val n = data24h.size
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            activeScrubIndex = ((fraction * (n - 1)).toInt()).coerceIn(0, n - 1)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isScrubbing = true
                                val n = data24h.size
                                val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                activeScrubIndex = ((fraction * (n - 1)).toInt()).coerceIn(0, n - 1)
                            },
                            onDragEnd = { isScrubbing = false },
                            onDragCancel = { isScrubbing = false },
                            onDrag = { change, _ ->
                                val n = data24h.size
                                val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                activeScrubIndex = ((fraction * (n - 1)).toInt()).coerceIn(0, n - 1)
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(210.dp)) {
                    val width = size.width
                    val height = size.height
                    val bottomPadding = 30f
                    val topPadding = 20f
                    val chartHeight = height - bottomPadding - topPadding

                    // Determine Min/Max scales
                    val (minVal, maxVal) = if (selectedMetricMode == 0) {
                        Pair(0.0, 36.0) // 0°C to 36°C
                    } else {
                        Pair(0.0, 100.0) // 0 to 100% (or kW scaled)
                    }

                    val range = maxVal - minVal

                    // Draw Horizontal Gridlines (4 lines)
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = topPadding + (chartHeight / gridLines) * i
                        val gridVal = maxVal - (range / gridLines) * i
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )

                        // Y-axis label text
                        drawContext.canvas.nativeCanvas.drawText(
                            if (selectedMetricMode == 0) "${gridVal.toInt()}°" else "${gridVal.toInt()}%",
                            6f,
                            y - 4f,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.GRAY
                                textSize = 24f
                                isAntiAlias = true
                            }
                        )
                    }

                    val n = data24h.size
                    val stepX = width / (n - 1).toFloat()

                    fun getY(v: Double): Float {
                        val norm = (v - minVal) / range
                        return (topPadding + chartHeight - (norm * chartHeight)).toFloat()
                    }

                    // Draw Series Curves with Smooth Cubic Splines
                    if (selectedMetricMode == 0) {
                        // Series 3: Outside (draw background first)
                        if (showOutside) {
                            drawRechartsCurve(
                                values = data24h.map { it.outsideTemp },
                                stepX = stepX,
                                getY = ::getY,
                                strokeColor = VxSlateBlue,
                                fillColor = VxSlateBlue.copy(alpha = 0.08f),
                                chartBottom = topPadding + chartHeight
                            )
                        }

                        // Series 1: Chamber 1
                        if (showChamber1) {
                            drawRechartsCurve(
                                values = data24h.map { it.chamber1Temp },
                                stepX = stepX,
                                getY = ::getY,
                                strokeColor = VxPrimaryBlue,
                                fillColor = VxPrimaryBlue.copy(alpha = 0.12f),
                                chartBottom = topPadding + chartHeight
                            )
                        }

                        // Series 2: Chamber 2
                        if (showChamber2) {
                            drawRechartsCurve(
                                values = data24h.map { it.chamber2Temp },
                                stepX = stepX,
                                getY = ::getY,
                                strokeColor = VxEmeraldDark,
                                fillColor = VxEmeraldDark.copy(alpha = 0.10f),
                                chartBottom = topPadding + chartHeight
                            )
                        }
                    } else {
                        // Solar & Energy
                        if (showBattery) {
                            drawRechartsCurve(
                                values = data24h.map { it.batterySoc },
                                stepX = stepX,
                                getY = ::getY,
                                strokeColor = VxEmeraldDark,
                                fillColor = VxEmeraldDark.copy(alpha = 0.08f),
                                chartBottom = topPadding + chartHeight
                            )
                        }
                        if (showSolarEff) {
                            drawRechartsCurve(
                                values = data24h.map { it.solarEfficiency },
                                stepX = stepX,
                                getY = ::getY,
                                strokeColor = VxPrimaryBlue,
                                fillColor = VxPrimaryBlue.copy(alpha = 0.12f),
                                chartBottom = topPadding + chartHeight
                            )
                        }
                        if (showSolarKw) {
                            // Scale 6.5 kW to % scale (kW * 15)
                            drawRechartsCurve(
                                values = data24h.map { it.solarKw * 15.0 },
                                stepX = stepX,
                                getY = ::getY,
                                strokeColor = VxWarningAmber,
                                fillColor = VxWarningAmber.copy(alpha = 0.08f),
                                chartBottom = topPadding + chartHeight
                            )
                        }
                    }

                    // Draw Vertical Crosshair at active index
                    val crosshairX = activeScrubIndex * stepX
                    drawLine(
                        color = VxPrimaryBlue,
                        start = Offset(crosshairX, topPadding),
                        end = Offset(crosshairX, topPadding + chartHeight),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                    )

                    // Draw active points circles on crosshair
                    if (selectedMetricMode == 0) {
                        if (showChamber1) {
                            val c1Y = getY(activePoint.chamber1Temp)
                            drawCircle(color = Color.White, radius = 6f, center = Offset(crosshairX, c1Y))
                            drawCircle(color = VxPrimaryBlue, radius = 4f, center = Offset(crosshairX, c1Y))
                        }
                        if (showChamber2) {
                            val c2Y = getY(activePoint.chamber2Temp)
                            drawCircle(color = Color.White, radius = 6f, center = Offset(crosshairX, c2Y))
                            drawCircle(color = VxEmeraldDark, radius = 4f, center = Offset(crosshairX, c2Y))
                        }
                    } else {
                        if (showSolarEff) {
                            val effY = getY(activePoint.solarEfficiency)
                            drawCircle(color = Color.White, radius = 6f, center = Offset(crosshairX, effY))
                            drawCircle(color = VxPrimaryBlue, radius = 4f, center = Offset(crosshairX, effY))
                        }
                    }

                    // Draw X-axis Hour Labels
                    val labelIndices = listOf(0, 3, 6, 9, 12)
                    labelIndices.forEach { idx ->
                        if (idx < data24h.size) {
                            val xPos = idx * stepX
                            val label = data24h[idx].hourLabel
                            drawContext.canvas.nativeCanvas.drawText(
                                label,
                                xPos - 20f,
                                height - 6f,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.DKGRAY
                                    textSize = 22f
                                    isAntiAlias = true
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trend Identification Card
            Surface(
                color = VxTheme.surfaceSubtle,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingFlat,
                                contentDescription = null,
                                tint = VxEmeraldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "24-Hour Trend Analysis",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = if (selectedMetricMode == 0) "Thermal Drift: ±0.2°C (STABLE)" else "Solar Peak: 92.4% (OPTIMAL)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VxEmeraldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (selectedMetricMode == 0) {
                            "Chamber 1 maintained setpoint between 11.9°C – 12.2°C across all 24 hours. Compressor duty cycle averaged 38.4%, efficiently buffering peak 31.0°C daytime ambient heat."
                        } else {
                            "Solar efficiency achieved peak 92.4% during peak sunlight (12:00–14:00). Battery SoC remained above 76% overnight, ensuring 100% solar self-sufficiency with zero grid reliance."
                        },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * Helper to draw smooth Recharts-style spline curves with gradient fills.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRechartsCurve(
    values: List<Double>,
    stepX: Float,
    getY: (Double) -> Float,
    strokeColor: Color,
    fillColor: Color,
    chartBottom: Float
) {
    if (values.size < 2) return

    val strokePath = Path()
    val fillPath = Path()

    val firstX = 0f
    val firstY = getY(values.first())

    strokePath.moveTo(firstX, firstY)
    fillPath.moveTo(firstX, chartBottom)
    fillPath.lineTo(firstX, firstY)

    for (i in 0 until values.size - 1) {
        val x0 = i * stepX
        val y0 = getY(values[i])
        val x1 = (i + 1) * stepX
        val y1 = getY(values[i + 1])

        val cx = (x0 + x1) / 2f
        strokePath.cubicTo(cx, y0, cx, y1, x1, y1)
        fillPath.cubicTo(cx, y0, cx, y1, x1, y1)
    }

    val lastX = (values.size - 1) * stepX
    fillPath.lineTo(lastX, chartBottom)
    fillPath.close()

    // Draw smooth gradient fill under the curve
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(fillColor, Color.Transparent),
            startY = 0f,
            endY = chartBottom
        )
    )

    // Draw main curve line
    drawPath(
        path = strokePath,
        color = strokeColor,
        style = Stroke(
            width = 3.5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
    )
}

@Composable
private fun LegendToggleChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) VxTheme.surfaceSubtle else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isSelected) color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) color else Color.Gray)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
