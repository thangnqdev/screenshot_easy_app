package com.example.screenshot_app.controller.services.interfaces

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UploadDataServiceInterface {
    @POST("graphql")
    fun sendImageData(
        @Header("Authorization") authToken: String,
        @Body requestBody: RequestBody
    ): Call<ResponseBody>
}