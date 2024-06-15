package com.prafull.chatbuddy.mainApp.home.model

import com.google.firebase.Timestamp
import com.prafull.chatbuddy.utils.Const
import java.util.UUID

data class ChatHistory(
    var id: String = UUID.randomUUID().toString(),
    var messages: MutableList<ChatMessage> = mutableListOf(),
    var model: String = "gemini-1.5-flash-latest",
    var lastModified: Timestamp = Timestamp.now(),
    var systemPrompt: String = Const.GENERAL_SYSTEM_PROMPT,
    var promptName: String = "",
    var promptDescription: String = ""
)

fun String.isGeminiModel() = this.contains("gemini")
fun String.isClaudeModel() = this.contains("claude")
fun String.isGptModel() = this.contains("gpt")