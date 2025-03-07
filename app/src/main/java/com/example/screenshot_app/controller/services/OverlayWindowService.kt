package com.example.screenshot_app.controller.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.example.screenshot_app.R
import kotlin.math.absoluteValue

class OverlayWindowService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatView: View
    private var isVisible = true
    private val tag = "FloatingWindowService"

    companion object {
        const val ACTION_HIDE = "com.screen.shot.HIDE"
        const val ACTION_SHOW = "com.screen.shot.SHOW"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Service created")
        setupOverlayWindow()
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    private fun setupOverlayWindow() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = createWindowParams()
        floatView = LayoutInflater.from(this).inflate(R.layout.layout_overlay_window, null)
        val imgCamera = floatView.findViewById<ImageView>(R.id.imgCamera)
        imgCamera.setOnTouchListener(createTouchListenerOverlay(params))
        try {
            windowManager.addView(floatView, params)
            Log.e(tag, "add view successfully")
        } catch (e: Exception) {
            Log.e(tag, "can not add view: ${e.message}")
        }
    }

    private fun createWindowParams(): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 0
            y = 100
        }
    }

    private fun createTouchListenerOverlay(params: WindowManager.LayoutParams): View.OnTouchListener {
        return object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isClick = true

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isClick = true
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val dx = (event.rawX - initialTouchX).toInt()
                        val dy = (event.rawY - initialTouchY).toInt()

                        // Nếu di chuyển đủ xa, coi như kéo (không phải click)
                        if (dx.absoluteValue > 10 || dy.absoluteValue > 10) {
                            isClick = false
                            params.x = initialX + dx
                            params.y = initialY + dy
                            windowManager.updateViewLayout(floatView, params)
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (isClick) {
                            takeScreenshot()  // Chỉ gọi onClick khi không có di chuyển
                        }
                        return true
                    }

                }
                return false
            }
        }
    }

    private fun takeScreenshot() {
        val intent = Intent(this, ScreenshotService::class.java).apply {
            action = ScreenshotService.ACTION_TAKE_SCREENSHOT
        }
        startService(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "onStartCommand: ${intent?.action}")
        when (intent?.action) {
            ACTION_HIDE -> hideButton()
            ACTION_SHOW -> showButton()
        }
        return START_NOT_STICKY
    }

    private fun hideButton() {
        if (isVisible) {
            floatView.visibility = View.GONE
            isVisible = false
            Log.d(tag, "Floating button hidden")
        }
    }

    private fun showButton() {
        if (!isVisible) {
            floatView.visibility = View.VISIBLE
            isVisible = true
            Log.d(tag, "Floating button shown")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatView)
        Log.d(tag, "Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}