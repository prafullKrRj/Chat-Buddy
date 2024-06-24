package com.prafull.chatbuddy.mainApp.common.data.repos

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.common.model.HistoryItem
import com.prafull.chatbuddy.mainApp.common.model.HistoryMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class HomeChatAbstract : KoinComponent {

    private val firestore: FirebaseFirestore by inject()
    private val auth: FirebaseAuth by inject()

    /**
     *      This function is used to get the response from the model
     * */
    abstract fun getResponse(
        history: HistoryItem,
        prompt: HistoryMessage
    ): Flow<HistoryMessage>

    suspend fun deleteLastTwo(item: HistoryItem): Boolean {
        val docRef =
            firestore.collection("users").document(auth.currentUser?.email.toString())
                .collection(item.promptType).document(item.id)
        docRef.get().await().let { document ->
            if (document != null) {
                val messages = document.get("messages") as MutableList<HistoryMessage>
                if (messages.isNotEmpty()) {
                    messages.removeLast() // Remove the last element
                    messages.removeLast()
                    docRef.update("messages", messages) // Update the document
                    return true
                } else {
                    Log.d("GeminiRepository", "No such document")
                }
            }
            return false
        }
    }
}
