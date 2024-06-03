package com.prafull.chatbuddy.homeScreen

import android.graphics.Bitmap
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

class ChatViewModel : ViewModel(), KoinComponent {
    private val generativeModel: GenerativeModel by inject()
    private val chat = generativeModel.startChat(
            history = listOf()
    )

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


    fun sendMessage(userMessage: String, images: List<Bitmap>) {
        _uiState.value.addMessage(
                ChatMessage(
                        text = userMessage,
                        participant = Participant.USER,
                        isPending = true,
                        imageUri = images
                )
        )

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

    fun replaceLastPendingMessage() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isPending = false }
            _messages.removeLast()
            _messages.add(newMessage)
        }
    }
}


enum class Participant {
    USER, MODEL, ERROR
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var imageUri: List<Bitmap> = emptyList(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
)