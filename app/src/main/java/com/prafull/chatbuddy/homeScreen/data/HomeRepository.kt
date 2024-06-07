package com.prafull.chatbuddy.homeScreen.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.homeScreen.models.ChatHistory
import com.prafull.chatbuddy.utils.CryptoEncryption
import com.prafull.chatbuddy.utils.Response
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeRepository : KoinComponent {

    private val firebaseAuth by inject<FirebaseAuth>()
    private val firestore by inject<FirebaseFirestore>()
    suspend fun getPreviousChats(): Flow<Response<List<ChatHistory>>> {
        return callbackFlow {
            try {
                val response = firestore.collection("users")
                    .document(firebaseAuth.currentUser?.email.toString()).collection("history")
                    .get().await()
                val chatHistoryList = response.documents.mapNotNull { document ->
                    val chatHistory = document.toObject(ChatHistory::class.java)
                    chatHistory?.let {
                        it.messages = it.messages.map { message ->
                            message.text = CryptoEncryption.decrypt(message.text)
                            message
                        }.toMutableList()
                    }
                    chatHistory
                }
                trySend(Response.Success(chatHistoryList.sortedByDescending { it.lastModified }))
            } catch (e: Exception) {
                trySend(Response.Error(e))
            }
            awaitClose { }
        }
    }
}