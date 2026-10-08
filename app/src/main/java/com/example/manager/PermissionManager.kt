package com.example.manager

import android.Manifest
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.service.SaraAccessibilityService

data class PermissionStatus(
    val id: String,
    val title: String,
    val isGranted: Boolean,
    val statusText: String,
    val explanation: String
)

data class PermissionTestResult(
    val id: String,
    val title: String,
    val isGranted: Boolean,
    val detail: String
)

object PermissionManager {

    // ================= REAL PERMISSION CHECKS =================

    fun isMicrophoneGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        if (SaraAccessibilityService.isServiceRunning.value) return true

        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expectedPackage = context.packageName

        return enabledServices.any {
            it.resolveInfo.serviceInfo.packageName == expectedPackage &&
                    it.resolveInfo.serviceInfo.name.contains("SaraAccessibilityService")
        }
    }

    fun isOverlayGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isNotificationsGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED &&
                    NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun isExactAlarmGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.canScheduleExactAlarms() ?: false
        } else {
            true
        }
    }

    fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    fun isContactsGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // ================= GET FULL STATUS LIST =================

    fun getAllPermissionsStatus(context: Context): List<PermissionStatus> {
        return listOf(
            PermissionStatus(
                id = "mic",
                title = "Microphone",
                isGranted = isMicrophoneGranted(context),
                statusText = if (isMicrophoneGranted(context)) "ON" else "OFF",
                explanation = "Required for real-time speech recognition & Hindi voice commands."
            ),
            PermissionStatus(
                id = "accessibility",
                title = "Accessibility Service",
                isGranted = isAccessibilityServiceEnabled(context),
                statusText = if (isAccessibilityServiceEnabled(context)) "ON" else "OFF",
                explanation = "Enables user-authorized UI interactions: clicking buttons, scrolling, and navigating apps."
            ),
            PermissionStatus(
                id = "overlay",
                title = "Display Over Other Apps",
                isGranted = isOverlayGranted(context),
                statusText = if (isOverlayGranted(context)) "ON" else "OFF",
                explanation = "Enables draggable neon floating SARA assistant bubble across any app."
            ),
            PermissionStatus(
                id = "notifications",
                title = "Notifications",
                isGranted = isNotificationsGranted(context),
                statusText = if (isNotificationsGranted(context)) "ON" else "OFF",
                explanation = "Displays assistant listening state, voice alerts, and background service status."
            ),
            PermissionStatus(
                id = "alarms",
                title = "Alarms & Reminders",
                isGranted = isExactAlarmGranted(context),
                statusText = if (isExactAlarmGranted(context)) "ON" else "OFF",
                explanation = "Required to schedule exact voice reminders and morning briefings."
            ),
            PermissionStatus(
                id = "battery",
                title = "Ignore Battery Optimization",
                isGranted = isBatteryOptimizationIgnored(context),
                statusText = if (isBatteryOptimizationIgnored(context)) "ON" else "OFF",
                explanation = "Prevents OS from killing SARA's voice detection service during screen lock."
            ),
            PermissionStatus(
                id = "contacts",
                title = "Contacts (optional)",
                isGranted = isContactsGranted(context),
                statusText = if (isContactsGranted(context)) "ON" else "OFF",
                explanation = "Find contacts by voice name (e.g., 'Rahul ko message bhejo'). Optional."
            )
        )
    }

    // ================= REAL SYSTEM SETTINGS INTENTS =================

    fun openMicrophoneSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openOverlaySettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        } else {
            Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        }
        context.startActivity(intent)
    }

    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        context.startActivity(intent)
    }

    fun openAlarmSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                Uri.parse("package:${context.packageName}")
            ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }
        context.startActivity(intent)
    }

    fun openBatteryOptimizationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}")
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            } catch (e: Exception) {
                Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            }
        } else {
            Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
        }
        context.startActivity(intent)
    }

    fun openContactsSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    // ================= REAL DIAGNOSTIC TEST =================
    fun testAllPermissions(context: Context): List<PermissionTestResult> {
        val mic = isMicrophoneGranted(context)
        val acc = isAccessibilityServiceEnabled(context)
        val ovl = isOverlayGranted(context)
        val notif = isNotificationsGranted(context)
        val alarm = isExactAlarmGranted(context)
        val batt = isBatteryOptimizationIgnored(context)
        val cont = isContactsGranted(context)

        return listOf(
            PermissionTestResult("mic", "Microphone", mic, if (mic) "Granted (RECORD_AUDIO active)" else "Denied (Record audio unavailable)"),
            PermissionTestResult("accessibility", "Accessibility Service", acc, if (acc) "Enabled (SaraAccessibilityService bound)" else "Disabled (Go to Accessibility Settings)"),
            PermissionTestResult("overlay", "Display Over Other Apps", ovl, if (ovl) "Granted (SYSTEM_ALERT_WINDOW active)" else "Disabled (Can Draw Overlays off)"),
            PermissionTestResult("notifications", "Notifications", notif, if (notif) "Enabled (POST_NOTIFICATIONS active)" else "Disabled (Notifications blocked)"),
            PermissionTestResult("alarms", "Alarms & Reminders", alarm, if (alarm) "Granted (SCHEDULE_EXACT_ALARM active)" else "Denied (Exact alarms blocked)"),
            PermissionTestResult("battery", "Ignore Battery Opt", batt, if (batt) "Exempt (Background wake protected)" else "Optimized (OS may throttle background)"),
            PermissionTestResult("contacts", "Contacts", cont, if (cont) "Granted (READ_CONTACTS active)" else "Denied (Optional contact search off)")
        )
    }
}
