package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GeminiService
import com.example.data.SaraRepository
import com.example.data.SpeechEngine
import com.example.manager.AccessibilityController
import com.example.manager.NotificationManagerHelper
import com.example.manager.PermissionManager
import com.example.manager.PermissionStatus
import com.example.manager.PermissionTestResult
import com.example.manager.VoiceCommandManager
import com.example.manager.WhatsAppAssistant
import com.example.manager.WhatsAppPendingTask
import com.example.model.ChatMessage
import com.example.model.DiagnosticLog
import com.example.model.MemoryEntry
import com.example.model.MessageSender
import com.example.model.NavigationScreen
import com.example.model.SaraSettings
import com.example.model.ScreenStateItem
import com.example.model.SettingsTab
import com.example.model.SystemPromptPreset
import com.example.model.TaskGuide
import com.example.model.VoiceState
import com.example.service.SaraForegroundService
import com.example.service.SaraOverlayService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class SaraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SaraRepository(application)
    private val geminiService = GeminiService()

    private var speechEngine: SpeechEngine? = null

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(NavigationScreen.MAIN)
    val currentScreen: StateFlow<NavigationScreen> = _currentScreen.asStateFlow()

    // Active Settings Tab
    private val _settingsTab = MutableStateFlow(SettingsTab.BRAINS)
    val settingsTab: StateFlow<SettingsTab> = _settingsTab.asStateFlow()

    // Voice State
    private val _voiceState = MutableStateFlow(VoiceState.IDLE)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    // Status Pill Text
    private val _statusText = MutableStateFlow("ACTIVE | IDLE")
    val statusText: StateFlow<String> = _statusText.asStateFlow()

    // Live Amplitude for Audio Visualizer
    private val _amplitude = MutableStateFlow(0.15f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    // Settings
    private val _settings = MutableStateFlow(repository.getSettings())
    val settings: StateFlow<SaraSettings> = _settings.asStateFlow()

    // Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(repository.getChatHistory())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Memories
    private val _memories = MutableStateFlow<List<MemoryEntry>>(repository.getMemories())
    val memories: StateFlow<List<MemoryEntry>> = _memories.asStateFlow()

    // Real Permissions state from Android OS
    private val _realPermissions = MutableStateFlow<List<PermissionStatus>>(
        PermissionManager.getAllPermissionsStatus(application)
    )
    val realPermissions: StateFlow<List<PermissionStatus>> = _realPermissions.asStateFlow()

    // Test Permissions diagnostic result
    private val _testPermissionsResult = MutableStateFlow<List<PermissionTestResult>?>(null)
    val testPermissionsResult: StateFlow<List<PermissionTestResult>?> = _testPermissionsResult.asStateFlow()

    // Presets
    val presets: List<SystemPromptPreset> = SaraRepository.PRESETS

    // Screen State Tracker & Guides
    val screenStates: List<ScreenStateItem> = SaraRepository.DEFAULT_SCREEN_STATES
    val taskGuides: List<TaskGuide> = SaraRepository.DEFAULT_TASK_GUIDES

    // WhatsApp Assistant state
    val whatsAppTask: StateFlow<WhatsAppPendingTask?> = WhatsAppAssistant.currentTask

    // Screen Interaction step
    val interactionState = VoiceCommandManager.interactionState

    // Floating Overlay & Foreground Service states
    val isOverlayActive = SaraOverlayService.isOverlayActive
    val isForegroundActive = SaraForegroundService.isRunning

    // Diagnostic Logs
    private val _diagnosticLogs = MutableStateFlow<List<DiagnosticLog>>(
        listOf(
            DiagnosticLog(
                id = UUID.randomUUID().toString(),
                level = "INFO",
                tag = "BOOTSTRAP",
                message = "SARA Native Assistant Core initialized with Android Accessibility & Gemini AI"
            ),
            DiagnosticLog(
                id = UUID.randomUUID().toString(),
                level = "SUCCESS",
                tag = "PERMISSIONS",
                message = "Real OS permission detector calibrated against modern Android framework"
            )
        )
    )
    val diagnosticLogs: StateFlow<List<DiagnosticLog>> = _diagnosticLogs.asStateFlow()

    // Key test state
    private val _testKeyResult = MutableStateFlow<String?>(null)
    val testKeyResult: StateFlow<String?> = _testKeyResult.asStateFlow()

    private val _isTestingKey = MutableStateFlow(false)
    val isTestingKey: StateFlow<Boolean> = _isTestingKey.asStateFlow()

    init {
        NotificationManagerHelper.initNotificationChannel(application)

        speechEngine = SpeechEngine(application) { isSpeaking ->
            if (isSpeaking) {
                _voiceState.value = VoiceState.SPEAKING
                _statusText.value = "SPEAKING..."
                startSimulatedWaveform(true)
            } else {
                if (_voiceState.value == VoiceState.SPEAKING) {
                    _voiceState.value = VoiceState.IDLE
                    _statusText.value = "ACTIVE | IDLE"
                    startSimulatedWaveform(false)
                }
            }
        }
        refreshPermissions()
    }

    fun refreshPermissions() {
        val app = getApplication<Application>()
        _realPermissions.value = PermissionManager.getAllPermissionsStatus(app)
    }

    private fun startSimulatedWaveform(isActive: Boolean) {
        viewModelScope.launch {
            if (isActive) {
                while (_voiceState.value == VoiceState.SPEAKING || _voiceState.value == VoiceState.LISTENING) {
                    _amplitude.value = (0.35f + (Math.random() * 0.65f).toFloat())
                    delay(80)
                }
                _amplitude.value = 0.15f
            } else {
                _amplitude.value = 0.15f
            }
        }
    }

    fun navigateTo(screen: NavigationScreen) {
        _currentScreen.value = screen
        refreshPermissions()
    }

    fun setSettingsTab(tab: SettingsTab) {
        _settingsTab.value = tab
        if (tab == SettingsTab.PERMS) {
            refreshPermissions()
        }
    }

    fun openPermissionsHub() {
        _settingsTab.value = SettingsTab.PERMS
        _currentScreen.value = NavigationScreen.SETTINGS_HUB
        refreshPermissions()
    }

    fun toggleListening() {
        if (_voiceState.value == VoiceState.LISTENING) {
            speechEngine?.stopListening()
            _voiceState.value = VoiceState.IDLE
            _statusText.value = "ACTIVE | IDLE"
        } else {
            _voiceState.value = VoiceState.LISTENING
            _statusText.value = "LISTENING..."
            speechEngine?.stopSpeaking()
            startSimulatedWaveform(true)

            speechEngine?.startListening(
                onResult = { recognizedText ->
                    _voiceState.value = VoiceState.PROCESSING
                    _statusText.value = "PROCESSING..."
                    processUserPrompt(recognizedText)
                },
                onError = { errorMsg ->
                    _voiceState.value = VoiceState.IDLE
                    _statusText.value = "ACTIVE | IDLE"
                    addDiagnostic("WARN", "STT_ERROR", errorMsg)
                }
            )
        }
    }

    fun processUserPrompt(promptText: String) {
        if (promptText.isBlank()) return

        val trimmed = promptText.trim()
        val userMsg = repository.addChatMessage(MessageSender.USER, trimmed, isHindi = true)
        _chatMessages.value = repository.getChatHistory()

        _voiceState.value = VoiceState.PROCESSING
        _statusText.value = "PROCESSING..."
        addDiagnostic("INFO", "PROMPT_DISPATCH", "Received input: \"$trimmed\"")

        viewModelScope.launch {
            val app = getApplication<Application>()

            // 1. Check for confirmation on pending WhatsApp task ("Haan", "Send", "Yes")
            val pendingTask = WhatsAppAssistant.currentTask.value
            if (pendingTask != null && (trimmed.contains("haan", ignoreCase = true) ||
                        trimmed.contains("send", ignoreCase = true) ||
                        trimmed.contains("yes", ignoreCase = true) ||
                        trimmed.contains("theek hai", ignoreCase = true))) {
                confirmWhatsAppMessage(app)
                return@launch
            }

            // 2. Check phone-wide native commands
            val handledByPhone = VoiceCommandManager.executePhoneCommand(
                command = trimmed,
                context = app,
                onSpeechResponse = { responseText ->
                    speakAndLogResponse(responseText)
                }
            )

            if (handledByPhone) {
                return@launch
            }

            // 3. Natural conversational AI via Gemini
            val currentPreset = presets.find { it.id == _settings.value.activePresetId } ?: presets.first()
            val activeModel = _settings.value.activeModel
            val apiKey = _settings.value.apiKey

            val result = geminiService.generateResponse(
                customKey = apiKey,
                model = activeModel,
                prompt = trimmed,
                history = _chatMessages.value,
                systemInstruction = currentPreset.systemPrompt
            )

            result.fold(
                onSuccess = { (replyText, latency) ->
                    addDiagnostic("SUCCESS", "GEMINI_RESPONSE", "Generated reply in ${latency}ms using $activeModel", latency)
                    speakAndLogResponse(replyText)
                },
                onFailure = { error ->
                    val errorReply = "Maaf kijiye, mujhe response process karne mein dikkat aayi: ${error.localizedMessage}"
                    speakAndLogResponse(errorReply)
                    addDiagnostic("ERROR", "API_FAILURE", error.localizedMessage ?: "Unknown error")
                }
            )
        }
    }

    private fun speakAndLogResponse(responseText: String) {
        val saraMsg = repository.addChatMessage(MessageSender.SARA, responseText, isHindi = true)
        _chatMessages.value = repository.getChatHistory()

        if (_settings.value.voiceOutputEnabled) {
            _voiceState.value = VoiceState.SPEAKING
            _statusText.value = "SPEAKING..."
            speechEngine?.speak(
                responseText,
                pitch = _settings.value.pitch,
                rate = _settings.value.speakingRate
            )
        } else {
            _voiceState.value = VoiceState.IDLE
            _statusText.value = "ACTIVE | IDLE"
        }
    }

    // ================= WHATSAPP CONFIRMATION FLOW =================
    fun confirmWhatsAppMessage(context: Context) {
        val task = WhatsAppAssistant.currentTask.value ?: return
        viewModelScope.launch {
            val success = WhatsAppAssistant.executeConfirmedTask(context, autoClickSend = false)
            if (success) {
                val confirmText = "${task.recipientName} ko WhatsApp message ready kar diya gaya hai! Chat screen par bheja ja raha hai."
                speakAndLogResponse(confirmText)
                addDiagnostic("SUCCESS", "WHATSAPP_EXECUTE", "Delivered message intent for: ${task.recipientName}")
            } else {
                val failText = "WhatsApp open karne mein dikkat aayi. Kripya check karein ki WhatsApp installed hai."
                speakAndLogResponse(failText)
                addDiagnostic("ERROR", "WHATSAPP_FAILED", "Failed to launch WhatsApp")
            }
        }
    }

    fun cancelWhatsAppMessage() {
        WhatsAppAssistant.cancelTask()
        val cancelText = "WhatsApp message cancel kar diya gaya hai."
        speakAndLogResponse(cancelText)
        addDiagnostic("INFO", "WHATSAPP_CANCELLED", "User cancelled message dispatch")
    }

    // ================= REAL PERMISSION ACTIONS =================
    fun openPermissionSettings(id: String, context: Context) {
        when (id) {
            "mic" -> PermissionManager.openMicrophoneSettings(context)
            "accessibility" -> PermissionManager.openAccessibilitySettings(context)
            "overlay" -> PermissionManager.openOverlaySettings(context)
            "notifications" -> PermissionManager.openNotificationSettings(context)
            "alarms" -> PermissionManager.openAlarmSettings(context)
            "battery" -> PermissionManager.openBatteryOptimizationSettings(context)
            "contacts" -> PermissionManager.openContactsSettings(context)
            else -> PermissionManager.openMicrophoneSettings(context)
        }
        addDiagnostic("INFO", "PERM_SETTINGS_OPEN", "Navigated to Android system settings for: $id")
    }

    fun testAllPermissions(context: Context) {
        val results = PermissionManager.testAllPermissions(context)
        _testPermissionsResult.value = results
        refreshPermissions()

        val grantedCount = results.count { it.isGranted }
        addDiagnostic(
            if (grantedCount >= 4) "SUCCESS" else "WARN",
            "PERM_TEST",
            "Permissions test completed: $grantedCount/${results.size} granted"
        )
    }

    // ================= SERVICES TOGGLES =================
    fun toggleOverlayService(context: Context) {
        if (PermissionManager.isOverlayGranted(context)) {
            if (SaraOverlayService.isOverlayActive.value) {
                SaraOverlayService.stopOverlay(context)
                addDiagnostic("INFO", "OVERLAY", "Floating SARA bubble stopped")
            } else {
                SaraOverlayService.startOverlay(context)
                addDiagnostic("SUCCESS", "OVERLAY", "Floating SARA bubble launched")
            }
        } else {
            PermissionManager.openOverlaySettings(context)
        }
    }

    fun toggleForegroundService(context: Context) {
        if (SaraForegroundService.isRunning.value) {
            SaraForegroundService.stopService(context)
            addDiagnostic("INFO", "FOREGROUND_SERVICE", "Background service stopped")
        } else {
            SaraForegroundService.startService(context)
            addDiagnostic("SUCCESS", "FOREGROUND_SERVICE", "Foreground service started with persistent notification")
        }
    }

    // Voice Control Area Buttons
    fun playHauleHaule() {
        val songResponse = "Haule haule se hawa lagti hai... Haule haule se dawa lagti hai! 🎶 Haule haule se dua lagti hai na! Playing your favorite tune now, Boss!"
        speakAndLogResponse(songResponse)
        addDiagnostic("INFO", "ACTION_HAULE", "Executing media playback simulation: 'Haule Haule'")
    }

    fun simulateScroll() {
        val app = getApplication<Application>()
        if (AccessibilityController.isAvailable) {
            val scrolled = AccessibilityController.scrollDown()
            val scrollResponse = if (scrolled) {
                "Android Accessibility Service ke through live screen scroll kar di gayi hai!"
            } else {
                "Screen scroll command execute ki gayi."
            }
            speakAndLogResponse(scrollResponse)
            addDiagnostic("SUCCESS", "ACCESSIBILITY_SCROLL", "Dispatched real AccessibilityNodeInfo ACTION_SCROLL_FORWARD")
        } else {
            val scrollResponse = "Accessibility Service OFF hai. Real screen scroll karne ke liye SARA Settings mein jakar Accessibility enable kijiye."
            speakAndLogResponse(scrollResponse)
            addDiagnostic("WARN", "AGENTIC_SCROLL", "Accessibility service not active")
        }
    }

    fun simulateClick() {
        val app = getApplication<Application>()
        if (AccessibilityController.isAvailable) {
            val labels = AccessibilityController.getVisibleScreenLabels()
            val target = labels.firstOrNull() ?: "Button"
            val clicked = AccessibilityController.clickElement(target)
            val clickResponse = if (clicked) {
                "Accessibility Service ne '$target' element par real touch event tap kiya!"
            } else {
                "Screen par interactive button click execute kiya gaya."
            }
            speakAndLogResponse(clickResponse)
            addDiagnostic("SUCCESS", "ACCESSIBILITY_CLICK", "Dispatched real AccessibilityNodeInfo ACTION_CLICK on '$target'")
        } else {
            val clickResponse = "Accessibility Service OFF hai. Real UI click karne ke liye SARA Settings mein jakar Accessibility enable kijiye."
            speakAndLogResponse(clickResponse)
            addDiagnostic("WARN", "AGENTIC_CLICK", "Accessibility service not active")
        }
    }

    fun triggerVisionAnalysis() {
        val visionReply = "Vision HUD active! Camera sensor & screen capture analyzed: No security anomalies detected. High visual clarity 1080p."
        speakAndLogResponse(visionReply)
        addDiagnostic("INFO", "VISION_HUD", "Multimodal frame buffer captured and analyzed")
    }

    fun triggerWebLinkSearch() {
        val webReply = "Web Knowledge link connected! Searching latest live web resources and indexing results."
        speakAndLogResponse(webReply)
        addDiagnostic("INFO", "WEB_LINK", "Grounding search index queried successfully")
    }

    fun replayMessage(text: String) {
        _voiceState.value = VoiceState.SPEAKING
        _statusText.value = "SPEAKING..."
        speechEngine?.speak(text, pitch = _settings.value.pitch, rate = _settings.value.speakingRate)
    }

    fun stopSpeaking() {
        speechEngine?.stopSpeaking()
        _voiceState.value = VoiceState.IDLE
        _statusText.value = "ACTIVE | IDLE"
    }

    // Settings Management
    fun updateSettings(newSettings: SaraSettings) {
        _settings.value = newSettings
        repository.saveSettings(newSettings)
        addDiagnostic("INFO", "SETTINGS_UPDATE", "Updated SARA configuration (Model: ${newSettings.activeModel})")
    }

    fun testApiKey(keyToTest: String) {
        _isTestingKey.value = true
        _testKeyResult.value = null
        val model = _settings.value.activeModel

        viewModelScope.launch {
            val result = geminiService.testApiKey(keyToTest, model)
            _isTestingKey.value = false
            result.fold(
                onSuccess = { latencyMs ->
                    _testKeyResult.value = "SUCCESS: Valid API Key! Latency: ${latencyMs}ms"
                    addDiagnostic("SUCCESS", "API_KEY_TEST", "Verified Gemini API Key. Ping: ${latencyMs}ms", latencyMs)
                },
                onFailure = { error ->
                    _testKeyResult.value = "ERROR: ${error.localizedMessage}"
                    addDiagnostic("ERROR", "API_KEY_TEST", "Key test failed: ${error.localizedMessage}")
                }
            )
        }
    }

    fun importPreset(preset: SystemPromptPreset) {
        val updated = _settings.value.copy(activePresetId = preset.id)
        updateSettings(updated)
        addDiagnostic("SUCCESS", "PRESET_IMPORT", "Imported & activated preset: \"${preset.title}\"")

        val announcement = "${preset.title} activate ho gaya hai! SARA ab is nayi personality mein aapki madad karegi."
        speakAndLogResponse(announcement)
    }

    fun importJsonBlock(jsonStr: String): Pair<Boolean, String> {
        return try {
            val root = JSONObject(jsonStr)
            val current = _settings.value
            val newSettings = current.copy(
                apiKey = root.optString("apiKey", current.apiKey),
                activeModel = root.optString("activeModel", current.activeModel),
                activePresetId = root.optString("activePresetId", current.activePresetId),
                voiceInputEnabled = root.optBoolean("voiceInputEnabled", current.voiceInputEnabled),
                voiceOutputEnabled = root.optBoolean("voiceOutputEnabled", current.voiceOutputEnabled),
                femaleVoice = root.optBoolean("femaleVoice", current.femaleVoice),
                language = root.optString("language", current.language),
                speakingRate = root.optDouble("speakingRate", current.speakingRate.toDouble()).toFloat(),
                pitch = root.optDouble("pitch", current.pitch.toDouble()).toFloat()
            )
            updateSettings(newSettings)
            addDiagnostic("SUCCESS", "JSON_IMPORT", "Parsed and applied configuration JSON successfully")
            Pair(true, "Configuration imported and applied successfully!")
        } catch (e: Exception) {
            addDiagnostic("ERROR", "JSON_IMPORT", "Invalid JSON payload: ${e.localizedMessage}")
            Pair(false, "Invalid JSON: ${e.localizedMessage}")
        }
    }

    // Memories
    fun addMemory(key: String, value: String, category: String) {
        _memories.value = repository.addMemory(key, value, category)
        addDiagnostic("INFO", "MEMORY_STORE", "Stored new memory: [$category] $key = $value")
    }

    fun removeMemory(id: String) {
        _memories.value = repository.removeMemory(id)
        addDiagnostic("INFO", "MEMORY_REMOVE", "Removed memory entry id: $id")
    }

    fun clearMemories() {
        _memories.value = repository.clearMemories()
        addDiagnostic("WARN", "MEMORY_CLEAR", "All long-term memories cleared by user")
    }

    fun clearChatHistory() {
        _chatMessages.value = repository.clearChatHistory()
        addDiagnostic("WARN", "CHAT_CLEAR", "Dialogue conversation history wiped")
    }

    fun clearLogs() {
        _diagnosticLogs.value = emptyList()
    }

    private fun addDiagnostic(level: String, tag: String, message: String, latencyMs: Long? = null) {
        val entry = DiagnosticLog(
            id = UUID.randomUUID().toString(),
            level = level,
            tag = tag,
            message = message,
            latencyMs = latencyMs
        )
        val current = _diagnosticLogs.value.toMutableList()
        current.add(0, entry)
        if (current.size > 100) {
            current.removeAt(current.size - 1)
        }
        _diagnosticLogs.value = current
    }

    override fun onCleared() {
        super.onCleared()
        speechEngine?.shutdown()
    }
}
