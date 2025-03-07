package com.example.screenshot_app.controller.client

import com.example.screenshot_app.controller.constant.Constant
import com.example.screenshot_app.controller.interfaces.GraphQLServiceUpInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val graphQLService: GraphQLServiceUpInterface by lazy {
        retrofit.create(GraphQLServiceUpInterface::class.java)
    }
}