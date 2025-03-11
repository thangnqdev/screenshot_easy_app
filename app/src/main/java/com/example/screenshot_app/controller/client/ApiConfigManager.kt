package com.example.screenshot_app.controller.client

import android.content.Context

object ApiConfigManager {

    private const val PREF_NAME = "ApiConfig"
    private const val BASE_URL = "BASE_URL"
    private const val TOKEN = "TOKEN"

    // Lưu thông tin API vào SharedPreferences
    fun saveApiConfig(
        context: Context,
        baseUrl: String,
        token: String
    ) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(BASE_URL, baseUrl)
            putString(TOKEN, token)
            apply()
        }
    }

    // Lấy thông tin API từ SharedPreferences
    fun getApiConfig(context: Context): Map<String, String> {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val baseUrl = sharedPref.getString(BASE_URL, "") ?: ""
        val token = sharedPref.getString(TOKEN, "") ?: ""

        return mapOf(
            "baseUrl" to baseUrl,
            "token" to token
        )
    }
}
