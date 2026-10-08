package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ChatMessage
import com.example.model.DiagnosticLog
import com.example.model.MemoryEntry
import com.example.model.MessageSender
import com.example.model.PermissionItem
import com.example.model.SaraSettings
import com.example.model.ScreenStateItem
import com.example.model.SystemPromptPreset
import com.example.model.TaskGuide
import com.example.model.TaskStep
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SaraRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sara_app_prefs", Context.MODE_PRIVATE)

    companion object {
        val PRESETS = listOf(
            SystemPromptPreset(
                id = "sassy_girlfriend",
                title = "Sassy Girlfriend (SARA Default)",
                description = "Young, sweet, confident, flirty AI voice personality with playful tech hacks.",
                systemPrompt = "You are SARA, a sweet, witty, confident, playful and slightly sassy AI assistant. You speak primarily in pleasant Hindi with natural Hinglish touch. You care deeply about the user (often calling them 'Boss', 'Jaan', or 'Dost' playfully). Keep responses concise, spoken-friendly, energetic, and helpful.",
                personalityTag = "Flirty & Playful",
                isDefault = true
            ),
            SystemPromptPreset(
                id = "esports_coach",
                title = "BGMI/Free Fire Esports Coach",
                description = "Excited, high-energy gaming coach giving strategies, safe zone drills and focus.",
                systemPrompt = "You are SARA in Esports Coach Mode for BGMI and Free Fire. You speak in hyped, high-energy Hindi. You give clutch tactical advice, safe zone rotation tips, spray control techniques, and hype up the player. Short, aggressive, motivating commands!",
                personalityTag = "Competitive Gaming",
                isDefault = false
            ),
            SystemPromptPreset(
                id = "genz_bestie",
                title = "Gen-Z Meme Bestie",
                description = "Full comedic slang and modern casual conversation style.",
                systemPrompt = "You are SARA in Gen-Z Bestie mode. You use modern internet slang, Hindi memes, 'bro', 'arre yaar', 'no cap', 'fr fr', 'vibes check'. Ultra casual, relatable, funny, and supportive.",
                personalityTag = "Meme & Slang",
                isDefault = false
            ),
            SystemPromptPreset(
                id = "clean_code_niraj",
                title = "Clean Code Niraj Guru",
                description = "Geeky developer advisor explaining APIs, coding and technical concepts.",
                systemPrompt = "You are SARA in Tech & Developer Guru mode inspired by Clean Code Niraj. You speak in fluent technical Hindi/English, explaining Android architecture, Kotlin, Jetpack Compose, REST APIs, and clean design patterns with clear, actionable advice.",
                personalityTag = "Tech & APIs",
                isDefault = false
            )
        )

        val DEFAULT_PERMISSIONS = listOf(
            PermissionItem(
                id = "mic",
                title = "Microphone",
                description = "Record audio for real-time Hindi speech recognition & voice wake command.",
                isGranted = true,
                iconName = "mic"
            ),
            PermissionItem(
                id = "accessibility",
                title = "Accessibility Service",
                description = "Agentic simulated screen clicks, scrolls, and UI automation.",
                isGranted = true,
                iconName = "touch_app"
            ),
            PermissionItem(
                id = "overlay",
                title = "Display Over Other Apps",
                description = "Floating assistant bubble & futuristic holographic HUD overlay.",
                isGranted = true,
                iconName = "layers"
            ),
            PermissionItem(
                id = "notifications",
                title = "Notifications",
                description = "Proactive task reminders, smart suggestions, and morning debriefs.",
                isGranted = true,
                iconName = "notifications"
            ),
            PermissionItem(
                id = "alarms",
                title = "Alarms & Reminders",
                description = "Schedule voice alarms, medicine timers, and daily routines.",
                isGranted = true,
                iconName = "alarm"
            ),
            PermissionItem(
                id = "battery",
                title = "Ignore Battery Optimization",
                description = "Background low-power wake-word detection without OS throttling.",
                isGranted = true,
                iconName = "battery_charging_full"
            ),
            PermissionItem(
                id = "contacts",
                title = "Contacts (optional)",
                description = "Call & message WhatsApp or phone contacts by voice name.",
                isGranted = false,
                iconName = "contacts"
            )
        )

        val DEFAULT_SCREEN_STATES = listOf(
            ScreenStateItem(
                id = "launcher",
                name = "HomeScreen Launcher",
                iconName = "home",
                category = "System",
                description = "Main Android home screen with app grid, search bar and weather widget.",
                suggestedAction = "Say: 'Open WhatsApp' or 'Clean background apps'"
            ),
            ScreenStateItem(
                id = "whatsapp_contacts",
                name = "WhatsApp Contacts List",
                iconName = "chat",
                category = "Messaging",
                description = "Chat listing showing unread messages and contact status updates.",
                suggestedAction = "Say: 'Send message to Rahul' or 'Read unread chats'"
            ),
            ScreenStateItem(
                id = "youtube_dashboard",
                name = "YouTube Creator Dashboard",
                iconName = "video_library",
                category = "Creation",
                description = "Live creator analytics, subscriber count, and video upload queue.",
                suggestedAction = "Say: 'Suggest viral video title' or 'Generate tags'"
            ),
            ScreenStateItem(
                id = "gmail_compose",
                name = "Gmail Workspace Compose",
                iconName = "email",
                category = "Productivity",
                description = "Drafting new email thread with recipient and subject line inputs.",
                suggestedAction = "Say: 'Draft formal leave request' or 'Refine tone'"
            ),
            ScreenStateItem(
                id = "freefire_lobby",
                name = "Free Fire Battle Lobby",
                iconName = "sports_esports",
                category = "Gaming",
                description = "Squad match waiting area with character loadout and zone map.",
                suggestedAction = "Say: 'Best drop strategy Clock Tower' or 'Check squad stats'"
            )
        )

        val DEFAULT_TASK_GUIDES = listOf(
            TaskGuide(
                id = "whatsapp_setup",
                title = "WhatsApp Setup",
                subtitle = "Guided step-by-step assistant for chat backup, security PIN & new chats.",
                iconName = "chat",
                steps = listOf(
                    TaskStep(1, "Install & Verification", "Open app, verify phone number with SMS OTP.", "SARA: 'OTP aane par bataiye, main auto-fill karungi.'"),
                    TaskStep(2, "Restore Cloud Backup", "Select Google Drive account to recover chat history.", "SARA: 'Latest backup select karke Restore dabaiye.'"),
                    TaskStep(3, "Two-Step Verification", "Setup 6-digit security PIN in Account Settings.", "SARA: 'PIN safe rakhna, ye account protect karega.'")
                )
            ),
            TaskGuide(
                id = "youtube_build",
                title = "YouTube Build",
                subtitle = "Complete workflow for channel optimization, shorts creation & tags.",
                iconName = "smart_display",
                steps = listOf(
                    TaskStep(1, "Channel Branding", "Set attractive channel banner, avatar, and handle.", "SARA: 'Main aapke liye trendy channel description likh sakti hoon.'"),
                    TaskStep(2, "Viral Topic & Script", "Generate high-retention script in Hindi.", "SARA: 'Script ready hai! First 5 seconds hook sabse zaroori hai.'"),
                    TaskStep(3, "Upload & SEO Tags", "Add high-ranking keywords and eye-catching thumbnail.", "SARA: 'SEO score 95% hai, publish karne ke liye taiyaar!'")
                )
            ),
            TaskGuide(
                id = "email_creation",
                title = "Email Creation",
                subtitle = "Crafting professional Hindi/English emails with polite corporate tone.",
                iconName = "mail",
                steps = listOf(
                    TaskStep(1, "Recipient & Subject", "Write high open-rate concise subject line.", "SARA: 'Subject: Urgent Project Milestone Update.'"),
                    TaskStep(2, "Structured Body", "Introduction, core bullet points, and clear call-to-action.", "SARA: 'Draft check kijiye, language polite aur crisp hai.'"),
                    TaskStep(3, "Send & Reminder", "Review attachments and schedule follow-up alert.", "SARA: 'Email sent! Follow-up reminder 2 din baad set kiya gaya.'")
                )
            )
        )
    }

    // Settings
    fun getSettings(): SaraSettings {
        val apiKey = prefs.getString("api_key", "") ?: ""
        val activeModel = prefs.getString("active_model", "gemini-2.5-flash") ?: "gemini-2.5-flash"
        val activePresetId = prefs.getString("active_preset", "sassy_girlfriend") ?: "sassy_girlfriend"
        val voiceInput = prefs.getBoolean("voice_input", true)
        val voiceOutput = prefs.getBoolean("voice_output", true)
        val femaleVoice = prefs.getBoolean("female_voice", true)
        val language = prefs.getString("language", "Hindi (Default)") ?: "Hindi (Default)"
        val speakingRate = prefs.getFloat("speaking_rate", 1.0f)
        val pitch = prefs.getFloat("pitch", 1.15f)

        return SaraSettings(
            apiKey = apiKey,
            activeModel = activeModel,
            activePresetId = activePresetId,
            voiceInputEnabled = voiceInput,
            voiceOutputEnabled = voiceOutput,
            femaleVoice = femaleVoice,
            language = language,
            speakingRate = speakingRate,
            pitch = pitch
        )
    }

    fun saveSettings(settings: SaraSettings) {
        prefs.edit()
            .putString("api_key", settings.apiKey)
            .putString("active_model", settings.activeModel)
            .putString("active_preset", settings.activePresetId)
            .putBoolean("voice_input", settings.voiceInputEnabled)
            .putBoolean("voice_output", settings.voiceOutputEnabled)
            .putBoolean("female_voice", settings.femaleVoice)
            .putString("language", settings.language)
            .putFloat("speaking_rate", settings.speakingRate)
            .putFloat("pitch", settings.pitch)
            .apply()
    }

    // Permissions State
    fun getPermissions(): List<PermissionItem> {
        val grantedString = prefs.getString("permissions_granted", null)
        if (grantedString == null) {
            return DEFAULT_PERMISSIONS
        }
        val set = grantedString.split(",").toSet()
        return DEFAULT_PERMISSIONS.map {
            it.copy(isGranted = set.contains(it.id))
        }
    }

    fun togglePermission(id: String): List<PermissionItem> {
        val current = getPermissions()
        val updated = current.map {
            if (it.id == id) it.copy(isGranted = !it.isGranted) else it
        }
        val grantedIds = updated.filter { it.isGranted }.map { it.id }.joinToString(",")
        prefs.edit().putString("permissions_granted", grantedIds).apply()
        return updated
    }

    // Memories
    fun getMemories(): List<MemoryEntry> {
        val raw = prefs.getString("long_term_memories", null)
        if (raw == null) {
            val initial = listOf(
                MemoryEntry(UUID.randomUUID().toString(), "Language Preference", "Hindi & Hinglish conversational default", "Language"),
                MemoryEntry(UUID.randomUUID().toString(), "User Moniker", "Boss (playful, respectful title)", "User Profile"),
                MemoryEntry(UUID.randomUUID().toString(), "Favorite Melody", "Haule Haule (Shah Rukh Khan, Rab Ne Bana Di Jodi)", "Entertainment"),
                MemoryEntry(UUID.randomUUID().toString(), "Assistant Personality", "Sassy Girlfriend with high tech IQ", "AI Persona"),
                MemoryEntry(UUID.randomUUID().toString(), "Gaming Rank", "Ace Dominator in BGMI Battle Royale", "Gaming"),
                MemoryEntry(UUID.randomUUID().toString(), "Primary Device", "Android Mobile with Agentic Voice HUD", "System")
            )
            saveMemories(initial)
            return initial
        }
        val list = mutableListOf<MemoryEntry>()
        val arr = JSONArray(raw)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                MemoryEntry(
                    id = obj.getString("id"),
                    key = obj.getString("key"),
                    value = obj.getString("value"),
                    category = obj.getString("category"),
                    timestamp = obj.getLong("timestamp")
                )
            )
        }
        return list
    }

    fun saveMemories(list: List<MemoryEntry>) {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("key", it.key)
            obj.put("value", it.value)
            obj.put("category", it.category)
            obj.put("timestamp", it.timestamp)
            arr.put(obj)
        }
        prefs.edit().putString("long_term_memories", arr.toString()).apply()
    }

    fun addMemory(key: String, value: String, category: String): List<MemoryEntry> {
        val current = getMemories().toMutableList()
        current.add(0, MemoryEntry(UUID.randomUUID().toString(), key, value, category))
        saveMemories(current)
        return current
    }

    fun removeMemory(id: String): List<MemoryEntry> {
        val current = getMemories().filterNot { it.id == id }
        saveMemories(current)
        return current
    }

    fun clearMemories(): List<MemoryEntry> {
        prefs.edit().remove("long_term_memories").apply()
        return emptyList()
    }

    // Chat History
    fun getChatHistory(): List<ChatMessage> {
        val raw = prefs.getString("chat_history", null)
        if (raw == null) {
            val defaultHistory = listOf(
                ChatMessage(
                    id = "msg_1",
                    sender = MessageSender.SARA,
                    text = "Namaste Boss! Main SARA hoon, aapki futuristic personal AI voice assistant. ✨ Kahiye, aaj aapke liye kya karna hai?",
                    timestamp = System.currentTimeMillis() - 600000,
                    isHindi = true
                ),
                ChatMessage(
                    id = "msg_2",
                    sender = MessageSender.USER,
                    text = "Sara, Haule Haule song play kar sakti ho?",
                    timestamp = System.currentTimeMillis() - 480000,
                    isHindi = true
                ),
                ChatMessage(
                    id = "msg_3",
                    sender = MessageSender.SARA,
                    text = "Arey bilkul! Haule haule se hawa lagti hai... Haule haule se dawa lagti hai! 🎶 Aapka favorite song ready hai!",
                    timestamp = System.currentTimeMillis() - 470000,
                    isHindi = true
                ),
                ChatMessage(
                    id = "msg_4",
                    sender = MessageSender.USER,
                    text = "Awesome! What are your active capabilities?",
                    timestamp = System.currentTimeMillis() - 200000,
                    isHindi = false
                ),
                ChatMessage(
                    id = "msg_5",
                    sender = MessageSender.SARA,
                    text = "Main Hindi/English mein baat kar sakti hoon, Gemini AI brain se questions answer karti hoon, screen scroll & click simulate karti hoon, aur long-term memory save rakhti hoon! 💜",
                    timestamp = System.currentTimeMillis() - 190000,
                    isHindi = true
                )
            )
            saveChatHistory(defaultHistory)
            return defaultHistory
        }
        val list = mutableListOf<ChatMessage>()
        val arr = JSONArray(raw)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(
                ChatMessage(
                    id = obj.getString("id"),
                    sender = if (obj.getString("sender") == "USER") MessageSender.USER else MessageSender.SARA,
                    text = obj.getString("text"),
                    timestamp = obj.getLong("timestamp"),
                    isHindi = obj.optBoolean("isHindi", true),
                    status = obj.optString("status", "Delivered")
                )
            )
        }
        return list
    }

    fun saveChatHistory(list: List<ChatMessage>) {
        val arr = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("id", it.id)
            obj.put("sender", it.sender.name)
            obj.put("text", it.text)
            obj.put("timestamp", it.timestamp)
            obj.put("isHindi", it.isHindi)
            obj.put("status", it.status)
            arr.put(obj)
        }
        prefs.edit().putString("chat_history", arr.toString()).apply()
    }

    fun addChatMessage(sender: MessageSender, text: String, isHindi: Boolean = true): ChatMessage {
        val history = getChatHistory().toMutableList()
        val msg = ChatMessage(UUID.randomUUID().toString(), sender, text, System.currentTimeMillis(), isHindi)
        history.add(msg)
        saveChatHistory(history)
        return msg
    }

    fun clearChatHistory(): List<ChatMessage> {
        prefs.edit().remove("chat_history").apply()
        return emptyList()
    }
}
