package com.prafull.chatbuddy.mainApp.common.data.repos

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.common.model.HistoryItem
import com.prafull.chatbuddy.mainApp.common.model.HistoryMessage
import com.prafull.chatbuddy.mainApp.common.model.Participant
import com.prafull.chatbuddy.mainApp.modelsScreen.ui.ModelSafety
import com.prafull.chatbuddy.utils.Const
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GeminiRepo : HomeChatAbstract() {

    override fun getResponse(
        history: HistoryItem,
        prompt: HistoryMessage
    ): Flow<HistoryMessage> {
        return callbackFlow {
            val generativeModel =
                GenerativeModel(
                        modelName = history.model,
                        apiKey = BuildConfig.GEMINI_API_KEY,
                        generationConfig = generationConfig {
                            temperature = history.temperature.toFloat()
                            topK = 64
                            topP = 0.95f
                        },
                        safetySettings = if (history.safetySettings == ModelSafety.ROMANTIC.name) {
                            Const.SAFETY_SETTINGS_ROMANTIC
                        } else if (history.safetySettings == ModelSafety.UNSPECIFIED.name) {
                            Const.SAFETY_SETTINGS_NORMAL
                        } else {
                            Const.SAFETY_SETTINGS_UNINTERRUPTED
                        },
                        systemInstruction = content {
                            text(history.system)
                        }
                )
            val chat = generativeModel.startChat(
                    history = history.messages.map {
                        it.geminiContent()
                    }
            )
            try {
                val response = chat.sendMessage(prompt.geminiContent())

                saveMessage(history, prompt)            // Save the message to the database

                response.text?.let { modelResponse ->
                    val responseModel = HistoryMessage(
                            text = modelResponse,
                            participant = Participant.ASSISTANT.name,
                            model = prompt.model,
                            botImage = prompt.botImage
                    )
                    saveMessage(history, responseModel)     // Save the message to the database
                    trySend(
                            responseModel
                    )
                }
            } catch (e: Exception) {
                val responseModel = HistoryMessage(
                        text = "Error: ${e.message}",
                        participant = Participant.ASSISTANT.name,
                        model = prompt.model,
                        botImage = prompt.botImage
                )
                saveMessage(history, responseModel)     // Save the message to the database
                trySend(
                        responseModel
                )
            }


            awaitClose { }
        }
    }
}