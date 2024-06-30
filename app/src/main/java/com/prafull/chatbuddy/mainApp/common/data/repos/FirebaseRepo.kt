package com.prafull.chatbuddy.mainApp.common.data.repos

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.prafull.chatbuddy.mainApp.home.models.ChatHistoryNormal
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryHistory
import com.prafull.chatbuddy.utils.CryptoEncryption
import com.prafull.chatbuddy.utils.toBase64
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FirebaseRepo : KoinComponent {
    private val firestore: FirebaseFirestore by inject()
    private val firebaseAuth by inject<FirebaseAuth>()

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
        firestore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection(history.promptType).document(history.id).set(
                    history,
                    SetOptions.merge()
            )
        addToUserHistory(history.id, history.promptType)
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
        firestore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection(history.promptType).document(history.id).set(
                    history,
                    SetOptions.merge()
            )
        addToUserHistory(history.id, history.promptType)
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
        firestore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection(history.promptType).document(history.id).set(
                    history,
                    SetOptions.merge()
            )
        addToUserHistory(history.id, history.promptType)
    }

    private fun addToUserHistory(id: String, promptType: String) {
        val ref =
            firestore.collection("users").document(firebaseAuth.currentUser!!.email.toString())
        ref.get().addOnSuccessListener {
            val history = it.get("history") as MutableList<UserHistory>
            Log.d("FirebaseRepo", "addToUserHistory: ${history.size}")
            history.add(UserHistory(id, promptType))
            ref.update("history", history)
        }
    }

    fun removeLastTwoMessages(id: String, promptType: String): Boolean {
        val docRef =
            firestore.collection("users").document(firebaseAuth.currentUser!!.email.toString())
                .collection(promptType).document(id)
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
        val docRef =
            firestore.collection("users").document(firebaseAuth.currentUser!!.email.toString())
                .collection(promptType).document(id)
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
}

/**
 *          This function is used to add history ids to the user dashboard so that it will be easy to clear some previous histories if needed
 * */
data class UserHistory(
    val id: String = "",
    val promptType: String = "",
    val timestamp: Timestamp = Timestamp.now()
)