package com.prafull.chatbuddy.mainApp.home.model

import com.google.gson.annotations.SerializedName

data class ClaudeRequest(
    @SerializedName("model") val model: String,
    @SerializedName("max_tokens") val max_tokens: Int,
    @SerializedName("system") val system: String,
    @SerializedName("temperature") val temperature: Float,
    @SerializedName("messages") val messages: List<ClaudeMessage>
)

data class ClaudeMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val claudeMessageContent: List<ClaudeMessageContent>
)

data class ClaudeMessageContent(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String? = null,
    @SerializedName("source") val claudeMessageContentSource: ClaudeMessageContentSource? = null
)

data class ClaudeMessageContentSource(
    @SerializedName("type") val type: String,
    @SerializedName("media_type") val media_type: String,
    @SerializedName("data") val data: String
)

data class ClaudeResponse(
    @SerializedName("content") val content: List<ClaudeResponseContent>,
    @SerializedName("id") val id: String,
    @SerializedName("model") val model: String,
    @SerializedName("role") val role: String,
    @SerializedName("stop_reason") val stop_reason: String,
    @SerializedName("stop_sequence") val stop_sequence: String?,
    @SerializedName("type") val type: String,
    @SerializedName("usage") val claudeResponseUsage: ClaudeResponseUsage
)

data class ClaudeResponseContent(
    @SerializedName("text") val text: String,
    @SerializedName("type") val type: String
)

data class ClaudeResponseUsage(
    @SerializedName("input_tokens") val input_tokens: Int,
    @SerializedName("output_tokens") val output_tokens: Int
)
