package com.prafull.chatbuddy.homeScreen.models

import com.google.firebase.Timestamp
import java.util.UUID

data class ChatHistory(
    val id: String = UUID.randomUUID().toString(),
    var messages: MutableList<ChatMessage> = mutableListOf(),
    val model: String = "gemini-1.5-flash-latest",
    var lastModified: Timestamp = Timestamp.now()
)
