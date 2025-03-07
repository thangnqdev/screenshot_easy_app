package com.example.screenshot_app.controller.services.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import com.example.screenshot_app.controller.services.OverlayWindowService
import com.example.screenshot_app.controller.services.ScreenshotService

// Check Accessibility da duoc bat chua
fun Context.isAccessibilityServiceEnabled(): Boolean {
    val enabledServices = Settings.Secure.getString(
        contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false

    val colonSplitter = enabledServices.split(":")
    val packageName = "$packageName/${ScreenshotService::class.java.name}"

    return colonSplitter.contains(packageName)
}

// Quyen Accessibility
fun Context.requestAccessibilityPermission() {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    startActivity(intent)
    Toast.makeText(this, "Ứng dụng đã tải xuống: Screenshot_app", Toast.LENGTH_LONG).show()
}

// Quyen overlay
fun Context.requestOverlayPermission() {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:$packageName")
    )
    startActivity(intent)
    Toast.makeText(this, "Screenshot_app", Toast.LENGTH_LONG).show()
}

//Hien thi overlay
fun Context.showOverlay() {
    val intent = Intent(this, OverlayWindowService::class.java).apply {
        action = OverlayWindowService.ACTION_SHOW
    }
    startService(intent)
}

//Tat hien thi overlay
fun Context.hideOverlay() {
    val intent = Intent(this, OverlayWindowService::class.java).apply {
        action = OverlayWindowService.ACTION_HIDE
    }
    startService(intent)

}