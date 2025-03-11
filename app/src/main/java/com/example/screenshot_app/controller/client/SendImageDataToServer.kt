package com.example.screenshot_app.controller.client

import android.content.Context
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendImageDataToServer(private val context: Context) {

    fun sendImageDataToServer(id: String, storage: String, filenameDownload: String) {
        val apiConfig: Map<String, String> = ApiConfigManager.getApiConfig(context)
        var baseUrl = apiConfig["baseUrl"]
        Log.e("upload: ", baseUrl.toString())
        if(baseUrl.isNullOrEmpty()){
            baseUrl = "https://cms.bolttech.space"
        }
        var token = apiConfig["token"]
        if(token.isNullOrEmpty()){
            token = "KjzcGYUfUJQdLca7SEx2hpZkXe5STOwj"
        }
        Log.e("upload: ", token.toString())

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            baseUrl = "https://$baseUrl"
        }

        // Lấy projectId từ SharedPreferences
        val projectId = getProjectIdFromMainActivity()
        if (projectId.isNullOrEmpty()) {
            Log.e("sendImageDataToServer: ", "projectId is null or empty")
            return
        }

        // Tạo mutation query cho GraphQL
        val mutationQuery = JSONObject().apply {
            put(
                "query", """
            mutation {
                create_projects_files_item(data: {
                    projects_id: "$projectId",
                    directus_files_id: {
                        id: "$id",
                        storage: "$storage",
                        filename_download: "$filenameDownload"
                    }
                }) {
                    id
                }
            }
            """.trimIndent()
            )
        }.toString()
        val requestBody = mutationQuery.toRequestBody("application/json".toMediaTypeOrNull())

        // Gửi yêu cầu bằng Retrofit
        val call = ApiUpdateClient.getUploadDataService(baseUrl.toString()).sendImageData(
            "Bearer $token", requestBody
        )

        // Xử lý phản hồi từ server
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseString = response.body()?.string()
                    Log.d("onResponse", "Upload thành công! Phản hồi: $responseString")
                } else {
                    Log.e("onResponse", "Lỗi: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("onFailure", "Lỗi: ${t.message}")
            }
        })
    }

    // Lấy projectId từ SharedPreferences
    private fun getProjectIdFromMainActivity(): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("PROJECT_ID", null)
    }
}
