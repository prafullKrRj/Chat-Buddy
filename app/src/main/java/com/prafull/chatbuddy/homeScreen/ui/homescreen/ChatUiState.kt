package com.prafull.chatbuddy.homeScreen.ui.homescreen

import androidx.compose.runtime.toMutableStateList
import com.prafull.chatbuddy.homeScreen.models.ChatMessage

class ChatUiState(
    messages: List<ChatMessage> = emptyList()
) {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _messages
    fun addMessage(msg: ChatMessage) {
        _messages.add(msg)
    }
}