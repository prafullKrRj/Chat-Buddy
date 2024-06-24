package com.prafull.chatbuddy.mainApp.common.data.repos

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.prafull.chatbuddy.mainApp.home.models.ChatHistoryNormal
import com.prafull.chatbuddy.mainApp.home.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsMessage
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryHistory
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryMessage
import com.prafull.chatbuddy.utils.CryptoEncryption
import com.prafull.chatbuddy.utils.toBase64
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FirebaseRepo : KoinComponent {
    private val firestore: FirebaseFirestore by inject()
    private val firebaseAuth by inject<FirebaseAuth>()

    // Save normal message
    suspend fun saveNormalMessage(history: ChatHistoryNormal, message: NormalHistoryMsg) {
        val encryptedMessage =
            message.copy(
                    text = getEncrypted(message.text),
                    imageBase64 = message.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        history.messages.add(encryptedMessage)
        firestore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection(history.promptType).document(history.id).set(
                    history,
                    SetOptions.merge()
            )
        addToUserHistory(history.id, history.promptType)
    }

    suspend fun savePromptLibraryMessage(
        history: PromptLibraryHistory,
        message: PromptLibraryMessage
    ) {
        val encryptedMessage =
            message.copy(
                    text = getEncrypted(message.text),
                    imageBase64 = message.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        history.messages.add(encryptedMessage)
        firestore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection(history.promptType).document(history.id).set(
                    history,
                    SetOptions.merge()
            )
        addToUserHistory(history.id, history.promptType)
    }

    suspend fun saveModelsMessage(history: ModelsHistory, message: ModelsMessage) {
        val encryptedMessage =
            message.copy(
                    text = getEncrypted(message.text),
                    imageBase64 = message.imageBitmaps.map {
                        it?.toBase64() ?: ""
                    },
                    imageBitmaps = emptyList()
            )
        history.messages.add(encryptedMessage)
        firestore.collection("users").document(firebaseAuth.currentUser?.email!!)
            .collection(history.promptType).document(history.id).set(
                    history,
                    SetOptions.merge()
            )
        addToUserHistory(history.id, history.promptType)
    }

    private fun addToUserHistory(id: String, promptType: String) {
        firestore.collection("users").document(firebaseAuth.currentUser!!.email.toString())
            .update("history", UserHistory(id, promptType, Timestamp.now()))
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