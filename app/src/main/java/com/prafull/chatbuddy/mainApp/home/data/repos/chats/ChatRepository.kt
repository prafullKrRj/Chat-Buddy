package com.prafull.chatbuddy.mainApp.home.data.repos.chats

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.utils.CryptoEncryption
import com.prafull.chatbuddy.utils.toBase64
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class ChatRepository : KoinComponent {
    protected val fireStore by inject<FirebaseFirestore>()
    protected val firebaseAuth by inject<FirebaseAuth>()
    abstract suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage>
    fun saveMessage(chat: ChatHistory, chatMessage: ChatMessage) {
        val encryptedText = CryptoEncryption.encrypt(chatMessage.text)
        val encryptedMessage =
            chatMessage.copy(
                    text = encryptedText,
                    imageUrls = chatMessage.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )

        fireStore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection("history").document(chat.id).set(
                    mapOf(
                            "id" to chat.id,
                            "model" to chat.model,
                            "messages" to FieldValue.arrayUnion(encryptedMessage),
                            "lastModified" to chat.lastModified,
                            "systemPrompt" to chat.systemPrompt,
                            "promptName" to chat.promptName,
                            "promptDescription" to chat.promptDescription,
                            "temperature" to chat.temperature,
                            "safetySetting" to chat.safetySetting,
                            "modelGeneralName" to chat.modelGeneralName,
                            "botImage" to chat.botImage,
                    ),
                    SetOptions.merge()
            )
    }


}