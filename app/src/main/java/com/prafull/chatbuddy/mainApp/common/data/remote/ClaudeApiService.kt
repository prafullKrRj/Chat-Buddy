package com.prafull.chatbuddy.mainApp.common.data.remote

import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.common.model.ClaudeRequest
import com.prafull.chatbuddy.mainApp.common.model.ClaudeResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ClaudeApiService {

    @POST("v1/messages")
    suspend fun postMessage(
        @Header("x-api-key") apiKey: String = BuildConfig.CLAUDE_API_KEY,
        @Header("anthropic-version") version: String = "2023-06-01",
        @Header("content-type") contentType: String = "application/json",
        @Body request: ClaudeRequest
    ): ClaudeResponse
}