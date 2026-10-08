package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manager.InteractionStep
import com.example.manager.WhatsAppStatus
import com.example.model.NavigationScreen
import com.example.model.VoiceState
import com.example.ui.components.ActionPillButton
import com.example.ui.components.AudioWaveform
import com.example.ui.components.AvatarSection
import com.example.ui.components.BottomNavPill
import com.example.ui.components.GlowingMicButton
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.LiveGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SaraBackground
import com.example.ui.theme.SaraCardBg
import com.example.ui.theme.SaraVoid
import com.example.ui.theme.TextMuted
import com.example.viewmodel.SaraViewModel

@Composable
fun MainScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    val voiceState by viewModel.voiceState.collectAsState()
    val statusText by viewModel.statusText.collectAsState()
    val amplitude by viewModel.amplitude.collectAsState()
    val whatsAppTask by viewModel.whatsAppTask.collectAsState()
    val interactionState by viewModel.interactionState.collectAsState()
    val context = LocalContext.current

    var quickTextInput by remember { mutableStateOf("") }
    var showQuickTextBar by remember { mutableStateOf(false) }

    val liveBadgeTransition = rememberInfiniteTransition(label = "live_pulse")
    val liveDotAlpha by liveBadgeTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SaraVoid,
                        SaraBackground,
                        Color(0xFF110726),
                        SaraVoid
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ================= TOP NAVIGATION =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: small "SETTINGS" button with gear icon
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SaraCardBg.copy(alpha = 0.9f))
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, color = NeonPurple),
                            onClick = { viewModel.navigateTo(NavigationScreen.SETTINGS_HUB) }
                        )
                        .padding(horizontal = 11.dp, vertical = 6.dp)
                        .testTag("settings_top_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            tint = NeonPurple,
                            modifier = Modifier.size(13.5.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "SETTINGS",
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Center: SARA branding in purple with "Live" status badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SARA",
                        color = NeonPurple,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.2.sp
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    // Small "Live" status badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D251B))
                            .border(1.dp, LiveGreen.copy(alpha = 0.55f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.5.dp)
                                    .clip(CircleShape)
                                    .background(LiveGreen.copy(alpha = liveDotAlpha))
                            )
                            Spacer(modifier = Modifier.width(3.5.dp))
                            Text(
                                text = "LIVE",
                                color = LiveGreen,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Right: Quick Navigation to Chat/Memory & Console
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(NavigationScreen.CHAT_MEMORY) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SaraCardBg)
                            .border(1.dp, CardBorderSubtle, CircleShape)
                            .testTag("chat_memory_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Chat and Memory Screen",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.navigateTo(NavigationScreen.COMPANION_CONSOLE) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(SaraCardBg)
                            .border(1.dp, CardBorderSubtle, CircleShape)
                            .testTag("companion_console_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Companion Console Screen",
                            tint = Color(0xFFC084FC),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ================= CENTER: AVATAR & RINGS =================
            AvatarSection(
                voiceState = voiceState,
                statusText = statusText,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ================= VOICE CONTROL AREA =================
            // Three small rounded buttons matching the reference exactly:
            // 1. "Play Haule Haule"
            // 2. "Simulate Scroll"
            // 3. "Simulate Click"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionPillButton(
                    text = "Play Haule Haule",
                    onClick = { viewModel.playHauleHaule() },
                    isActive = voiceState == VoiceState.SPEAKING,
                    testTag = "btn_play_haule"
                )
                ActionPillButton(
                    text = "Simulate Scroll",
                    onClick = { viewModel.simulateScroll() },
                    testTag = "btn_simulate_scroll"
                )
                ActionPillButton(
                    text = "Simulate Click",
                    onClick = { viewModel.simulateClick() },
                    testTag = "btn_simulate_click"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Below them: Small animated audio / wave indicator
            AudioWaveform(
                voiceState = voiceState,
                amplitude = amplitude,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // WhatsApp Message Confirmation Card
            if (whatsAppTask != null && whatsAppTask?.status == WhatsAppStatus.PENDING_CONFIRMATION) {
                val task = whatsAppTask!!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0F231D))
                        .border(1.dp, LiveGreen, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                        .testTag("whatsapp_confirmation_card")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(LiveGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WhatsApp Message Ready",
                                color = LiveGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "To: ${task.recipientName}",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "\"${task.messageText}\"",
                            color = Color(0xFFD1FAE5),
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Text(
                            text = "Message ready. Send karna hai?",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            androidx.compose.material3.Button(
                                onClick = { viewModel.confirmWhatsAppMessage(context) },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = LiveGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp).testTag("btn_confirm_whatsapp")
                            ) {
                                Text("Send Confirm", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            androidx.compose.material3.Button(
                                onClick = { viewModel.cancelWhatsAppMessage() },
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1E1E)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp).testTag("btn_cancel_whatsapp")
                            ) {
                                Text("Cancel", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Screen Interaction Mode Stepper
            if (interactionState.step != InteractionStep.IDLE) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF130924))
                        .border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                        .testTag("interaction_mode_stepper")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Screen Interaction Mode",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val steps = listOf(
                            Pair(InteractionStep.LISTENING, "Listening"),
                            Pair(InteractionStep.UNDERSTANDING, "Understanding"),
                            Pair(InteractionStep.FINDING_UI_ELEMENT, "Finding UI"),
                            Pair(InteractionStep.CONFIRMATION, "Confirm"),
                            Pair(InteractionStep.EXECUTING, "Executing"),
                            Pair(InteractionStep.COMPLETED, "Completed")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            steps.forEach { (stepEnum, label) ->
                                val isActive = interactionState.step == stepEnum
                                Text(
                                    text = label,
                                    color = if (isActive) LiveGreen else if (interactionState.step.ordinal > stepEnum.ordinal) NeonCyan else TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                        if (interactionState.feedbackMessage.isNotBlank()) {
                            Text(
                                text = interactionState.feedbackMessage,
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Quick Keyboard input toggle pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SaraCardBg.copy(alpha = 0.65f))
                        .border(0.8.dp, CardBorderSubtle, RoundedCornerShape(16.dp))
                        .clickable { showQuickTextBar = !showQuickTextBar }
                        .padding(horizontal = 10.dp, vertical = 3.5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showQuickTextBar) "Hide Keyboard Bar ▲" else "Type in Hindi / English ▼",
                            color = TextMuted,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showQuickTextBar,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = quickTextInput,
                        onValueChange = { quickTextInput = it },
                        placeholder = { Text("Ask SARA (e.g. 'Aaj ka mausam kaisa hai?')", fontSize = 11.5.sp, color = TextMuted) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_text_input"),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedContainerColor = SaraCardBg,
                            unfocusedContainerColor = SaraCardBg,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (quickTextInput.isNotBlank()) {
                                viewModel.processUserPrompt(quickTextInput)
                                quickTextInput = ""
                            }
                        })
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (quickTextInput.isNotBlank()) {
                                viewModel.processUserPrompt(quickTextInput)
                                quickTextInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NeonPurple)
                            .testTag("quick_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send prompt",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= BOTTOM CONTROLS & NAVIGATION =================
            // Bottom navigation matching reference layout:
            // Left: "WEB LINK"
            // Right: "VISION"
            // Center microphone control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: "WEB LINK"
                BottomNavPill(
                    text = "WEB LINK",
                    icon = Icons.Default.Language,
                    onClick = { viewModel.triggerWebLinkSearch() },
                    modifier = Modifier.weight(1f),
                    testTag = "web_link_button"
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Center: Large circular purple microphone button
                GlowingMicButton(
                    voiceState = voiceState,
                    onClick = { viewModel.toggleListening() },
                    modifier = Modifier.testTag("center_mic_button")
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Right: "VISION"
                BottomNavPill(
                    text = "VISION",
                    icon = Icons.Default.Visibility,
                    onClick = { viewModel.triggerVisionAnalysis() },
                    modifier = Modifier.weight(1f),
                    testTag = "vision_button"
                )
            }
        }
    }
}
