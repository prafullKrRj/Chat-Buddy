package com.prafull.chatbuddy.mainApp.home.data.gemini

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.home.data.ChatRepository
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GeminiRepository : ChatRepository() {
    override suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage> {
        return callbackFlow {
            val generativeModel =
                GenerativeModel(
                        modelName = history.model,
                        apiKey = BuildConfig.GEMINI_API_KEY,
                        generationConfig = generationConfig {
                            temperature = history.temperature
                            topK = 64
                            topP = 0.95f
                        },
                        safetySettings = history.safetySetting,
                        systemInstruction = content {
                            text(history.systemPrompt)
                        }
                )
            val chat = generativeModel.startChat(
                    history = history.messages.map { it.toGeminiContent() },
            )
            try {
                val response = chat.sendMessage(prompt.toGeminiContent())
                response.text?.let { modelResponse ->
                    trySend(
                            ChatMessage(
                                    text = modelResponse,
                                    participant = Participant.ASSISTANT,
                                    isPending = false
                            )
                    )
                }
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