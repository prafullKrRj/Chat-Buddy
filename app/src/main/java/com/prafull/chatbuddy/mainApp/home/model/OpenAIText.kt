package com.prafull.chatbuddy.mainApp.home.model

import com.google.gson.annotations.SerializedName

data class OpenAiRequest(
    @SerializedName("model") val model: String,
    @SerializedName("max_tokens") val maxTokens: Int,
    @SerializedName("messages") val messages: List<OpenAiMessageInp>
)

data class OpenAiMessageInp(
    @SerializedName("role") val role: String,
    @SerializedName("content") val openAiMessageContent: List<OpenAiMessageContent>
)

data class OpenAiMessageContent(
    @SerializedName("type") val type: String,
    @SerializedName("text") val text: String? = null,
    @SerializedName("image_url") val imageUrl: String? = null
)

data class OpenAiResponse(
    @SerializedName("choices")
    val choices: List<ResponseChoice>,
    @SerializedName("created")
    val created: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("object")
    val responseObject: String,
    @SerializedName("usage")
    val usage: OpenAiUsage
)

data class OpenAiUsage(
    @SerializedName("completion_tokens")
    val completionTokens: Int,
    @SerializedName("prompt_tokens")
    val promptTokens: Int,
    @SerializedName("total_tokens")
    val totalTokens: Int
)

data class OpenAiMessageResp(
    @SerializedName("content")
    val content: String,
    @SerializedName("role")
    val role: String
)

data class ResponseChoice(
    @SerializedName("finish_reason")
    val finishReason: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("logprobs")
    val logprobs: Any,
    val openAiMessageResp: OpenAiMessageResp
)