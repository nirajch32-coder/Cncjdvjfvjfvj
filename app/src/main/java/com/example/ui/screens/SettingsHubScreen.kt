package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manager.PermissionStatus
import com.example.manager.PermissionTestResult
import com.example.model.NavigationScreen
import com.example.model.PermissionItem
import com.example.model.SaraSettings
import com.example.model.SettingsTab
import com.example.model.SystemPromptPreset
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BorderGlowCyan
import com.example.ui.theme.BorderGlowPurple
import com.example.ui.theme.CardBorderSubtle
import com.example.ui.theme.LiveGreen
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsHubScreen(
    viewModel: SaraViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.MAIN)
    }

    val context = LocalContext.current
    val currentTab by viewModel.settingsTab.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val realPermissions by viewModel.realPermissions.collectAsState()
    val testPermissionsResult by viewModel.testPermissionsResult.collectAsState()
    val isOverlayActive by viewModel.isOverlayActive.collectAsState()
    val isForegroundActive by viewModel.isForegroundActive.collectAsState()
    val presets = viewModel.presets
    val diagnosticLogs by viewModel.diagnosticLogs.collectAsState()
    val isTestingKey by viewModel.isTestingKey.collectAsState()
    val testKeyResult by viewModel.testKeyResult.collectAsState()

    val tabs = SettingsTab.values()

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
        ) {
            // ================= HEADER =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SaraCardBg)
                            .border(1.5.dp, BorderGlowPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S",
                            color = NeonPurple,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Sara Settings Hub",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Customize active provider, import instructions presets, and check live diagnostics catalog.",
                            color = TextMuted,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            maxLines = 2
                        )
                    }
                }

                // Close button on top-right
                IconButton(
                    onClick = { viewModel.navigateTo(NavigationScreen.MAIN) },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(SaraCardBg)
                        .border(1.dp, CardBorderSubtle, CircleShape)
                        .testTag("settings_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Settings",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // ================= TAB NAVIGATION =================
            ScrollableTabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = SaraCardBg,
                contentColor = NeonPurple,
                edgePadding = 6.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                        color = NeonPurple
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
            ) {
                tabs.forEach { tab ->
                    val tabName = when (tab) {
                        SettingsTab.BRAINS -> "Brains"
                        SettingsTab.PRESETS -> "Presets"
                        SettingsTab.PERMS -> "Perms"
                        SettingsTab.IMPORT -> "Import"
                        SettingsTab.LOGS -> "Logs"
                    }
                    Tab(
                        selected = currentTab == tab,
                        onClick = { viewModel.setSettingsTab(tab) },
                        text = {
                            Text(
                                text = tabName,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        selectedContentColor = NeonPurple,
                        unselectedContentColor = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ================= TAB CONTENT =================
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (currentTab) {
                    SettingsTab.BRAINS -> BrainsTabContent(
                        settings = settings,
                        onUpdate = { viewModel.updateSettings(it) },
                        onTestKey = { viewModel.testApiKey(it) },
                        isTestingKey = isTestingKey,
                        testKeyResult = testKeyResult
                    )

                    SettingsTab.PRESETS -> PresetsTabContent(
                        presets = presets,
                        activePresetId = settings.activePresetId,
                        onImport = { viewModel.importPreset(it) }
                    )

                    SettingsTab.PERMS -> PermsTabContent(
                        permissions = realPermissions,
                        onOpenSettings = { viewModel.openPermissionSettings(it, context) },
                        onTestAll = { viewModel.testAllPermissions(context) },
                        testResults = testPermissionsResult,
                        isOverlayActive = isOverlayActive,
                        onToggleOverlay = { viewModel.toggleOverlayService(context) },
                        isForegroundActive = isForegroundActive,
                        onToggleForeground = { viewModel.toggleForegroundService(context) }
                    )

                    SettingsTab.IMPORT -> ImportTabContent(
                        onImportJson = { viewModel.importJsonBlock(it) }
                    )

                    SettingsTab.LOGS -> LogsTabContent(
                        diagnosticLogs = diagnosticLogs,
                        onClearLogs = { viewModel.clearLogs() },
                        onCopyLogs = {
                            val allText = diagnosticLogs.joinToString("\n") { "[${it.level}] ${it.tag}: ${it.message}" }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("SARA Logs", allText))
                            Toast.makeText(context, "Diagnostics logs copied to clipboard!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

// ================= 1. BRAINS TAB =================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainsTabContent(
    settings: SaraSettings,
    onUpdate: (SaraSettings) -> Unit,
    onTestKey: (String) -> Unit,
    isTestingKey: Boolean,
    testKeyResult: String?
) {
    var apiKeyInput by remember(settings.apiKey) { mutableStateOf(settings.apiKey) }
    var showApiKey by remember { mutableStateOf(false) }

    val models = listOf(
        "gemini-2.5-flash",
        "gemini-3.5-flash",
        "gemini-3.1-pro-preview",
        "gemini-3.1-flash-lite-preview"
    )
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Active: Gemini Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SaraCardGlow)
                .border(1.dp, BorderGlowPurple, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(LiveGreen)
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = "Active: Gemini",
                        color = Color.White,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Brain, voice input or voice output yahan configure karo.",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )
            }
        }

        // Section: AI Brain Provider Settings
        Text(
            text = "AI Brain Provider Settings",
            color = Color.White,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold
        )

        // Provider Dropdown: Gemini (Google DeepMind - Default)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SaraCardBg)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Brain Provider", color = TextMuted, fontSize = 9.5.sp)
                    Text("Gemini (Google DeepMind - Default)", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonPurple.copy(alpha = 0.25f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text("CONNECTED", color = NeonCyan, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // API Key Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SaraCardBg)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    text = "Gemini API Key",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                // Masked secure input
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        onUpdate(settings.copy(apiKey = it))
                    },
                    placeholder = { Text("Enter your Gemini API key...", fontSize = 11.5.sp, color = TextMuted) },
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle key visibility",
                                tint = NeonPurple,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gemini_api_key_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonPurple,
                        unfocusedBorderColor = CardBorderSubtle,
                        focusedContainerColor = SaraBackground,
                        unfocusedContainerColor = SaraBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Never display the complete API key after saving
                if (settings.apiKey.isNotBlank() && !showApiKey) {
                    val masked = if (settings.apiKey.length > 4) {
                        "••••••••••••" + settings.apiKey.takeLast(4)
                    } else "••••••••"
                    Text(
                        text = "Saved Key: $masked",
                        color = LiveGreen,
                        fontSize = 10.5.sp
                    )
                }

                // TEST button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { onTestKey(apiKeyInput) },
                        enabled = !isTestingKey,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("test_api_key_button")
                    ) {
                        if (isTestingKey) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(13.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Testing...", fontSize = 11.sp)
                        } else {
                            Text("TEST", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    if (testKeyResult != null) {
                        Text(
                            text = testKeyResult,
                            color = if (testKeyResult.startsWith("SUCCESS")) LiveGreen else AlertRed,
                            fontSize = 10.5.sp,
                            maxLines = 2,
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                    }
                }
            }
        }

        // Model Selection Dropdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SaraCardBg)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    text = "Active Gemini Model",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = modelDropdownExpanded,
                    onExpandedChange = { modelDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = settings.activeModel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonPurple,
                            unfocusedBorderColor = CardBorderSubtle,
                            focusedContainerColor = SaraBackground,
                            unfocusedContainerColor = SaraBackground,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = modelDropdownExpanded,
                        onDismissRequest = { modelDropdownExpanded = false },
                        modifier = Modifier.background(SaraCardGlow)
                    ) {
                        models.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model, color = Color.White, fontSize = 12.sp) },
                                onClick = {
                                    onUpdate(settings.copy(activeModel = model))
                                    modelDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Description card explaining selected model
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SaraBackground.copy(alpha = 0.85f))
                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(10.dp))
                        .padding(9.dp)
                ) {
                    Text(
                        text = when (settings.activeModel) {
                            "gemini-2.5-flash" -> "gemini-2.5-flash: Ultra-fast sub-second latency model with native Hindi fluency and high multimodal efficiency."
                            "gemini-3.5-flash" -> "gemini-3.5-flash: Balanced flagship model for high-fidelity reasoning and complex multi-turn dialogue."
                            "gemini-3.1-pro-preview" -> "gemini-3.1-pro-preview: Deep reasoning model for complex engineering and agentic logic."
                            else -> "Optimized lightweight Gemini model for rapid edge inference."
                        },
                        color = TextSecondary,
                        fontSize = 10.5.sp,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Voice Section: Gemini Native Voice Active
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SaraCardBg)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Gemini Native Voice Active", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        Text("Configured for Hindi speech synthesis & recognition", color = TextMuted, fontSize = 9.5.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SpeakingGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text("ON", color = SpeakingGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Voice Input Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Voice Input (Speech Recognition)", color = TextSecondary, fontSize = 11.5.sp)
                    Switch(
                        checked = settings.voiceInputEnabled,
                        onCheckedChange = { onUpdate(settings.copy(voiceInputEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonPurple)
                    )
                }

                // Voice Output Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Voice Output (Hindi TTS Speech)", color = TextSecondary, fontSize = 11.5.sp)
                    Switch(
                        checked = settings.voiceOutputEnabled,
                        onCheckedChange = { onUpdate(settings.copy(voiceOutputEnabled = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonPurple)
                    )
                }

                // Female Voice Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Female Voice (SARA Default Tone)", color = TextSecondary, fontSize = 11.5.sp)
                    Switch(
                        checked = settings.femaleVoice,
                        onCheckedChange = { onUpdate(settings.copy(femaleVoice = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonCyan)
                    )
                }

                // Speaking Speed Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Speaking Speed", color = TextSecondary, fontSize = 11.5.sp)
                        Text(String.format(Locale.US, "%.1fx", settings.speakingRate), color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = settings.speakingRate,
                        onValueChange = { onUpdate(settings.copy(speakingRate = it)) },
                        valueRange = 0.6f..1.8f,
                        colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple)
                    )
                }

                // Voice Pitch Slider
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Voice Pitch", color = TextSecondary, fontSize = 11.5.sp)
                        Text(String.format(Locale.US, "%.2fx", settings.pitch), color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = settings.pitch,
                        onValueChange = { onUpdate(settings.copy(pitch = it)) },
                        valueRange = 0.8f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )
                }
            }
        }
    }
}

// ================= 2. PRESETS TAB =================
@Composable
fun PresetsTabContent(
    presets: List<SystemPromptPreset>,
    activePresetId: String,
    onImport: (SystemPromptPreset) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text(
            text = "System Prompt Presets Importer",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tap 'Import Preset' to activate SARA's personality model in one click.",
            color = TextMuted,
            fontSize = 10.5.sp
        )

        presets.forEach { preset ->
            val isActive = preset.id == activePresetId

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isActive) SaraCardGlow else SaraCardBg)
                    .border(
                        width = 1.dp,
                        color = if (isActive) BorderGlowCyan else CardBorderSubtle,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
                    .testTag("preset_card_${preset.id}")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = preset.title,
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpeakingGreen.copy(alpha = 0.2f))
                                    .border(1.dp, SpeakingGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = SpeakingGreen, modifier = Modifier.size(11.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("ACTIVE", color = SpeakingGreen, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Text(
                        text = preset.description,
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 15.sp
                    )

                    Button(
                        onClick = { onImport(preset) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) Color(0xFF1E293B) else NeonPurple
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("import_preset_btn_${preset.id}")
                    ) {
                        Text(
                            text = if (isActive) "Active Preset" else "Import Preset",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isActive) NeonCyan else Color.White
                        )
                    }
                }
            }
        }
    }
}

