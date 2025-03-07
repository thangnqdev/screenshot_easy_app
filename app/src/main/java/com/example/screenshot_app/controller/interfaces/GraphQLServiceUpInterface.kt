package com.example.screenshot_app.controller.interfaces

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GraphQLServiceUpInterface {
    @POST("graphql")
    fun createProjectFile(
        @Header("Authorization") authToken: String,
        @Body requestBody: RequestBody
    ): Call<ResponseBody>
}