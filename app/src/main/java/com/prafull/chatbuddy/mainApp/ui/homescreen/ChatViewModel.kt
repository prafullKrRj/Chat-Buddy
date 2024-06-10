package com.prafull.chatbuddy.mainApp.ui.homescreen

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.prafull.chatbuddy.mainApp.data.ChatRepository
import com.prafull.chatbuddy.mainApp.models.ChatHistory
import com.prafull.chatbuddy.mainApp.models.ChatMessage
import com.prafull.chatbuddy.mainApp.models.Participant
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class ChatViewModel : ViewModel(), KoinComponent {
    private val chatRepository: ChatRepository by inject()

    private val _currChatUUID = MutableStateFlow(UUID.randomUUID().toString())  // Default value
    val currChatUUID = _currChatUUID.asStateFlow()

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState())   // UI state
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _chatting = MutableStateFlow(false)
    val chatting = _chatting.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private var chat = ChatHistory(id = currChatUUID.value)         // current chat

    fun sendMessage(userMessage: String, images: List<Bitmap>) {
        _chatting.update {
            true
        }
        _loading.update {
            true
        }
        _uiState.value.addMessage(
                ChatMessage(
                        text = userMessage,
                        participant = Participant.USER,
                        isPending = true,
                        imageUri = images.toMutableList()
                )
        )
        chat.apply {
            messages.add(_uiState.value.messages.last())
            lastModified = Timestamp.now()
        }
        chatRepository.saveMessage(chat, _uiState.value.messages.last())

        viewModelScope.launch {
            delay(2000L)
            _uiState.value.addMessage(
                    ChatMessage(
                            text = "oobrbneroibneriobe\noiwvonorbnoerb\noiwniwnbiernerinerb\niwnviwnieniernbernbirnbierb\noiwnvininbinbinbinwerinwiniwer\niowdnviwdniniwrnbwrwer",
                            participant = Participant.MODEL,
                            isPending = false
                    )
            )
            chat.apply {
                messages.add(_uiState.value.messages.last())
                lastModified = Timestamp.now()
            }
            chatRepository.saveMessage(chat, _uiState.value.messages.last())
            _loading.update {
                false
            }
        }

        /*
        viewModelScope.launch {
            chatRepository.getResponse(
                    ChatMessage(
                            text = userMessage,
                            participant = Participant.USER,
                            isPending = true,
                            imageUri = images
                    )
            ).collect {
                _uiState.value.addMessage(it)
            }
        }
         */
    }

    fun chatFromHistory(chatHistory: ChatHistory) {
        chatRepository.clearChat()
        _currChatUUID.update {
            chatHistory.id
        }
        chat.apply {
            id = chatHistory.id
            messages = chatHistory.messages.toMutableList()
            lastModified = chatHistory.lastModified
            model = chatHistory.model
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
        println(chatting.value)
        if (chatting.value) {
            _currChatUUID.update { UUID.randomUUID().toString() }
            chat = ChatHistory(id = currChatUUID.value)
            _chatting.update { false }
            _loading.update { false }
            _uiState.update {
                ChatUiState()
            }
        }
    }
}