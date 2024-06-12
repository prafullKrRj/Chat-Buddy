package com.prafull.chatbuddy.mainApp.home.model

import com.google.firebase.Timestamp
import java.util.UUID

data class ChatHistory(
    var id: String = UUID.randomUUID().toString(),
    var messages: MutableList<ChatMessage> = mutableListOf(),
    var model: String = "gemini-1.5-flash-latest",
    var lastModified: Timestamp = Timestamp.now()
)
