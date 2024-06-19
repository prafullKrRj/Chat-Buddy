package com.prafull.chatbuddy.mainApp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.ChatRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.ClaudeRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.OpenAiRepository
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.ChatUiState
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
    private val openAiRepository: OpenAiRepository by inject()

    var currChatUUID by mutableStateOf(UUID.randomUUID().toString())

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState())   // UI state
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    var currPrompt by mutableStateOf(ChatMessage())    // current prompt
    var chatting by mutableStateOf(false)
    var loading by mutableStateOf(false)

    protected var chat =
        ChatHistory(id = currChatUUID)         // current chat

    var currModel by mutableStateOf(Model())    // current model
    fun sendMessage() {
        viewModelScope.launch {
            loading = true
            chatting = true
            _uiState.value.addMessage(currPrompt)
            saveMessage(currPrompt)
            currPrompt = ChatMessage()
            getResponse()
        }
    }

    fun regenerateResponse() {
        viewModelScope.launch {
            chat.messages.removeLast()
            _uiState.value.removeLastMessage()
            currPrompt = _uiState.value.messages.last()
            loading = true
            chatting = true
            currPrompt = ChatMessage()
            geminiRepository.deleteLast(currChatUUID)
            getResponse()
        }
    }

    private fun saveMessage(message: ChatMessage) {
        geminiRepository.saveMessage(chat, message)
        chat.messages.add(message)
        chat.lastModified = Timestamp.now()
    }

    private fun getResponse() {
        when {
            chat.model.isGeminiModel() -> getResponseFromGemini()
            chat.model.isClaudeModel() -> getResponseFromClaude()
            chat.model.isGptModel() -> getResponseFromOpenAI()
            else -> getResponseFromGemini()
        }
    }

    private fun getResponseFromOpenAI() = getResponseFromRepository(openAiRepository)
    private fun getResponseFromClaude() = getResponseFromRepository(claudeRepository)
    private fun getResponseFromGemini() = getResponseFromRepository(geminiRepository)
    private fun getResponseFromRepository(repository: ChatRepository) {
        viewModelScope.launch {
            repository.getResponse(chat, _uiState.value.messages.last()).collect { response ->
                updateChatWithResponse(response)
            }
        }
    }

    private fun updateChatWithResponse(response: ChatMessage) {
        chat.apply {
            messages.add(response)
            lastModified = Timestamp.now()
        }
        _uiState.value.addMessage(response)
        saveMessage(response)
        loading = false
    }

    fun chatFromHistory(chatHistory: ChatHistory) {
        Log.d("ChatViewModel", "Chat from history: $chatHistory")
        viewModelScope.launch {
            currChatUUID = chatHistory.id
            currPrompt = ChatMessage()
            chat = chatHistory.copy()
            currModel = chatHistory.toModel()
            _uiState.update {
                ChatUiState(messages = chatHistory.messages)
            }
            chatting = true
            loading = false
        }
    }

    fun chatFromHistoryCharacter(chatHistory: ChatHistory, character: Model) = chatFromHistory(
            chatHistory.apply {
                model = character.actualName
                temperature = character.temperature
                safetySetting = character.safetySetting
                promptName = character.system
            }
    )

    fun loadNewChat() {
        if (chatting) {
            updateScreenState()
            chat = ChatHistory(id = currChatUUID)
            chatting = false
        }
    }

    fun newCharacterChat(character: Model) {
        currChatUUID = character.generalName
        currPrompt = ChatMessage()
        chat = ChatHistory(
                id = currChatUUID,
                model = character.actualName,
                temperature = character.temperature,
                systemPrompt = character.system,
                safetySetting = character.safetySetting,
                modelGeneralName = character.generalName,
                botImage = character.image
        )
        chatting = false
        loading = false
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
        loading = false
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

    fun getCurrChat() = chat // for prompt details
}