package com.prafull.chatbuddy.mainApp.home.ui.homescreen

import androidx.compose.runtime.toMutableStateList
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage

class ChatUiState(
    messages: List<ChatMessage> = emptyList()
) {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _messages
    fun addMessage(msg: ChatMessage) {
        _messages.add(msg)
    }

    fun removeLastMessage() {
        _messages.removeLast()
    }

    fun getLast() = _messages.last()
}