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
import com.example.screenshot_app.controller.services.interfaces.UploadServiceInterface
import com.example.screenshot_app.controller.client.ApiConfigManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            upload(uri)
        }
    }

    private fun upload(imageUri: Uri) {
        val apiConfig: Map<String, String> = ApiConfigManager.getApiConfig(this)
        var baseUrl = apiConfig["baseUrl"]
        Log.e("upload: ", baseUrl.toString())
        var token = apiConfig["token"]
        if(baseUrl.isNullOrEmpty()){
            baseUrl = "https://cms.bolttech.space"
        }
        if(token.isNullOrEmpty()){
            token = "KjzcGYUfUJQdLca7SEx2hpZkXe5STOwj"
        }
        Log.e("upload: ", token.toString())
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "https://$baseUrl" // Thêm scheme nếu thiếu
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl.toString())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(UploadServiceInterface::class.java)

        val file = File(getRealPathFromURI(imageUri))
        val requestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
        // Thực hiện request
        val authorization = "Bearer $token"
        apiService.uploadFile(authorization, multipartBody)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        // Đọc JSON response nếu có
                        val jsonString = response.body()?.string() ?: return
                        val jsonObject = JSONObject(jsonString)
                        val dataObject = jsonObject.getJSONObject("data")

                        val id = dataObject.getString("id")
                        val storage = dataObject.getString("storage")
                        val filenameDownload = dataObject.getString("filename_download")

                        Log.d(
                            "Upload Success",
                            "id: $id, storage: $storage, filename_download: $filenameDownload"
                        )
                        val sendImageService = SendImageDataToServer(this@ScreenshotService)
                        sendImageService.sendImageDataToServer(id, storage, filenameDownload)
                    } else {
                        Log.e("Upload Error", "Response Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("Upload Error", "Exception: ${t.message}")
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