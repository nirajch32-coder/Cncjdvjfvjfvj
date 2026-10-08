package com.example.manager

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.provider.Settings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class InteractionStep {
    IDLE,
    LISTENING,
    UNDERSTANDING,
    FINDING_UI_ELEMENT,
    CONFIRMATION,
    EXECUTING,
    COMPLETED,
    ERROR
}

data class InteractionState(
    val step: InteractionStep = InteractionStep.IDLE,
    val currentCommand: String = "",
    val targetElement: String? = null,
    val feedbackMessage: String = ""
)

object VoiceCommandManager {

    private val _interactionState = MutableStateFlow(InteractionState())
    val interactionState: StateFlow<InteractionState> = _interactionState.asStateFlow()

    suspend fun executePhoneCommand(
        command: String,
        context: Context,
        onSpeechResponse: (String) -> Unit
    ): Boolean {
        val lower = command.lowercase().trim()

        _interactionState.value = InteractionState(
            step = InteractionStep.UNDERSTANDING,
            currentCommand = command,
            feedbackMessage = "Analyzing voice command: \"$command\""
        )
        delay(350)

        // 1. WhatsApp Assistant Command
        if (lower.contains("whatsapp")) {
            val pendingTask = WhatsAppAssistant.parseWhatsAppCommand(command, context)
            if (pendingTask != null) {
                _interactionState.value = InteractionState(
                    step = InteractionStep.CONFIRMATION,
                    currentCommand = command,
                    targetElement = "WhatsApp Chat: ${pendingTask.recipientName}",
                    feedbackMessage = "${pendingTask.recipientName} ke WhatsApp chat mein ye message ready hai: '${pendingTask.messageText}'. Send karna hai?"
                )
                onSpeechResponse("${pendingTask.recipientName} ke WhatsApp chat mein ye message ready hai: '${pendingTask.messageText}'. Send karna hai?")
                return true
            }
        }

        // 2. Click Command ("Is button par click karo", "Click on Submit", etc.)
        if (lower.contains("click") || lower.contains("dabao") || lower.contains("tap")) {
            _interactionState.value = InteractionState(
                step = InteractionStep.FINDING_UI_ELEMENT,
                currentCommand = command,
                feedbackMessage = "Scanning active screen UI hierarchy..."
            )
            delay(500)

            // Extract target text
            val targetText = command
                .replace(Regex("(?i)click\\s*(on)?"), "")
                .replace(Regex("(?i)is\\s*button\\s*par\\s*click\\s*karo"), "")
                .replace(Regex("(?i)par\\s*click\\s*karo"), "")
                .replace(Regex("(?i)tap\\s*(on)?"), "")
                .trim(' ', '"', '\'')

            val finalTarget = if (targetText.isNotBlank()) targetText else "Next"

            if (AccessibilityController.isAvailable) {
                _interactionState.value = InteractionState(
                    step = InteractionStep.EXECUTING,
                    currentCommand = command,
                    targetElement = finalTarget,
                    feedbackMessage = "Executing click on element: '$finalTarget'"
                )
                val clicked = AccessibilityController.clickElement(finalTarget)
                delay(400)

                if (clicked) {
                    _interactionState.value = InteractionState(
                        step = InteractionStep.COMPLETED,
                        currentCommand = command,
                        targetElement = finalTarget,
                        feedbackMessage = "Successfully clicked '$finalTarget'!"
                    )
                    onSpeechResponse("Element '$finalTarget' par click successfully execute ho gaya!")
                    return true
                } else {
                    _interactionState.value = InteractionState(
                        step = InteractionStep.ERROR,
                        currentCommand = command,
                        feedbackMessage = "Target '$finalTarget' not found on visible screen."
                    )
                    onSpeechResponse("Visible screen par '$finalTarget' element nahi mila.")
                    return false
                }
            } else {
                _interactionState.value = InteractionState(
                    step = InteractionStep.ERROR,
                    feedbackMessage = "Accessibility Service is not enabled."
                )
                onSpeechResponse("UI click karne ke liye SARA Accessibility Service enable kijiye.")
                return false
            }
        }

        // 3. Scroll Down ("Scroll karo", "Neeche scroll karo")
        if (lower.contains("scroll")) {
            _interactionState.value = InteractionState(
                step = InteractionStep.EXECUTING,
                currentCommand = command,
                feedbackMessage = "Executing vertical scroll gesture..."
            )
            delay(300)

            val scrolled = if (lower.contains("up") || lower.contains("upar")) {
                AccessibilityController.scrollUp()
            } else {
                AccessibilityController.scrollDown()
            }

            _interactionState.value = InteractionState(
                step = InteractionStep.COMPLETED,
                currentCommand = command,
                feedbackMessage = "Scroll executed successfully!"
            )
            onSpeechResponse("Screen scroll complete ho gaya!")
            return scrolled
        }

        // 4. Back Navigation ("Back karo", "Peeche jao")
        if (lower.contains("back")) {
            _interactionState.value = InteractionState(
                step = InteractionStep.EXECUTING,
                currentCommand = command,
                feedbackMessage = "Navigating back..."
            )
            delay(250)
            val backOk = AccessibilityController.performBack()
            _interactionState.value = InteractionState(
                step = InteractionStep.COMPLETED,
                feedbackMessage = "Back action executed."
            )
            onSpeechResponse("Back action complete!")
            return backOk
        }

        // 5. Home Navigation ("Home par jao", "Home screen kholo")
        if (lower.contains("home")) {
            _interactionState.value = InteractionState(
                step = InteractionStep.EXECUTING,
                currentCommand = command,
                feedbackMessage = "Navigating to Home screen..."
            )
            delay(250)
            val homeOk = AccessibilityController.performHome()
            _interactionState.value = InteractionState(
                step = InteractionStep.COMPLETED,
                feedbackMessage = "Returned to Home screen."
            )
            onSpeechResponse("Home screen par aa gaye hain!")
            return homeOk
        }

        // 6. Settings kholo
        if (lower.contains("settings kholo") || lower.contains("open settings")) {
            _interactionState.value = InteractionState(
                step = InteractionStep.EXECUTING,
                currentCommand = command,
                feedbackMessage = "Opening Android System Settings..."
            )
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            _interactionState.value = InteractionState(
                step = InteractionStep.COMPLETED,
                feedbackMessage = "System Settings opened."
            )
            onSpeechResponse("Android Settings open kar diya gaya hai.")
            return true
        }

        // 7. YouTube kholo
        if (lower.contains("youtube kholo") || lower.contains("open youtube")) {
            _interactionState.value = InteractionState(
                step = InteractionStep.EXECUTING,
                currentCommand = command,
                feedbackMessage = "Launching YouTube..."
            )
            val intent = context.packageManager.getLaunchIntentForPackage("com.google.android.youtube")
                ?: Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://youtube.com")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            context.startActivity(intent)
            _interactionState.value = InteractionState(
                step = InteractionStep.COMPLETED,
                feedbackMessage = "YouTube launched."
            )
            onSpeechResponse("YouTube open kar diya gaya hai!")
            return true
        }

        // 8. Camera kholo
        if (lower.contains("camera kholo") || lower.contains("open camera")) {
            _interactionState.value = InteractionState(
                step = InteractionStep.EXECUTING,
                currentCommand = command,
                feedbackMessage = "Launching Camera..."
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(intent)
                _interactionState.value = InteractionState(
                    step = InteractionStep.COMPLETED,
                    feedbackMessage = "Camera launched."
                )
                onSpeechResponse("Camera khol diya gaya hai!")
                return true
            } catch (e: Exception) {
                // Ignore
            }
        }

        // Not a phone navigation command -> handled by Gemini AI
        _interactionState.value = InteractionState(step = InteractionStep.IDLE)
        return false
    }

    fun resetState() {
        _interactionState.value = InteractionState(step = InteractionStep.IDLE)
    }
}
