package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VoiceState
import com.example.ui.theme.BorderGlowCyan
import com.example.ui.theme.BorderGlowPurple
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SaraCardBg
import com.example.ui.theme.SpeakingGreen

@Composable
fun GlowingMicButton(
    voiceState: VoiceState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isListening = voiceState == VoiceState.LISTENING
    val isSpeaking = voiceState == VoiceState.SPEAKING

    val transition = rememberInfiniteTransition(label = "mic_glow")
    val pulseScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening || isSpeaking) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 750 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    val outerGlowAlpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = if (isListening) 0.85f else 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 700 else 1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "outer_glow"
    )

    val buttonColor = when {
        isSpeaking -> SpeakingGreen
        isListening -> NeonCyan
        else -> NeonPurple
    }

    Box(
        modifier = modifier
            .size(82.dp)
            .testTag("microphone_button"),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing neon halo ring
        Box(
            modifier = Modifier
                .size(78.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            buttonColor.copy(alpha = outerGlowAlpha),
                            buttonColor.copy(alpha = outerGlowAlpha * 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Middle circular button with glossy gradient and shadow
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(buttonColor, Color(0xFFC084FC))
                    ),
                    shape = CircleShape
                )
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            buttonColor.copy(alpha = 0.9f),
                            Color(0xFF5B21B6)
                        )
                    )
                )
                .shadow(18.dp, CircleShape, spotColor = buttonColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = Color.White),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Microphone Toggle Button",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun ActionPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isActive: Boolean = false,
    testTag: String = "action_pill"
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 40.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (isActive) Brush.horizontalGradient(listOf(NeonPurple.copy(alpha = 0.4f), Color(0xFF6B21A8).copy(alpha = 0.4f)))
                else Brush.horizontalGradient(listOf(SaraCardBg, Color(0xFF1C0F35)))
            )
            .border(
                width = 1.dp,
                color = if (isActive) BorderGlowPurple else CardBorderSubtle,
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NeonPurple),
                onClick = onClick
            )
            .padding(horizontal = 13.dp, vertical = 7.5.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) NeonCyan else Color(0xFFE9D5FF),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
fun BottomNavPill(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    testTag: String = "bottom_nav_pill"
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 46.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SaraCardBg.copy(alpha = 0.9f))
            .border(
                width = 1.dp,
                color = if (isActive) BorderGlowCyan else BorderGlowPurple,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = NeonPurple),
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isActive) NeonCyan else Color(0xFFC084FC),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = if (isActive) NeonCyan else Color.White,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}
