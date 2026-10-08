package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.VoiceState
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SpeakingGreen
import kotlin.math.sin

@Composable
fun AudioWaveform(
    voiceState: VoiceState,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val barCount = 20
    val transition = rememberInfiniteTransition(label = "waveform_anim")

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveform_phase"
    )

    val isSpeaking = voiceState == VoiceState.SPEAKING
    val isListening = voiceState == VoiceState.LISTENING

    val barBrush = when {
        isSpeaking -> Brush.verticalGradient(
            colors = listOf(SpeakingGreen, NeonCyan)
        )
        isListening -> Brush.verticalGradient(
            colors = listOf(NeonCyan, Color(0xFF38BDF8))
        )
        else -> Brush.verticalGradient(
            colors = listOf(NeonPurple, Color(0xFF6D28D9))
        )
    }

    Row(
        modifier = modifier
            .height(26.dp)
            .testTag("audio_waveform"),
        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val offset = (i.toFloat() / barCount) * (2 * Math.PI).toFloat()
            val wave = (sin(phase + offset) + 1f) / 2f // 0f to 1f

            val baseHeight = when {
                isSpeaking -> (6f + wave * 20f * amplitude.coerceIn(0.4f, 1.2f)).dp
                isListening -> (4f + wave * 17f * amplitude.coerceIn(0.3f, 1.0f)).dp
                else -> (3.5f + wave * 5.5f).dp
            }

            Box(
                modifier = Modifier
                    .width(2.8.dp)
                    .height(baseHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barBrush)
            )
        }
    }
}
