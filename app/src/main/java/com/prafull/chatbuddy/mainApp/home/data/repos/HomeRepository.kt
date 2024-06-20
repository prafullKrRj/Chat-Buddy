package com.prafull.chatbuddy.mainApp.home.data.repos

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.utils.Const
import com.prafull.chatbuddy.utils.CryptoEncryption
import com.prafull.chatbuddy.utils.Resource
import com.prafull.chatbuddy.utils.SharedPrefManager
import com.prafull.chatbuddy.utils.base64ToBitmap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeRepository : KoinComponent {

    private val firebaseAuth by inject<FirebaseAuth>()
    private val firestore by inject<FirebaseFirestore>()
    private val storage by inject<FirebaseStorage>()
    private val sharedPrefManager by inject<SharedPrefManager>()
    suspend fun getPreviousChats(): Flow<Resource<List<ChatHistory>>> {
        return callbackFlow {
            try {
                val response = firestore.collection("users")
                    .document(firebaseAuth.currentUser?.email.toString()).collection("history")
                    .get().await()

                val chatHistoryList = response.documents.mapNotNull { document ->
                    val chatHistory = document.toObject(ChatHistory::class.java)
                    chatHistory?.let { it ->
                        it.messages = it.messages.map { message ->
                            message.text = CryptoEncryption.decrypt(message.text)
                            message.imageBitmaps = message.imageUrls.map { base64 ->
                                base64.base64ToBitmap()
                            }
                            message
                        }.toMutableList()
                    }
                    chatHistory
                }
                val sortedChatHistoryList = chatHistoryList.sortedByDescending { it.lastModified }

                if (sortedChatHistoryList.size > 20) {
                    val documentsToDelete = sortedChatHistoryList.drop(20)
                    documentsToDelete.forEach { chatHistory ->
                        firestore.collection("users")
                            .document(firebaseAuth.currentUser?.email.toString())
                            .collection("history")
                            .document(chatHistory.id)
                            .delete().await()
                        storage.reference.child("users/${firebaseAuth.currentUser?.email.toString()}/history/${chatHistory.id}")
                            .delete().await()
                    }
                }
                trySend(Resource.Success(sortedChatHistoryList.take(20)))
            } catch (e: Exception) {
                Log.d("HomeRepository", "getPreviousChats: ${e.message}")
                trySend(Resource.Error(e))
            }
            awaitClose { }
        }
    }

    suspend fun updateCoins(currValue: Long): Flow<Boolean> {
        return callbackFlow {
            try {
                firestore.collection("users")
                    .document(firebaseAuth.currentUser?.email.toString())
                    .update("currCoins", currValue + 2000)
                    .await()
                trySend(true)
            } catch (e: Exception) {
                trySend(false)
            }
            awaitClose { }
        }
    }

    suspend fun getCoins(): Flow<Resource<Long>> {
        return callbackFlow {
            try {
                val response = firestore.collection("users")
                    .document(firebaseAuth.currentUser?.email.toString())
                    .get().await()
                val coins = response.getLong("currCoins") ?: 2000
                trySend(Resource.Success(coins))
            } catch (e: Exception) {
                trySend(Resource.Error(e))
            }
            awaitClose { }
        }
    }

    fun getModels(): Flow<Resource<List<Model>>> {
        return callbackFlow {
            try {
                val models = mutableListOf<Model>()
                firestore.collection("models").document(Const.NLP).collection(Const.CHAT_BUDDY)
                    .get()
                    .await().documents.forEach {
                        models.add(it.toObject(Model::class.java)!!)
                    }
                firestore.collection("models").document(Const.NLP).collection(Const.CLAUDE).get()
                    .await().documents.forEach {
                        models.add(it.toObject(Model::class.java)!!)
                    }
                firestore.collection("models").document(Const.NLP).collection(Const.OPENAI).get()
                    .await().documents.forEach {
                        models.add(it.toObject(Model::class.java)!!)
                    }
                firestore.collection("models").document(Const.NLP).collection(Const.GEMINI).get()
                    .await().documents.forEach {
                        models.add(it.toObject(Model::class.java)!!)
                    }

                Log.d(
                        "HomeRepository", "getModels: ${
                    models.map {
                        it.generalName
                    }
                }"
                )
                trySend(Resource.Success(models))
            } catch (e: Exception) {
                trySend(Resource.Error(e))
            }
            awaitClose { }
        }
    }

    fun deleteChat(id: String): Flow<Boolean> {
        return callbackFlow {
            try {
                firestore.collection("users")
                    .document(firebaseAuth.currentUser?.email.toString())
                    .collection("history")
                    .document(id)
                    .delete().await()
                storage.reference.child("users/${firebaseAuth.currentUser?.email.toString()}/history/${id}")
                    .delete().await()
                trySend(true)
            } catch (e: Exception) {
                trySend(false)
            }
            awaitClose { }
        }
    }

    fun getCurrentModel(): Flow<Model> {
        return callbackFlow {
            try {
                val modelGroup = sharedPrefManager.getDefaultModel().split("/")
                firestore.collection("models").document("nlp").collection(modelGroup[0])
                    .document(modelGroup[1]).get().await().toObject(Model::class.java)?.let {
                        trySend(it)
                    }
            } catch (e: Exception) {
                trySend(Model())
            }
            awaitClose { }
        }
    }
}