package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SaraAccessibilityService : AccessibilityService() {

    companion object {
        var instance: SaraAccessibilityService? = null
            private set

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private val _lastDetectedApp = MutableStateFlow<String?>(null)
        val lastDetectedApp: StateFlow<String?> = _lastDetectedApp.asStateFlow()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceRunning.value = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString()
        if (!pkg.isNullOrBlank()) {
            _lastDetectedApp.value = pkg
        }
    }

    override fun onInterrupt() {
        // Accessibility service interrupted
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        _isServiceRunning.value = false
    }

    // ================= GLOBAL ACTIONS =================
    fun performBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun performHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun performRecents(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    // ================= SCROLLING =================
    fun performScrollDown(): Boolean {
        val root = rootInActiveWindow ?: return performSwipeGesture(swipeDown = true)
        val scrollable = findFirstScrollableNode(root)
        val result = scrollable?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD) ?: false
        scrollable?.recycle()
        root.recycle()
        return if (!result) performSwipeGesture(swipeDown = true) else true
    }

    fun performScrollUp(): Boolean {
        val root = rootInActiveWindow ?: return performSwipeGesture(swipeDown = false)
        val scrollable = findFirstScrollableNode(root)
        val result = scrollable?.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD) ?: false
        scrollable?.recycle()
        root.recycle()
        return if (!result) performSwipeGesture(swipeDown = false) else true
    }

    private fun findFirstScrollableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isScrollable) return node

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findFirstScrollableNode(child)
            if (found != null) {
                if (child != found) child.recycle()
                return found
            }
            child.recycle()
        }
        return null
    }

    private fun performSwipeGesture(swipeDown: Boolean): Boolean {
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels.toFloat()
        val height = displayMetrics.heightPixels.toFloat()

        val startX = width / 2f
        val startY = if (swipeDown) height * 0.75f else height * 0.25f
        val endY = if (swipeDown) height * 0.25f else height * 0.75f

        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(startX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()

        return dispatchGesture(gesture, null, null)
    }

    // ================= CLICK ACTIONS =================
    fun clickElementWithText(targetText: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val cleanTarget = targetText.trim().lowercase()

        val matchingNodes = mutableListOf<AccessibilityNodeInfo>()
        collectClickableNodesWithText(root, cleanTarget, matchingNodes)

        var clicked = false
        for (node in matchingNodes) {
            if (performClickOnNodeOrParent(node)) {
                clicked = true
                node.recycle()
                break
            }
            node.recycle()
        }
        root.recycle()
        return clicked
    }

    fun clickElementWithId(resourceId: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val nodes = root.findAccessibilityNodeInfosByViewId(resourceId)
        var clicked = false
        for (node in nodes) {
            if (performClickOnNodeOrParent(node)) {
                clicked = true
                node.recycle()
                break
            }
            node.recycle()
        }
        root.recycle()
        return clicked
    }

    private fun collectClickableNodesWithText(
        node: AccessibilityNodeInfo?,
        targetText: String,
        outList: MutableList<AccessibilityNodeInfo>
    ) {
        if (node == null) return

        val text = node.text?.toString()?.lowercase()
        val desc = node.contentDescription?.toString()?.lowercase()

        val textMatches = text?.contains(targetText) == true || desc?.contains(targetText) == true
        if (textMatches && (node.isClickable || node.parent?.isClickable == true)) {
            outList.add(AccessibilityNodeInfo.obtain(node))
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            collectClickableNodesWithText(child, targetText, outList)
            child.recycle()
        }
    }

    private fun performClickOnNodeOrParent(node: AccessibilityNodeInfo?): Boolean {
        var current = node
        while (current != null) {
            if (current.isClickable) {
                return current.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            val parent = current.parent
            if (current != node) current.recycle()
            current = parent
        }
        return false
    }

    // ================= TEXT INPUT =================
    fun typeTextIntoFocusedInput(textToType: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        val target = focused ?: findFirstEditableNode(root)

        val success = if (target != null && target.isEditable) {
            val args = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, textToType)
            }
            target.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        } else false

        target?.recycle()
        root.recycle()
        return success
    }

    private fun findFirstEditableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isEditable) return node

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findFirstEditableNode(child)
            if (found != null) {
                if (child != found) child.recycle()
                return found
            }
            child.recycle()
        }
        return null
    }

    // ================= SAFE UI TREE INSPECTION =================
    fun getVisibleScreenLabels(maxCount: Int = 12): List<String> {
        val root = rootInActiveWindow ?: return emptyList()
        val labels = mutableListOf<String>()
        collectLabels(root, labels, maxCount)
        root.recycle()
        return labels
    }

    private fun collectLabels(node: AccessibilityNodeInfo?, list: MutableList<String>, maxCount: Int) {
        if (node == null || list.size >= maxCount) return

        val text = node.text?.toString()?.trim()
        val desc = node.contentDescription?.toString()?.trim()

        if (!text.isNullOrBlank() && text.length in 2..50 && !node.isPassword) {
            list.add(text)
        } else if (!desc.isNullOrBlank() && desc.length in 2..50 && !node.isPassword) {
            list.add(desc)
        }

        for (i in 0 until node.childCount) {
            if (list.size >= maxCount) break
            val child = node.getChild(i) ?: continue
            collectLabels(child, list, maxCount)
            child.recycle()
        }
    }
}
