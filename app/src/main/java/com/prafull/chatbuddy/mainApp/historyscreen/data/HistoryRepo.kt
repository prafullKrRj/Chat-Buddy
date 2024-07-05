package com.prafull.chatbuddy.mainApp.historyscreen.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.common.data.repos.UserHistory
import com.prafull.chatbuddy.mainApp.historyscreen.model.HistoryClass
import com.prafull.chatbuddy.utils.CryptoEncryption
import com.prafull.chatbuddy.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoryRepo : KoinComponent {

    private val fireAuth by inject<FirebaseAuth>()
    private val firesStore by inject<FirebaseFirestore>()
    private val user by lazy {
        firesStore.collection("users").document(fireAuth.currentUser?.email.toString())
    }

    suspend fun getHistory(): Flow<Resource<HistoryClass>> {
        return callbackFlow {
            try {
                val document = user.get().await()
                val data = document["history"] as List<Map<String, Any>>
                val historyList = data.map {
                    UserHistory(
                            id = it["id"] as String,
                            promptType = (it["promptType"] as String),
                            timestamp = it["timestamp"] as Timestamp,
                            firstPrompt = CryptoEncryption.decrypt(it["firstPrompt"] as String),
                            title = (it["title"] as String)
                    )
                }
                trySend(Resource.Success(HistoryClass(historyList.sortedByDescending {
                    it.timestamp
                })))
            } catch (e: Exception) {
                trySend(Resource.Error(e))
            }
            awaitClose { }
        }
    }

    suspend fun deleteChat(chatId: String, promptType: String): Flow<Boolean> {
        return callbackFlow {
            user.collection(promptType).document(chatId).delete()
            val document = user.get().await()
            val data = document["history"] as MutableList<Map<String, Any>>

            data.removeAll { it["id"] == chatId }
            user.update("history", data).addOnSuccessListener {
                trySend(true)
            }.addOnFailureListener {
                trySend(false)
            }
            awaitClose {  }
        }
    }
}