// ================= 3. PERMISSIONS TAB =================
@Composable
fun PermsTabContent(
    permissions: List<PermissionStatus>,
    onOpenSettings: (String) -> Unit,
    onTestAll: () -> Unit,
    testResults: List<PermissionTestResult>?,
    isOverlayActive: Boolean,
    onToggleOverlay: () -> Unit,
    isForegroundActive: Boolean,
    onToggleForeground: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SARA Real Device Permissions",
                    color = Color.White,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Live system checks via Android OS runtime APIs. Zero fake states.",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            // Test Permissions Button
            Button(
                onClick = onTestAll,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("test_all_permissions_button")
            ) {
                Text("Test Permissions", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Live Test Audit Results Banner (if ran)
        if (testResults != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0820))
                    .border(1.dp, BorderGlowPurple, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Android System Permission Audit Report:",
                        color = NeonCyan,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    testResults.forEach { res ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${res.title}: ${res.detail}",
                                color = if (res.isGranted) SpeakingGreen else Color(0xFFF59E0B),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Quick System Services Control Card (Overlay & Foreground Service)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(13.dp))
                .background(SaraCardGlow)
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(13.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Native Android Background & HUD Services",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                // Floating Overlay Bubble Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Draggable Floating SARA Bubble", color = TextSecondary, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        Text("Movable purple neon bubble over all apps", color = TextMuted, fontSize = 9.5.sp)
                    }
                    Switch(
                        checked = isOverlayActive,
                        onCheckedChange = { onToggleOverlay() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonPurple)
                    )
                }

                // Foreground Service Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Persistent Voice Service (Foreground)", color = TextSecondary, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        Text("Show persistent notification & low-power wake", color = TextMuted, fontSize = 9.5.sp)
                    }
                    Switch(
                        checked = isForegroundActive,
                        onCheckedChange = { onToggleForeground() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = NeonCyan)
                    )
                }
            }
        }

        // 7 Permission Cards
        permissions.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(13.dp))
                    .background(SaraCardBg)
                    .border(
                        width = 1.dp,
                        color = if (item.isGranted) BorderGlowCyan else BorderGlowPurple,
                        shape = RoundedCornerShape(13.dp)
                    )
                    .padding(12.dp)
                    .testTag("permission_card_${item.id}")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                                    .background(if (item.isGranted) SpeakingGreen.copy(alpha = 0.2f) else Color(0xFF26123D)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item.id) {
                                        "mic" -> Icons.Default.Mic
                                        "accessibility" -> Icons.Default.TouchApp
                                        "overlay" -> Icons.Default.Layers
                                        "notifications" -> Icons.Default.Notifications
                                        "alarms" -> Icons.Default.Alarm
                                        "battery" -> Icons.Default.BatteryChargingFull
                                        else -> Icons.Default.Contacts
                                    },
                                    contentDescription = item.title,
                                    tint = if (item.isGranted) SpeakingGreen else NeonPurple,
                                    modifier = Modifier.size(17.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.explanation,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Dynamic status badge: ✓ Enabled or ⚠ Disabled
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(7.dp))
                                .background(
                                    if (item.isGranted) SpeakingGreen.copy(alpha = 0.2f)
                                    else Color(0xFF3B1E14)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (item.isGranted) SpeakingGreen.copy(alpha = 0.6f) else Color(0xFFF59E0B).copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(7.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (item.isGranted) "✓ Enabled" else "⚠ Disabled",
                                color = if (item.isGranted) SpeakingGreen else Color(0xFFF59E0B),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Action buttons row: "Enable / Grant", "Open Settings", "Test"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenSettings(item.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.isGranted) Color(0xFF1E293B) else NeonPurple
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_grant_${item.id}")
                        ) {
                            Text(
                                text = if (item.isGranted) "Active" else "Enable",
                                color = if (item.isGranted) NeonCyan else Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onOpenSettings(item.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF23143B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                                .testTag("btn_settings_${item.id}")
                        ) {
                            Text(
                                text = "Open Settings",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = onTestAll,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF130924)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(34.dp)
                                .testTag("btn_test_${item.id}")
                        ) {
                            Text(
                                text = "Test",
                                color = NeonCyan,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ================= 4. IMPORT TAB =================
@Composable
fun ImportTabContent(
    onImportJson: (String) -> Pair<Boolean, String>
) {
    var jsonText by remember {
        mutableStateOf(
            """{
  "activeModel": "gemini-2.5-flash",
  "activePresetId": "sassy_girlfriend",
  "voiceInputEnabled": true,
  "voiceOutputEnabled": true,
  "femaleVoice": true,
  "speakingRate": 1.0,
  "pitch": 1.15
}"""
        )
    }
    var importResultMsg by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text(
            text = "Paste Settings JSON Block",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Import external SARA configurations, prompt instructions, and voice parameters via raw JSON.",
            color = TextMuted,
            fontSize = 10.5.sp
        )

        // Large dark code editor
        OutlinedTextField(
            value = jsonText,
            onValueChange = { jsonText = it },
            placeholder = { Text("Paste your JSON config block here...", color = TextMuted, fontSize = 11.5.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .testTag("json_config_editor"),
            shape = RoundedCornerShape(13.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
                color = NeonCyan
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonPurple,
                unfocusedBorderColor = CardBorderSubtle,
                focusedContainerColor = Color(0xFF090412),
                unfocusedContainerColor = Color(0xFF090412)
            )
        )

        // Result Banner
        if (importResultMsg != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSuccess) Color(0xFF0F291E) else Color(0xFF2E1010))
                    .border(1.dp, if (isSuccess) LiveGreen else AlertRed, RoundedCornerShape(10.dp))
                    .padding(11.dp)
            ) {
                Text(
                    text = importResultMsg.orEmpty(),
                    color = if (isSuccess) LiveGreen else AlertRed,
                    fontSize = 11.5.sp
                )
            }
        }

        // Large Green Button: Load and Parse Config JSON
        Button(
            onClick = {
                val (success, msg) = onImportJson(jsonText)
                isSuccess = success
                importResultMsg = msg
            },
            colors = ButtonDefaults.buttonColors(containerColor = LiveGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("load_parse_json_button")
        ) {
            Text(
                text = "Load and Parse Config JSON",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.5.sp
            )
        }
    }
}

// ================= 5. LOGS TAB =================
@Composable
fun LogsTabContent(
    diagnosticLogs: List<com.example.model.DiagnosticLog>,
    onClearLogs: () -> Unit,
    onCopyLogs: () -> Unit
) {
    val errorCatalog = listOf(
        Pair("Unauthorized Key/Token (401/403)", "API key invalid, restricted or quota credentials expired."),
        Pair("Location Restricted (Geo)", "Region geo-fence block on specific Gemini endpoints."),
        Pair("Rate Limit Exceeded (429)", "Free tier rate exceeded. SARA will auto-switch to local fallback."),
        Pair("Connection / Timeout", "Socket read timeout > 60s or network cellular drop."),
        Pair("Model Not Found (404)", "Model identifier deprecated. SARA defaults to gemini-2.5-flash.")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Known Network Errors Catalog
        Text(
            text = "Known Network Errors Catalog",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        errorCatalog.forEach { (title, desc) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SaraCardBg)
                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(12.dp))
                    .padding(11.dp)
            ) {
                Column {
                    Text(title, color = NeonPurple, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(desc, color = TextMuted, fontSize = 10.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Real-time Request Diagnostics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Real-time Request Diagnostics",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = onCopyLogs, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Logs", tint = NeonCyan, modifier = Modifier.size(15.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onClearLogs, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear Logs", tint = Color(0xFFEF4444), modifier = Modifier.size(15.dp))
                }
            }
        }

        // Scrollable diagnostic console
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(Color(0xFF07030E))
                .border(1.dp, CardBorderSubtle, RoundedCornerShape(13.dp))
                .padding(11.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (diagnosticLogs.isEmpty()) {
                    Text("No diagnostic logs recorded.", color = TextMuted, fontSize = 10.5.sp)
                } else {
                    diagnosticLogs.forEach { log ->
                        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        val levelColor = when (log.level) {
                            "SUCCESS" -> LiveGreen
                            "WARN" -> Color(0xFFF59E0B)
                            "ERROR" -> AlertRed
                            else -> NeonPurple
                        }
                        Text(
                            text = "[$timeStr] [${log.tag}] ${log.message}${if (log.latencyMs != null) " (${log.latencyMs}ms)" else ""}",
                            color = levelColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 13.5.sp
                        )
                    }
                }
            }
        }
    }
}
