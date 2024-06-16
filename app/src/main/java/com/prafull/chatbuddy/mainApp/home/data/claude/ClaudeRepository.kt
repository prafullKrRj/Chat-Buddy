package com.prafull.chatbuddy.mainApp.home.data.claude

import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.home.data.ChatRepository
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.ClaudeRequest
import com.prafull.chatbuddy.mainApp.home.model.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.inject

class ClaudeRepository : ChatRepository() {

    private val claudeApiService: ClaudeApiService by inject()
    override suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage> {
        return callbackFlow {
            try {
                val request = ClaudeRequest(
                        model = history.model,
                        max_tokens = 1024,
                        temperature = history.temperature,
                        system = history.systemPrompt,
                        messages = history.messages.map {
                            it.toClaudeContent()
                        } + prompt.toClaudeContent()
                )
                val response =
                    claudeApiService.postMessage(BuildConfig.CLAUDE_API_KEY, request = request)
                response.content.first().text.let {
                    trySend(
                            ChatMessage(
                                    text = it,
                                    participant = Participant.ASSISTANT,
                                    isPending = false
                            )
                    )
                }
            } catch (httpException: retrofit2.HttpException) {
                trySend(
                        ChatMessage(
                                text = httpException.localizedMessage ?: "Error",
                                participant = Participant.ERROR
                        )
                )
            } catch (e: Exception) {
                trySend(
                        ChatMessage(
                                text = e.localizedMessage ?: "Error",
                                participant = Participant.ERROR
                        )
                )
            }
            awaitClose { }
        }
    }
}