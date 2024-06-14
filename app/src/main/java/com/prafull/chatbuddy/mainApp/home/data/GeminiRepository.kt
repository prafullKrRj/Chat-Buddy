package com.prafull.chatbuddy.mainApp.home.data

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.prafull.chatbuddy.BuildConfig
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.Participant
import com.prafull.chatbuddy.utils.CryptoEncryption
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeminiRepository : KoinComponent {
    private val fireStore by inject<FirebaseFirestore>()
    private val firebaseAuth by inject<FirebaseAuth>()
    suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage> {
        val generativeModel =
            GenerativeModel(
                    modelName = "gemini-1.5-flash-latest",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    generationConfig = generationConfig {
                        temperature = 0.7f
                    },
                    systemInstruction = content {
                        text(history.systemPrompt)
                    }
            )
        Log.d("GeminiRepository", "getResponse: ${history.systemPrompt}")
        return callbackFlow {
            val chat = generativeModel.startChat(
                    history = history.messages.map { it.toGeminiContent() },
            )
            try {
                val response = chat.sendMessage(prompt.toGeminiContent())
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
                                text = e.localizedMessage ?: "Error",
                                participant = Participant.ERROR
                        )
                )
            }
            awaitClose { }
        }
    }

    fun saveMessage(chat: ChatHistory, chatMessage: ChatMessage) {
        val encryptedText = CryptoEncryption.encrypt(chatMessage.text)
        val encryptedMessage = chatMessage.copy(text = encryptedText, imageUri = mutableListOf())
        fireStore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection("history").document(chat.id).set(
                    mapOf(
                            "id" to chat.id,
                            "model" to chat.model,
                            "messages" to FieldValue.arrayUnion(encryptedMessage),
                            "lastModified" to chat.lastModified
                    ),
                    SetOptions.merge()
            )
    }

    fun setGenerativeModel(systemPrompt: String) {

    }
}