package com.prafull.chatbuddy.mainApp.home.data.repos.chats

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.Participant
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelSafety
import com.prafull.chatbuddy.utils.Const
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GeminiRepository : ChatRepository() {


    fun deleteLast(chatId: String) {
        val docRef =
            fireStore.collection("users").document(firebaseAuth.currentUser?.email.toString())
                .collection("history").document(chatId)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val messages = document.get("messages") as MutableList<ChatMessage>
                if (messages.isNotEmpty()) {
                    messages.removeLast() // Remove the last element
                    docRef.update("messages", messages) // Update the document
                }
            } else {
                Log.d("GeminiRepository", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("GeminiRepository", "get failed with ", exception)
        }
    }

    suspend fun deleteLastTwo(chatId: String): Boolean {
        val docRef =
            fireStore.collection("users").document(firebaseAuth.currentUser?.email.toString())
                .collection("history").document(chatId)
        docRef.get().await().let { document ->
            if (document != null) {
                val messages = document.get("messages") as MutableList<ChatMessage>
                if (messages.isNotEmpty()) {
                    messages.removeLast() // Remove the last element
                    messages.removeLast()
                    docRef.update("messages", messages) // Update the documentr
                    return true
                } else {
                    Log.d("GeminiRepository", "No such document")
                }
            }
            return false
        }
    }

    override suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage> {
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
                        safetySettings = if (history.safetySetting == ModelSafety.ROMANTIC.name) {
                            Const.SAFETY_SETTINGS_ROMANTIC
                        } else {
                            Const.SAFETY_SETTINGS_NORMAL
                        },
                        systemInstruction = content {
                            text(history.systemPrompt)
                        }
                )
            Log.d("GeminiRepository", "Starting chat with model: ${history.toString()}")
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