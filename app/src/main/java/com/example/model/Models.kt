package com.example.model

enum class VoiceState {
    IDLE,
    LISTENING,
    PROCESSING,
    SPEAKING
}

enum class MessageSender {
    USER,
    SARA
}

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isHindi: Boolean = true,
    val status: String = "Delivered"
)

data class SystemPromptPreset(
    val id: String,
    val title: String,
    val description: String,
    val systemPrompt: String,
    val personalityTag: String,
    val isDefault: Boolean = false
)

data class PermissionItem(
    val id: String,
    val title: String,
    val description: String,
    val isGranted: Boolean,
    val iconName: String
)

data class MemoryEntry(
    val id: String,
    val key: String,
    val value: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DiagnosticLog(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val level: String = "INFO", // INFO, SUCCESS, WARN, ERROR
    val tag: String,
    val message: String,
    val latencyMs: Long? = null
)

data class SaraSettings(
    val apiKey: String = "",
    val activeModel: String = "gemini-2.5-flash",
    val activePresetId: String = "sassy_girlfriend",
    val voiceInputEnabled: Boolean = true,
    val voiceOutputEnabled: Boolean = true,
    val femaleVoice: Boolean = true,
    val language: String = "Hindi (Default)",
    val speakingRate: Float = 1.0f,
    val pitch: Float = 1.15f
)

enum class NavigationScreen {
    MAIN,
    CHAT_MEMORY,
    SETTINGS_HUB,
    COMPANION_CONSOLE
}

enum class SettingsTab {
    BRAINS,
    PRESETS,
    PERMS,
    IMPORT,
    LOGS
}

data class ScreenStateItem(
    val id: String,
    val name: String,
    val iconName: String,
    val category: String,
    val description: String,
    val suggestedAction: String
)

data class TaskGuide(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val steps: List<TaskStep>
)

data class TaskStep(
    val stepNumber: Int,
    val title: String,
    val detail: String,
    val voiceCommand: String
)
