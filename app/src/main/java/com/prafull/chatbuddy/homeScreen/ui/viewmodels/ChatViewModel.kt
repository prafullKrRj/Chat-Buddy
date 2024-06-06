package com.prafull.chatbuddy.homeScreen.ui.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.prafull.chatbuddy.homeScreen.data.ChatRepository
import com.prafull.chatbuddy.homeScreen.models.ChatMessage
import com.prafull.chatbuddy.homeScreen.models.Participant
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

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()

    private val _chatting = MutableStateFlow(false)
    val chatting = _chatting.asStateFlow()


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
        )
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