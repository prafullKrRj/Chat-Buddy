package com.prafull.chatbuddy.mainApp.home.data.repos.chats

import com.prafull.chatbuddy.mainApp.home.data.remote.OpenAIService
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.OpenAiRequest
import com.prafull.chatbuddy.mainApp.home.model.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.inject

class OpenAiRepository : ChatRepository() {
    private val openAiService: OpenAIService by inject()

    override suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage> {
        return callbackFlow {
            try {
                val request = OpenAiRequest(
                        model = history.model,
                        maxTokens = 1024,
                        messages = history.messages.map {
                            it.toOpenAi()
                        } + prompt.toOpenAi()
                )
                val response = OpenAIService.getResponse(request)
                response.choices.first().openAiMessageResp.let {
                    trySend(
                            ChatMessage(
                                    text = it.content,
                                    isPending = false,
                                    participant = Participant.ASSISTANT,
                                    model = history.modelGeneralName
                            )
                    )
                }
            } catch (e: Exception) {
                trySend(
                        ChatMessage(
                                text = e.localizedMessage ?: "Error",
                                model = history.modelGeneralName
                        )
                )
            }
            awaitClose { }
        }
    }
}