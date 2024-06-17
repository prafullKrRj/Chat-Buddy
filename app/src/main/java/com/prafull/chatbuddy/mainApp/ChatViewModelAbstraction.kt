package com.prafull.chatbuddy.mainApp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.prafull.chatbuddy.mainApp.home.data.repos.ClaudeRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.ui.ChatUiState
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.model.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

abstract class ChatViewModelAbstraction : KoinComponent, ViewModel() {

    private val geminiRepository: GeminiRepository by inject()
    private val claudeRepository: ClaudeRepository by inject()

    var currChatUUID by mutableStateOf(UUID.randomUUID().toString())
    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState())   // UI state
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    var currPrompt by mutableStateOf(ChatMessage())    // current prompt
    var chatting by mutableStateOf(false)
    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()
    protected var chat =
        ChatHistory(id = currChatUUID)         // current chat

    var currModel by mutableStateOf(Model())    // current model
    fun sendMessage() {
        chatting = true
        _loading.update {
            true
        }
        _uiState.value.addMessage(currPrompt)
        claudeRepository.saveMessage(chat, currPrompt)
        Log.d("ChatViewModel", "sendMessage: ${uiState.value.messages.first()}")
        currPrompt = ChatMessage()
        if (chat.model.isGeminiModel()) {
            responseFromGemini()
        } else if (chat.model.isClaudeModel()) {
            responseFromClaude()
        } else {
            responseFromGemini()
        }
    }

    private fun responseFromClaude() {
        viewModelScope.launch {
            Log.d("ChatViewModel", "responseFromClaude: ${_uiState.value.messages.last()}")
            claudeRepository.getResponse(chat, _uiState.value.messages.last())
                .collect { chatMessageResponse ->
                    chat.apply {
                        messages.add(_uiState.value.messages.last())
                        lastModified = Timestamp.now()
                    }

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

    private fun responseFromGemini() {
        viewModelScope.launch {
            geminiRepository.getResponse(chat, _uiState.value.messages.last())
                .collect { chatMessageResponse ->
                    updatingChat(chatMessageResponse)
                }
        }
    }

    private fun updatingChat(chatMessageResponse: ChatMessage) {
        chat.apply {
            messages.add(_uiState.value.messages.last())
            lastModified = Timestamp.now()
        }

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

    fun chatFromHistory(chatHistory: ChatHistory) {
        currChatUUID = chatHistory.id
        currPrompt = ChatMessage()
        chat.apply {
            id = chatHistory.id
            messages = chatHistory.messages.toMutableList()
            lastModified = chatHistory.lastModified
            model = chatHistory.model
            systemPrompt = chatHistory.systemPrompt
            promptDescription = chatHistory.promptDescription
            promptName = chatHistory.promptName
            safetySetting = chatHistory.safetySetting
            temperature = chatHistory.temperature
        }
        _uiState.update {
            ChatUiState(messages = chatHistory.messages)
        }
        chatting = true
        _loading.update {
            false
        }
    }

    fun loadNewChat() {
        if (chatting) {
            updateScreenState()
            chat.apply {
                id = currChatUUID
                messages = mutableListOf()
            }
            chatting = false
        }
    }

    fun loadFromPromptLibrary(promptLibraryItem: PromptLibraryItem) {
        updateScreenState()
        chat.apply {
            id = currChatUUID
            messages = mutableListOf()
            systemPrompt = promptLibraryItem.system
            promptDescription = promptLibraryItem.description
            promptName = promptLibraryItem.name
        }
        chatting = false
    }

    private fun updateScreenState() {
        currChatUUID = UUID.randomUUID().toString()
        currPrompt = ChatMessage()
        _loading.update { false }
        _uiState.update {
            ChatUiState()
        }
    }

    fun onModelSelected(it: Model) {
        chat.apply {
            model = it.actualName
            temperature = it.temperature
        }
        currModel = it
    }

    fun getCurrChat() = chat
}