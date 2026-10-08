package com.example.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder

data class WhatsAppPendingTask(
    val recipientName: String,
    val messageText: String,
    val phoneNumber: String? = null,
    val status: WhatsAppStatus = WhatsAppStatus.PENDING_CONFIRMATION
)

enum class WhatsAppStatus {
    IDLE,
    PARSING_COMMAND,
    PENDING_CONFIRMATION,
    OPENING_WHATSAPP,
    INSPECTING_UI,
    READY_TO_SEND,
    COMPLETED,
    FAILED,
    CANCELLED
}

object WhatsAppAssistant {

    private val _currentTask = MutableStateFlow<WhatsAppPendingTask?>(null)
    val currentTask: StateFlow<WhatsAppPendingTask?> = _currentTask.asStateFlow()

    fun parseWhatsAppCommand(command: String, context: Context): WhatsAppPendingTask? {
        val lower = command.lowercase()
        if (!lower.contains("whatsapp")) return null

        var recipient = ""
        var message = ""

        // Common patterns:
        // "<Name> ko whatsapp par message bhejo: <Message>"
        // "<Name> ko whatsapp pe bol do ki <Message>"
        // "whatsapp par <Name> ko message karo: <Message>"

        val messageSeparators = listOf(":", "ki ", "message: ", "bol do ki ", "bhejo: ", "likh do ki ")
        var splitIndex = -1
        var chosenSeparator = ""

        for (sep in messageSeparators) {
            val idx = lower.indexOf(sep)
            if (idx != -1 && (splitIndex == -1 || idx < splitIndex)) {
                splitIndex = idx
                chosenSeparator = sep
            }
        }

        if (splitIndex != -1) {
            val before = command.substring(0, splitIndex).trim()
            message = command.substring(splitIndex + chosenSeparator.length).trim()

            // Extract recipient name before "ko" or "par"
            val beforeLower = before.lowercase()
            val koIndex = beforeLower.lastIndexOf(" ko")
            if (koIndex != -1) {
                recipient = before.substring(0, koIndex)
                    .replace(Regex("(?i)whatsapp\\s*(par|pe)?"), "")
                    .replace(Regex("(?i)send|bhejo|bolo"), "")
                    .trim()
            } else {
                recipient = before
                    .replace(Regex("(?i)whatsapp\\s*(par|pe)?"), "")
                    .replace(Regex("(?i)send|message|to"), "")
                    .trim()
            }
        } else {
            // Fallback pattern
            val words = command.split(" ")
            val koIdx = words.indexOfFirst { it.equals("ko", ignoreCase = true) }
            if (koIdx > 0) {
                recipient = words.subList(0, koIdx).joinToString(" ").replace("(?i)whatsapp", "").trim()
                message = words.subList(koIdx + 1, words.size).joinToString(" ").trim()
            }
        }

        if (recipient.isBlank()) recipient = "Friend"
        if (message.isBlank()) message = "Hello"

        // Clean quotes and prefixes
        message = message.trim('"', '\'', ' ')

        // Look up contact phone number if contacts permission is available
        val matchedContacts = ContactManager.searchContactsByName(context, recipient)
        val phone = matchedContacts.firstOrNull()?.phoneNumber

        val task = WhatsAppPendingTask(
            recipientName = matchedContacts.firstOrNull()?.name ?: recipient,
            messageText = message,
            phoneNumber = phone,
            status = WhatsAppStatus.PENDING_CONFIRMATION
        )
        _currentTask.value = task
        return task
    }

    suspend fun executeConfirmedTask(context: Context, autoClickSend: Boolean = false): Boolean {
        val task = _currentTask.value ?: return false
        _currentTask.value = task.copy(status = WhatsAppStatus.OPENING_WHATSAPP)

        try {
            val encodedMessage = URLEncoder.encode(task.messageText, "UTF-8")
            val phone = task.phoneNumber?.replace("+", "")?.replace(" ", "")

            val intent = if (!phone.isNullOrBlank()) {
                // Direct WhatsApp chat URL intent
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$encodedMessage")
                    setPackage("com.whatsapp")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            } else {
                // Generic send intent targeting WhatsApp
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, task.messageText)
                    setPackage("com.whatsapp")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }

            // Verify WhatsApp is installed
            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
            } else {
                // Open browser fallback
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?text=$encodedMessage")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
            }

            // If accessibility service is enabled, assist with typing and stopping before final send
            if (AccessibilityController.isAvailable) {
                _currentTask.value = task.copy(status = WhatsAppStatus.INSPECTING_UI)
                delay(1200)

                // If message input was not filled, type it
                AccessibilityController.typeText(task.messageText)
                _currentTask.value = task.copy(status = WhatsAppStatus.READY_TO_SEND)

                if (autoClickSend) {
                    delay(800)
                    AccessibilityController.clickElement("Send")
                    AccessibilityController.clickElement("send")
                }
            }

            _currentTask.value = task.copy(status = WhatsAppStatus.COMPLETED)
            return true
        } catch (e: Exception) {
            _currentTask.value = task.copy(status = WhatsAppStatus.FAILED)
            return false
        }
    }

    fun cancelTask() {
        _currentTask.value = _currentTask.value?.copy(status = WhatsAppStatus.CANCELLED)
    }

    fun clearTask() {
        _currentTask.value = null
    }
}
