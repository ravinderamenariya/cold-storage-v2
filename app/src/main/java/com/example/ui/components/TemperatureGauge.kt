package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VxTheme
import kotlin.math.abs

/**
 * Reusable Circular Arc Temperature Gauge Component
 * Built using Jetpack Compose Canvas and DrawScope.
 *
 * Features:
 * - 260° circular track arc with smooth rounded stroke caps
 * - Live active temperature sweep arc color-coded by target tolerance
 * - Target setpoint calibration notch/tick drawn on the track
 * - Precision indicator dot at the tip of the sweep arc
 * - Fully theme-aware in both Light & Dark modes
 * - Compact & standard display modes for dashboards, grids, and chamber cards
 */
@Composable
fun TemperatureGauge(
    temperature: Double,
    targetTemp: Double = 0.0,
    title: String? = null,
    minTemp: Double = -10.0,
    maxTemp: Double = 40.0,
    unit: String = "°C",
    size: Dp = 110.dp,
    strokeWidth: Dp = 9.dp,
    showTarget: Boolean = true,
    compact: Boolean = false,
    accentColor: Color? = null,
    modifier: Modifier = Modifier
) {
    val clampedTemp = temperature.coerceIn(minTemp, maxTemp)
    val totalRange = (maxTemp - minTemp).coerceAtLeast(1.0)
    val fraction = ((clampedTemp - minTemp) / totalRange).toFloat().coerceIn(0f, 1f)

    val startAngle = 140f
    val sweepAngle = 260f
    val currentSweep = (sweepAngle * fraction).coerceAtLeast(1f)

    // Dynamic color coding based on deviation from target temperature
    val gaugeColor = accentColor ?: when {
        abs(temperature - targetTemp) <= 1.5 -> VxTheme.success
        temperature > targetTemp + 3.0 -> VxTheme.warningAmber
        temperature < targetTemp - 3.0 -> VxTheme.primaryBlue
        else -> VxTheme.primaryBlue
    }

    val trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
    val targetTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subtextColor = MaterialTheme.colorScheme.onSurfaceVariant

    val targetFraction = if (showTarget && targetTemp in minTemp..maxTemp) {
        ((targetTemp.coerceIn(minTemp, maxTemp) - minTemp) / totalRange).toFloat()
    } else null

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val stroke = Stroke(width = strokePx, cap = StrokeCap.Round)
            val diameter = size.toPx() - strokePx
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)
            val arcSize = Size(diameter, diameter)
            val radius = diameter / 2f
            val cx = size.toPx() / 2f
            val cy = size.toPx() / 2f

            // 1. Background Track Arc
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            // 2. Target Setpoint Tick (Calibration Notch)
            if (targetFraction != null) {
                val targetAngleDeg = startAngle + sweepAngle * targetFraction
                val targetAngleRad = Math.toRadians(targetAngleDeg.toDouble())
                val cosTarget = Math.cos(targetAngleRad).toFloat()
                val sinTarget = Math.sin(targetAngleRad).toFloat()

                val rInner = radius - strokePx * 0.45f
                val rOuter = radius + strokePx * 0.45f
                drawLine(
                    color = targetTickColor,
                    start = Offset(cx + rInner * cosTarget, cy + rInner * sinTarget),
                    end = Offset(cx + rOuter * cosTarget, cy + rOuter * sinTarget),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 3. Active Temperature Value Arc
            drawArc(
                color = gaugeColor,
                startAngle = startAngle,
                sweepAngle = currentSweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            // 4. Tip Indicator Dot on leading edge
            val tipAngleDeg = startAngle + currentSweep
            val tipAngleRad = Math.toRadians(tipAngleDeg.toDouble())
            val tipX = cx + radius * Math.cos(tipAngleRad).toFloat()
            val tipY = cy + radius * Math.sin(tipAngleRad).toFloat()

            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = strokePx * 0.22f,
                center = Offset(tipX, tipY)
            )
        }

        // Center Readout Information
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    fontSize = if (compact) 9.sp else 11.sp,
                    color = subtextColor,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "%.1f%s".format(temperature, unit),
                fontSize = if (compact) 16.sp else 19.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            if (showTarget) {
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = "लक्ष्य ${targetTemp.toInt()}$unit",
                    fontSize = if (compact) 9.sp else 10.sp,
                    color = gaugeColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
