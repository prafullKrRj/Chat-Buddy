package com.prafull.chatbuddy.homeScreen.ui

import android.graphics.Bitmap
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class ChatViewModel : ViewModel(), KoinComponent {
    private val generativeModel: GenerativeModel by inject()
    private val chat = generativeModel.startChat(
            history = listOf()
    )

    private val _adButtonEnabled = MutableStateFlow(true)
    val adButtonEnabled = _adButtonEnabled.asStateFlow()

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState(chat.history.map { content ->
            ChatMessage(
                    text = content.parts.first().asTextOrNull() ?: "",
                    participant = if (content.role == "user") Participant.USER else Participant.MODEL,
                    isPending = false,
                    imageUri = mutableListOf()
            )
        }))

    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()

    private val _chatting = MutableStateFlow(false)
    val chatting = _chatting.asStateFlow()

    private val _coins = MutableStateFlow(2000L)
    val coins = _coins.asStateFlow()

    private val _watchedAd = MutableStateFlow(false)
    val watchedAd = _watchedAd.asStateFlow()

    fun sendMessage(userMessage: String, images: List<Bitmap>) {
        _chatting.update {
            true
        }
        _uiState.value.addMessage(
                ChatMessage(
                        text = userMessage,
                        participant = Participant.USER,
                        isPending = true,
                        imageUri = images
                )
        )
        _uiState.value.addMessage(
                ChatMessage(
                        text = "oobrbneroibneriobe\noiwvonorbnoerb\noiwniwnbiernerinerb\niwnviwnieniernbernbirnbierb\noiwnvininbinbinbinwerinwiniwer\niowdnviwdniniwrnbwrwer",
                        participant = Participant.MODEL,
                        isPending = false
                )
        )/*
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(content {
                    for (image in images) {
                        image(image)
                    }
                    text(userMessage)
                })

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                            ChatMessage(
                                    text = modelResponse,
                                    participant = Participant.MODEL,
                                    isPending = false
                            )
                    )
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                        ChatMessage(
                                text = e.localizedMessage,
                                participant = Participant.ERROR
                        )
                )
            }
        }*/
    }

    fun adWatched() {
        _watchedAd.update {
            true
        }
        if (watchedAd.value) addCoins()
    }

    private fun addCoins() {
        _coins.update {
            it + 5000
        }
        updateAdButtonState(true)
    }

    fun updateAdButtonState(enabled: Boolean) {
        _adButtonEnabled.update {
            enabled
        }
    }
}


class ChatUiState(
    messages: List<ChatMessage> = emptyList()
) {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _messages

    fun addMessage(msg: ChatMessage) {
        _messages.add(msg)
    }
}


enum class Participant {
    USER, MODEL
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var imageUri: List<Bitmap> = emptyList(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
)