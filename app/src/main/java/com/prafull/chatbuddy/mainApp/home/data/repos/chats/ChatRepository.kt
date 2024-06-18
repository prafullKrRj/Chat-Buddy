package com.prafull.chatbuddy.mainApp.home.data.repos.chats

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.utils.CryptoEncryption
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayOutputStream

abstract class ChatRepository : KoinComponent {
    private val fireStore by inject<FirebaseFirestore>()
    private val firebaseAuth by inject<FirebaseAuth>()
    private val storage by inject<FirebaseStorage>()
    abstract suspend fun getResponse(history: ChatHistory, prompt: ChatMessage): Flow<ChatMessage>
    var imageUrls by mutableStateOf(listOf<String>())
    fun saveMessage(chat: ChatHistory, chatMessage: ChatMessage) {
        val encryptedText = CryptoEncryption.encrypt(chatMessage.text)
        val encryptedMessage =
            chatMessage.copy(text = encryptedText, imageBitmaps = mutableListOf())

        chatMessage.imageBitmaps.forEachIndexed { index, bitmap ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
            val data = byteArrayOutputStream.toByteArray()
            val ref =
                storage.reference.child("users/${firebaseAuth.currentUser?.email}/history/${chat.id}/${chatMessage.id}/${index}")
            ref.putBytes(data)
        }
        imageUrls = List(chatMessage.imageBitmaps.size) { index ->
            "users/${firebaseAuth.currentUser?.email}/history/${chat.id}/${chatMessage.id}/${index}"
        }
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
                            "imageUrls" to imageUrls.toList()
                    ),
                    SetOptions.merge()
            )
        imageUrls = mutableListOf()
    }
}