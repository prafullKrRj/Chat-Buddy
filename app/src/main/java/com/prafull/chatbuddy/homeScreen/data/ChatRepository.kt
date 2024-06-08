package com.prafull.chatbuddy.homeScreen.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.prafull.chatbuddy.homeScreen.models.ChatHistory
import com.prafull.chatbuddy.homeScreen.models.ChatMessage
import com.prafull.chatbuddy.homeScreen.models.Participant
import com.prafull.chatbuddy.utils.CryptoEncryption
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatRepository : KoinComponent {

    private val generativeModel: GenerativeModel by inject()

    private val chat = generativeModel.startChat(
            history = listOf()
    )
    private val fireStore by inject<FirebaseFirestore>()
    private val firebaseAuth by inject<FirebaseAuth>()
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
}