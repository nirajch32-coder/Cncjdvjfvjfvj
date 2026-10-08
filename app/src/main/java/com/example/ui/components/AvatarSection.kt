package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.VoiceState
import com.example.ui.theme.BorderGlowCyan
import com.example.ui.theme.BorderGlowPurple
import com.example.ui.theme.LiveGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SaraCardBg
import com.example.ui.theme.SpeakingGreen
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AvatarSection(
    voiceState: VoiceState,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")

    // Breathing scale animation
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_breathing"
    )

    // Outer ring rotation
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_ring_rotation"
    )

    // Counter-rotation for inner orbital track
    val counterRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_rotation"
    )

    // Soft pulsating halo glow
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val isSpeaking = voiceState == VoiceState.SPEAKING
    val isListening = voiceState == VoiceState.LISTENING
    val isProcessing = voiceState == VoiceState.PROCESSING

    val primaryRingColor = when {
        isSpeaking -> SpeakingGreen
        isListening -> NeonCyan
        isProcessing -> Color(0xFF38BDF8)
        else -> NeonPurple
    }

    val secondaryRingColor = when {
        isSpeaking -> NeonCyan
        isListening -> Color(0xFF67E8F9)
        isProcessing -> NeonPurple
        else -> Color(0xFFD946EF)
    }

    Column(
        modifier = modifier.testTag("avatar_section"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Avatar with concentric glowing animated rings
        Box(
            modifier = Modifier
                .size(205.dp)
                .scale(if (isSpeaking || isListening) breathingScale * 1.02f else breathingScale),
            contentAlignment = Alignment.Center
        ) {
            // Ambient background glow halo
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryRingColor.copy(alpha = glowAlpha * 0.45f),
                                secondaryRingColor.copy(alpha = glowAlpha * 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Canvas for futuristic sci-fi concentric orbit rings & particles
            Canvas(
                modifier = Modifier
                    .size(205.dp)
                    .rotate(ringRotation)
            ) {
                val radiusOuter = size.minDimension / 2f - 4.dp.toPx()
                val radiusMiddle = size.minDimension / 2f - 14.dp.toPx()

                // Outer animated ring with gradient sweep stroke
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryRingColor.copy(alpha = 0.95f),
                            secondaryRingColor.copy(alpha = 0.5f),
                            primaryRingColor.copy(alpha = 0.15f),
                            secondaryRingColor.copy(alpha = 0.85f),
                            primaryRingColor.copy(alpha = 0.95f)
                        )
                    ),
                    radius = radiusOuter,
                    style = Stroke(
                        width = if (isSpeaking) 4.dp.toPx() else 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                // Concentric middle dashed orbit
                drawCircle(
                    color = primaryRingColor.copy(alpha = 0.35f),
                    radius = radiusMiddle,
                    style = Stroke(
                        width = 1.2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 14f), 0f)
                    )
                )

                // Orbital particle dots
                val dotCount = 8
                for (i in 0 until dotCount) {
                    val angle = (i * (360f / dotCount)) * (Math.PI / 180f)
                    val x = center.x + (radiusOuter * cos(angle)).toFloat()
                    val y = center.y + (radiusOuter * sin(angle)).toFloat()
                    drawCircle(
                        color = if (i % 2 == 0) primaryRingColor else secondaryRingColor,
                        radius = 2.2.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            // Counter-rotating subtle cyber accents
            Canvas(
                modifier = Modifier
                    .size(190.dp)
                    .rotate(counterRotation)
            ) {
                val r = size.minDimension / 2f - 10.dp.toPx()
                for (i in 0 until 4) {
                    val angle = (i * 90f + 45f) * (Math.PI / 180f)
                    val x = center.x + (r * cos(angle)).toFloat()
                    val y = center.y + (r * sin(angle)).toFloat()
                    drawCircle(
                        color = secondaryRingColor.copy(alpha = 0.7f),
                        radius = 1.8.dp.toPx(),
                        center = Offset(x, y)
                    )
                }
            }

            // Inner Ring Border with shadow
            Box(
                modifier = Modifier
                    .size(164.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(primaryRingColor, secondaryRingColor)
                        ),
                        shape = CircleShape
                    )
                    .shadow(18.dp, CircleShape, spotColor = primaryRingColor),
                contentAlignment = Alignment.Center
            ) {
                // Large circular anime-style female AI assistant avatar image
                Image(
                    painter = painterResource(id = R.drawable.sara_avatar),
                    contentDescription = "SARA AI Assistant Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .testTag("sara_avatar_image")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Small Status Pill under avatar
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SaraCardBg.copy(alpha = 0.92f))
                .border(
                    width = 1.dp,
                    color = if (isSpeaking) BorderGlowCyan else BorderGlowPurple,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 5.5.dp)
                .testTag("status_pill"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Glowing status dot
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSpeaking -> SpeakingGreen
                                isListening -> NeonCyan
                                isProcessing -> Color(0xFF38BDF8)
                                else -> LiveGreen
                            }
                        )
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = statusText,
                    color = when {
                        isSpeaking -> SpeakingGreen
                        isListening -> NeonCyan
                        isProcessing -> Color(0xFF38BDF8)
                        else -> Color.White
                    },
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
