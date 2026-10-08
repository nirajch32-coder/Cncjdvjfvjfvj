package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavigationScreen
import com.example.ui.theme.BorderGlowCyan
import com.example.ui.theme.BorderGlowPurple
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SaraBackground
import com.example.ui.theme.SaraCardBg
import com.example.ui.theme.SaraCardGlow
import com.example.ui.theme.SaraVoid
import com.example.ui.theme.SpeakingGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SaraViewModel

@Composable
fun CompanionConsoleScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.MAIN)
    }

    val taskGuides = viewModel.taskGuides
    val screenStates = viewModel.screenStates
    val memories by viewModel.memories.collectAsState()

    var expandedGuideId by remember { mutableStateOf<String?>("whatsapp_setup") }
    var selectedScreenStateId by remember { mutableStateOf("launcher") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SaraVoid, SaraBackground, Color(0xFF100724), SaraVoid)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(NavigationScreen.MAIN) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SaraCardBg)
                        .border(1.dp, CardBorderSubtle, CircleShape)
                        .testTag("console_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "SARA Companion Console",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Configure SARA’s step-by-step guidance modes and interact offline.",
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                }
            }

            // SECTION 1: Step-by-Step Task Guides
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(
                    text = "Step-by-Step Task Guides",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                taskGuides.forEach { guide ->
                    val isExpanded = expandedGuideId == guide.id

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SaraCardBg)
                            .border(
                                width = 1.dp,
                                color = if (isExpanded) BorderGlowPurple else CardBorderSubtle,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(13.dp)
                            .testTag("guide_card_${guide.id}")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedGuideId = if (isExpanded) null else guide.id
                                    },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(NeonPurple.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (guide.iconName) {
                                                "chat" -> Icons.AutoMirrored.Filled.Chat
                                                "smart_display" -> Icons.Default.SmartDisplay
                                                else -> Icons.Default.Email
                                            },
                                            contentDescription = guide.title,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(9.dp))
                                    Column {
                                        Text(guide.title, color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                        Text(guide.subtitle, color = TextMuted, fontSize = 10.sp, maxLines = 1)
                                    }
                                }

                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Toggle",
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    verticalArrangement = Arrangement.spacedBy(7.dp)
                                ) {
                                    guide.steps.forEach { step ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(9.dp))
                                                .background(SaraCardGlow)
                                                .padding(9.dp)
                                        ) {
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(19.dp)
                                                            .clip(CircleShape)
                                                            .background(NeonPurple),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Text(step.stepNumber.toString(), color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.width(7.dp))
                                                    Text(step.title, color = NeonCyan, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                                }
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Text(step.detail, color = TextSecondary, fontSize = 10.5.sp)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(step.voiceCommand, color = Color(0xFFC084FC), fontSize = 9.5.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.processUserPrompt("Start guide: ${guide.title}")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(38.dp).padding(top = 2.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Launch Step-by-Step Voice Guide", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: Simulated Screen State Tracker (Agentic)
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(
                    text = "Simulated Screen State Tracker (Agentic)",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                screenStates.forEach { item ->
                    val isSelected = selectedScreenStateId == item.id

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(13.dp))
                            .background(if (isSelected) SaraCardGlow else SaraCardBg)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BorderGlowCyan else CardBorderSubtle,
                                shape = RoundedCornerShape(13.dp)
                            )
                            .clickable { selectedScreenStateId = item.id }
                            .padding(13.dp)
                            .testTag("screen_state_${item.id}")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = when (item.iconName) {
                                            "home" -> Icons.Default.Home
                                            "chat" -> Icons.AutoMirrored.Filled.Chat
                                            "video_library" -> Icons.Default.VideoLibrary
                                            "email" -> Icons.Default.Email
                                            else -> Icons.Default.SportsEsports
                                        },
                                        contentDescription = item.name,
                                        tint = if (isSelected) NeonCyan else NeonPurple,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(7.dp))
                                    Text(
                                        text = item.name,
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) SpeakingGreen.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isSelected) "CURRENT STATE" else item.category,
                                        color = if (isSelected) SpeakingGreen else TextMuted,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))
                            Text(item.description, color = TextSecondary, fontSize = 11.sp)

                            if (isSelected) {
                                Spacer(modifier = Modifier.height(7.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SaraBackground)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text("SARA Agentic Recommendation:", color = NeonCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                        Text(item.suggestedAction, color = Color.White, fontSize = 10.5.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 3: SARA’s Stored Memory Logs
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SARA’s Stored Memory Logs",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${memories.size} Active Keys",
                        color = NeonPurple,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                memories.take(5).forEach { memory ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SaraCardBg)
                            .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                            .padding(11.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(memory.key, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                                Text(memory.value, color = TextMuted, fontSize = 10.5.sp)
                            }
                            Text(
                                text = memory.category,
                                color = NeonCyan,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
