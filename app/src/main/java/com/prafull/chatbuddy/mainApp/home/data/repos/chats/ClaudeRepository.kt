package com.prafull.chatbuddy.mainApp.home.data.repos.chats

import android.util.Log
import com.prafull.chatbuddy.mainApp.home.data.remote.ClaudeApiService
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.ClaudeRequest
import com.prafull.chatbuddy.mainApp.home.model.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.inject

class ClaudeRepository : ChatRepository() {
    private val claudeApiService by inject<ClaudeApiService>()
    override suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage> {
        return callbackFlow {
            try {
                val request = ClaudeRequest(
                        model = history.model,
                        max_tokens = 350,
                        temperature = 1.0f,
                        system = history.systemPrompt,
                        messages = history.messages.map {
                            it.toClaudeMessages()
                        } + prompt.toClaudeMessages()
                )
                val response = claudeApiService.postMessage(request = request)
                response.content.firstOrNull()?.text?.let {
                    trySend(
                            ChatMessage(
                                    text = it,
                                    participant = Participant.ASSISTANT,
                                    isPending = false,
                                    model = history.modelGeneralName
                            )
                    )
                }
            } catch (e: Exception) {
                Log.e("ClaudeRepository", "getResponse: ", e)
                trySend(
                        ChatMessage(
                                text = e.localizedMessage ?: "Error occurred. Please try again.",
                                participant = Participant.ERROR,
                                isPending = false,
                                model = history.modelGeneralName
                        )
                )
            }
            awaitClose { }
        }
    }
}
