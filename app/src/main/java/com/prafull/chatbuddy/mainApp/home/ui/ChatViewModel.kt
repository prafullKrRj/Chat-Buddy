package com.prafull.chatbuddy.mainApp.home.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.prafull.chatbuddy.mainApp.home.data.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class ChatViewModel : ViewModel(), KoinComponent {
    private val geminiRepository: GeminiRepository by inject()

    private val _currChatUUID = MutableStateFlow(UUID.randomUUID().toString())  // Default value
    val currChatUUID = _currChatUUID.asStateFlow()

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState())   // UI state
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _chatting = MutableStateFlow(false)
    val chatting = _chatting.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    private var chat =
        ChatHistory(id = currChatUUID.value)         // current chat


    fun sendMessage(prompt: ChatMessage) {
        _chatting.update {
            true
        }
        _loading.update {
            true
        }
        _uiState.value.addMessage(prompt)
        geminiRepository.saveMessage(chat, prompt)

        viewModelScope.launch {
            geminiRepository.getResponse(chat, prompt).collect { chatMessageResponse ->
                chat.apply {
                    messages.add(chatMessageResponse)
                    lastModified = Timestamp.now()
                }
                _uiState.value.addMessage(chatMessageResponse)
                geminiRepository.saveMessage(chat, chatMessageResponse)
                _loading.update {
                    false
                }
            }
        }
    }

    fun chatFromHistory(chatHistory: ChatHistory) {
        _currChatUUID.update {
            chatHistory.id
        }
        chat.apply {
            id = chatHistory.id
            messages = chatHistory.messages.toMutableList()
            lastModified = chatHistory.lastModified
            model = chatHistory.model

            systemPrompt = chatHistory.systemPrompt
            promptDescription = chatHistory.promptDescription
            promptName = chatHistory.promptName
        }
        _uiState.update {
            ChatUiState(messages = chatHistory.messages)
        }
        _chatting.update {
            true
        }
        _loading.update {
            false
        }
    }

    fun loadNewChat() {
        if (chatting.value) {
            _currChatUUID.update { UUID.randomUUID().toString() }
            chat.apply {
                id = currChatUUID.value
                messages = mutableListOf()
            }
            _chatting.update { false }
            _loading.update { false }
            _uiState.update {
                ChatUiState()
            }
        }
    }

    fun loadFromPromptLibrary(promptLibraryItem: PromptLibraryItem) {
        _chatting.update { true }
        _currChatUUID.update { UUID.randomUUID().toString() }
        chat.apply {
            id = currChatUUID.value
            messages = mutableListOf()
            systemPrompt = promptLibraryItem.system
            promptDescription = promptLibraryItem.description
            promptName = promptLibraryItem.name
        }
        _loading.update { false }
        _uiState.update {
            ChatUiState()
        }
        Log.d("ChatViewModel", "Loading from prompt library: ${chat.systemPrompt}")
        geminiRepository.setGenerativeModel(chat.systemPrompt)
    }
}