package com.example.manager

import com.example.service.SaraAccessibilityService

object AccessibilityController {

    val isAvailable: Boolean
        get() = SaraAccessibilityService.instance != null

    fun performBack(): Boolean {
        return SaraAccessibilityService.instance?.performBack() ?: false
    }

    fun performHome(): Boolean {
        return SaraAccessibilityService.instance?.performHome() ?: false
    }

    fun performRecents(): Boolean {
        return SaraAccessibilityService.instance?.performRecents() ?: false
    }

    fun scrollDown(): Boolean {
        return SaraAccessibilityService.instance?.performScrollDown() ?: false
    }

    fun scrollUp(): Boolean {
        return SaraAccessibilityService.instance?.performScrollUp() ?: false
    }

    fun clickElement(textOrDescription: String): Boolean {
        return SaraAccessibilityService.instance?.clickElementWithText(textOrDescription) ?: false
    }

    fun typeText(text: String): Boolean {
        return SaraAccessibilityService.instance?.typeTextIntoFocusedInput(text) ?: false
    }

    fun getVisibleScreenLabels(): List<String> {
        return SaraAccessibilityService.instance?.getVisibleScreenLabels() ?: emptyList()
    }
}
