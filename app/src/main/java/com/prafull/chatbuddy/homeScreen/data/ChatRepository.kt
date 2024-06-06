package com.prafull.chatbuddy.homeScreen.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import com.prafull.chatbuddy.homeScreen.models.ChatMessage
import com.prafull.chatbuddy.homeScreen.models.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.inject

class ChatRepository: KoinComponent {

    private val generativeModel: GenerativeModel by inject()

    private val chat = generativeModel.startChat(
            history = listOf()
    )
    suspend fun getResponse(prompt: ChatMessage): Flow<ChatMessage> {
        return callbackFlow {
            try {
                val response = chat.sendMessage(content {
                    for (image in prompt.imageUri) {
                        image(image)
                    }
                    text(prompt.text)
                })
                response.text?.let { modelResponse ->
                    trySend(
                            ChatMessage(
                                text = modelResponse,
                                participant = Participant.MODEL,
                                isPending = false
                        )
                    )
                }
            } catch (e: Exception) {
                trySend(
                        ChatMessage(
                                text = e.localizedMessage,
                                participant = Participant.ERROR
                        )
                )
            }
            awaitClose {  }
        }
    }
}