package com.example.screenshot_app.controller.client

import android.content.Context
import android.util.Log
import com.example.screenshot_app.controller.constant.Constant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SendImageDataToServer(private val context: Context) {
    fun sendImageDataToServer(id: String, storage: String, filenameDownload: String) {
        val projectId = getProjectIdFromMainActivity()
        if(projectId.isNullOrEmpty()){
            Log.e("sendImageDataToServer: ", "projectId is null or empty")
            return
        }
        val mutationQuery = JSONObject().apply {
            put("query", """
        mutation {
            create_projects_files_item (data: {
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
    """.trimIndent())
        }.toString()

        val requestBody = mutationQuery.toRequestBody("application/json".toMediaTypeOrNull())
        val call = ApiClient.graphQLService.createProjectFile(
            "Bearer " + Constant.TOKEN, requestBody
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseString = response.body()?.string()
                    Log.d("onResponse", "Upload thành công! Phản hồi: ${responseString.toString()}")
                } else {
                    Log.e("onResponse", "Lỗi: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("onFailure", "Lỗi: ${t.message}")
            }
        })
    }
    private fun getProjectIdFromMainActivity(): String? {
        val sharedPref = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("PROJECT_ID", null)
    }

}