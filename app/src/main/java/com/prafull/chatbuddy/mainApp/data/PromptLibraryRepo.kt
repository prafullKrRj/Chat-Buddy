package com.prafull.chatbuddy.mainApp.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.models.PromptLibraryItem
import com.prafull.chatbuddy.utils.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PromptLibraryRepo : KoinComponent {
    private val firestore: FirebaseFirestore by inject()

    suspend fun getAllPrompts(): Flow<Resource<Pair<List<PromptLibraryItem>, List<PromptLibraryItem>>>> {
        return callbackFlow {
            try {
                val response = firestore.collection("personalPromptLibrary").get().await()
                val businessPrompts = firestore.collection("businessPromptLibrary").get()
                    .await().documents.mapNotNull {
                    it.toObject(PromptLibraryItem::class.java)
                }
                val personal = response.documents.mapNotNull { document ->
                    val item = document.toObject(PromptLibraryItem::class.java)
                    Log.d("PromptLibraryRepo", "documents: ${document.data}")
                    item
                }
                Log.d("PromptLibraryRepo", "getAllPrompts: $personal")
                trySend(
                        Resource.Success(
                                Pair(
                                        personal,
                                        businessPrompts
                                )
                        )
                )
            } catch (e: Exception) {
                trySend(Resource.Error(e))
            }
            awaitClose { }
        }
    }
}