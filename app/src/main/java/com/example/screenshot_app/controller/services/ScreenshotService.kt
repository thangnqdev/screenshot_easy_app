package com.example.screenshot_app.controller.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import com.example.screenshot_app.controller.client.SendImageDataToServer
import com.example.screenshot_app.controller.constant.Constant
import com.example.screenshot_app.controller.interfaces.UploadInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ScreenshotService : AccessibilityService() {
    companion object {
        const val ACTION_START = "com.stormx.shot.START"
        const val ACTION_TAKE_SCREENSHOT = "com.stormx.shot.TAKE_SCREENSHOT"

        private var instance: ScreenshotService? = null
        private var isServiceRunning = false

         fun isRunning(context: Context): Boolean = isServiceRunning

        fun takeScreenshot() {
            instance?.performScreenshot()
        }
    }

    override fun onServiceConnected() {
        instance = this
        isServiceRunning = true
        val floatingIntent = Intent(this, OverlayWindowService::class.java)
        startService(floatingIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {}
            ACTION_TAKE_SCREENSHOT -> {
                performScreenshot()
            }
        }
        return START_STICKY
    }

    private fun performScreenshot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val hideIntent = Intent(this, OverlayWindowService::class.java).apply {
                action = OverlayWindowService.ACTION_HIDE
            }
            startService(hideIntent)

            Handler(Looper.getMainLooper()).postDelayed({
                takeScreenshot(
                    Display.DEFAULT_DISPLAY,
                    mainExecutor,
                    object : TakeScreenshotCallback {
                        override fun onSuccess(screenshot: ScreenshotResult) {
                            val bitmap = Bitmap.wrapHardwareBuffer(
                                screenshot.hardwareBuffer,
                                screenshot.colorSpace
                            )
                            bitmap?.let { saveScreenshot(it) }

                            Handler(Looper.getMainLooper()).post {
                                val showIntent = Intent(
                                    this@ScreenshotService,
                                    OverlayWindowService::class.java
                                ).apply {
                                    action = OverlayWindowService.ACTION_SHOW
                                }
                                startService(showIntent)
                            }
                        }

                        override fun onFailure(errorCode: Int) {
                            Handler(Looper.getMainLooper()).post {
                                val showIntent = Intent(
                                    this@ScreenshotService,
                                    OverlayWindowService::class.java
                                ).apply {
                                    action = OverlayWindowService.ACTION_SHOW
                                }
                                startService(showIntent)
                            }
                        }
                    })
            }, 100)
        }
    }

    private fun saveScreenshot(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Screenshot_$timeStamp.png"
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }
        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { imageUri ->
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            upLoadImageToServer(imageUri)
        }
    }
    private fun upLoadImageToServer(imageUri: Uri) {
        val file = File(getRealPathFromURI(imageUri))
        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val retrofit = UploadFileService.getClient().create(UploadInterface::class.java)
        val call = retrofit.uploadImage(body, "Bearer " + Constant.TOKEN)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val jsonString = response.body()?.string() ?: return
                    val jsonObject = JSONObject(jsonString)
                    val dataObject = jsonObject.getJSONObject("data")

                    val id = dataObject.getString("id")
                    val storage = dataObject.getString("storage")
                    val filenameDownload = dataObject.getString("filename_download")

                    Log.d("JSON Data", "id: $id, storage: $storage, filename_download: $filenameDownload")
                    val sendImageService = SendImageDataToServer(this@ScreenshotService)
                    sendImageService.sendImageDataToServer(id, storage, filenameDownload)

                } else {
                    Log.e("onResponse", "Lỗi: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("onFailure", "Lỗi: ${t.message}")
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            if (columnIndex != -1) {
                cursor.moveToFirst()
                filePath = cursor.getString(columnIndex)
            }
        }
        return filePath
    }


    override fun onInterrupt() {}
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        instance = null
    }
}