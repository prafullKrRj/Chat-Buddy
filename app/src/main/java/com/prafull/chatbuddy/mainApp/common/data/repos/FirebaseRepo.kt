package com.prafull.chatbuddy.mainApp.common.data.repos

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.home.models.ChatHistoryNormal
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryHistory
import com.prafull.chatbuddy.utils.Const
import com.prafull.chatbuddy.utils.CryptoEncryption
import com.prafull.chatbuddy.utils.toBase64
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FirebaseRepo : KoinComponent {
    private val firestore: FirebaseFirestore by inject()
    private val firebaseAuth by inject<FirebaseAuth>()
    private val user by lazy {
        firestore.collection("users").document(firebaseAuth.currentUser?.email!!)
    }

    // Save normal message
    fun saveNormalMessage(history: ChatHistoryNormal) {
        val response = history.messages.last()
        val prompt = history.messages[history.messages.size - 2]
        val encryptedPrompt =
            prompt.copy(
                    text = getEncrypted(prompt.text),
                    imageBase64 = prompt.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        val encryptedResponse =
            response.copy(
                    text = getEncrypted(response.text),
                    imageBase64 = response.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        history.messages.removeLast()
        history.messages.removeLast()
        history.messages.add(encryptedPrompt)
        history.messages.add(encryptedResponse)
        user
            .collection(history.promptType).document(history.id).set(
                    history,
                    SetOptions.merge()
            )
        addToUserHistory(history.id, history.promptType, history.messages.first().text, "")
    }

    fun savePromptLibraryMessage(
        history: PromptLibraryHistory
    ) {
        val response = history.messages.last()
        val prompt = history.messages[history.messages.size - 2]
        val encryptedPrompt =
            prompt.copy(
                    text = getEncrypted(prompt.text),
                    imageBase64 = prompt.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        val encryptedResponse =
            response.copy(
                    text = getEncrypted(response.text),
                    imageBase64 = response.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        history.messages.removeLast()
        history.messages.removeLast()
        history.messages.add(encryptedPrompt)
        history.messages.add(encryptedResponse)
        user.collection(history.promptType).document(history.id).set(
                history,
                SetOptions.merge()
        )
        addToUserHistory(
                history.id,
                history.promptType,
                firstPrompt = history.messages.first().text,
                title = history.name
        )
    }

    fun saveModelsMessage(history: ModelsHistory) {
        val response = history.messages.last()
        val prompt = history.messages[history.messages.size - 2]
        val encryptedPrompt =
            prompt.copy(
                    text = getEncrypted(prompt.text),
                    imageBase64 = prompt.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        val encryptedResponse =
            response.copy(
                    text = getEncrypted(response.text),
                    imageBase64 = response.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        history.messages.removeLast()
        history.messages.removeLast()
        history.messages.add(encryptedPrompt)
        history.messages.add(encryptedResponse)
        user.collection(history.promptType).document(history.id).set(
                history,
                SetOptions.merge()
        )
        addToUserHistory(
                history.id,
                history.promptType,
                firstPrompt = history.messages.first().text,
                title = history.messages.first().model
        )
    }

    fun hashMapToUserHistory(hashMap: HashMap<String, Any>): UserHistory {
        val id = hashMap["id"] as String
        val promptType = hashMap["promptType"] as String
        val firstPrompt = hashMap["firstPrompt"] as String
        val title = hashMap["title"] as String

        return UserHistory(id, promptType, firstPrompt = firstPrompt, title = title)
    }

    private fun addToUserHistory(
        id: String,
        promptType: String,
        firstPrompt: String = "",
        title: String = ""
    ) {
        user.get().addOnSuccessListener {
            val history = (it.get("history") as List<HashMap<String, Any>>).map { hashMap ->
                hashMapToUserHistory(hashMap)
            }.toMutableList()

            val existingHistory = history.find { it.id == id }

            if (existingHistory == null) {
                history.add(UserHistory(id, promptType, firstPrompt = firstPrompt, title = title))
                user.update("history", history)
            }
        }
    }

    fun removeLastTwoMessages(id: String, promptType: String): Boolean {
        val docRef = user.collection(promptType).document(id)
        return try {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val messages = document.get("messages") as MutableList<*>
                    if (messages.size >= 2) {
                        messages.removeAt(messages.size - 1)
                        messages.removeAt(messages.size - 1)
                        docRef.update("messages", messages)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getEncrypted(text: String) = CryptoEncryption.encrypt(text)
    fun removeLastMessage(id: String, promptType: String): Boolean {
        val docRef = user.collection(promptType).document(id)
        return try {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val messages = document.get("messages") as MutableList<*>
                    if (messages.isNotEmpty()) {
                        messages.removeAt(messages.size - 1)
                        docRef.update("messages", messages)
                    }
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getModelsHistory(id: String, model: Model): Flow<ModelsHistory> {
        return callbackFlow {
            try {
                val doc = user.collection(Const.MODELS_HISTORY).document(id).get().await()
                    .toObject(ModelsHistory::class.java)
                if (doc == null) trySend(
                        ModelsHistory(
                                id = id, model = model.actualName,
                                system = model.system,
                                safetySettings = model.safetySetting,
                                temperature = model.temperature
                        )
                )
                else {
                    val modelsHistory = doc.copy(
                            messages = doc.messages.onEach {
                                it.text = CryptoEncryption.decrypt(it.text)
                            }
                    )
                    Log.d("ModelsHistory", modelsHistory.toString())
                    trySend(modelsHistory)
                }
            } catch (e: Exception) {
                Log.d("ModelsHistory", e.toString())
                trySend(
                        ModelsHistory(
                                id = id, model = model.actualName,
                                system = model.system,
                                safetySettings = model.safetySetting,
                                temperature = model.temperature
                        )
                )
            }
            awaitClose { }
        }
    }
}

/**
 *          This function is used to add history ids to the user dashboard so that it will be easy to clear some previous histories if needed
 * */
data class UserHistory(
    val id: String = "",
    val promptType: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val firstPrompt: String = "",
    val title: String = ""
)