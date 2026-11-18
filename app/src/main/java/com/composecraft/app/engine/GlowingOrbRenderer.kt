package com.composecraft.app.engine

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composecraft.app.model.ElementProperties
import kotlin.math.sin

@Composable
fun GlowingOrbRenderer(
    properties: ElementProperties,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbInfiniteTransition")

    // Breathing pulse scale animation
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = properties.animationSpeedMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbPulseScale"
    )

    // Continuous 360 degree rotation for the 3D gradient illusion
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = properties.animationSpeedMs * 3, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotation"
    )

    // Outer glow aura breathing alpha
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (properties.animationSpeedMs * 1.2).toInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraAlpha"
    )

    val primaryColor = parseHexColor(properties.orbColorPrimaryHex, Color(0xFF00F2FE))
    val secondaryColor = parseHexColor(properties.orbColorSecondaryHex, Color(0xFF7F00FF))
    val glowColor = parseHexColor(properties.orbGlowColorHex, Color(0xFF4FACFE))

    val sizeDp = properties.orbSizeDp.dp

    Box(
        modifier = modifier
            .size(sizeDp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.72f * pulseScale

            // 1. Layer 1: Outer Neon Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = auraAlpha),
                        primaryColor.copy(alpha = auraAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.5f
                ),
                radius = baseRadius * 1.5f,
                center = center
            )

            // 2. Layer 2: Main 3D Sphere Body with multi-color radial gradient
            val radAngle = Math.toRadians(rotationAngle.toDouble())
            val lightSourceOffset = Offset(
                x = center.x + (baseRadius * 0.35f * kotlin.math.cos(radAngle)).toFloat(),
                y = center.y + (baseRadius * 0.35f * kotlin.math.sin(radAngle)).toFloat()
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        primaryColor,
                        secondaryColor,
                        Color(0xFF0F172A)
                    ),
                    center = lightSourceOffset,
                    radius = baseRadius * 1.1f
                ),
                radius = baseRadius,
                center = center
            )

            // 3. Layer 3: 3D Specular Highlight (creates glossy glass/sphere effect)
            val specularCenter = Offset(center.x - baseRadius * 0.32f, center.y - baseRadius * 0.32f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color.White.copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = specularCenter,
                    radius = baseRadius * 0.45f
                ),
                radius = baseRadius * 0.45f,
                center = specularCenter
            )

            // 4. Layer 4: Inner Core Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.6f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 0.6f
                ),
                radius = baseRadius * 0.6f,
                center = center
            )
        }
    }
}

@Composable
fun LiveWaveformRenderer(
    properties: ElementProperties,
    modifier: Modifier = Modifier
) {
    val barCount = properties.waveformBars.coerceIn(8, 36)
    val waveColor = parseHexColor(properties.waveformColorHex, Color(0xFF00F2FE))
    val infiniteTransition = rememberInfiniteTransition(label = "WaveformTransition")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = properties.animationSpeedMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(properties.fixedHeightDp.dp)
            .padding(horizontal = 12.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = (width / (barCount * 2f)).coerceAtLeast(3f)
        val spacing = (width - (barCount * barWidth)) / (barCount - 1).coerceAtLeast(1)

        for (i in 0 until barCount) {
            val normalizedIndex = i.toFloat() / barCount
            // Compute animated wave height using harmonic sine waves
            val wave1 = sin(phase + normalizedIndex * 4.5f)
            val wave2 = sin(phase * 1.5f + normalizedIndex * 2.5f)
            val combined = (wave1 + wave2) / 2f // -1 .. 1
            val amplitude = 0.25f + ((combined + 1f) / 2f) * 0.70f // 0.25 .. 0.95

            val barHeight = height * amplitude
            val left = i * (barWidth + spacing)
            val top = (height - barHeight) / 2f

            drawRoundRect(
                color = waveColor.copy(alpha = 0.4f + amplitude * 0.6f),
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}
