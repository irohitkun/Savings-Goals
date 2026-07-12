package com.rohit.savingsgoals.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Full-circle progress ring, used in the hero summary card.
 */
@Composable
fun CircularProgressRing(
    progress: Float,
    size: Dp,
    strokeWidth: Dp = 6.dp,
    trackColor: Color,
    progressColor: Color,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val inset = strokeWidth.toPx() / 2
            val arcSize = Size(size.toPx() - strokeWidth.toPx(), size.toPx() - strokeWidth.toPx())
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        content()
    }
}

/**
 * Speedometer-style semi-circle gauge (180°), used on the goal detail screen.
 * The icon/avatar sits centered beneath the arc. Height is derived from the
 * diameter so the dome always fits its bounding box exactly.
 */
@Composable
fun SemiCircleGauge(
    progress: Float,
    diameter: Dp,
    strokeWidth: Dp = 14.dp,
    trackColor: Color,
    progressColor: Color,
    content: @Composable () -> Unit
) {
    val radius = diameter / 2
    val canvasHeight = radius + strokeWidth

    Box(
        modifier = Modifier.size(width = diameter, height = canvasHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(modifier = Modifier.size(width = diameter, height = canvasHeight)) {
            val stroke = strokeWidth.toPx()
            val inset = stroke / 2
            val arcDiameter = diameter.toPx() - stroke
            val arcSize = Size(arcDiameter, arcDiameter)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = progressColor,
                startAngle = 180f,
                sweepAngle = 180f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Box(modifier = Modifier.padding(bottom = 2.dp)) {
            content()
        }
    }
}
