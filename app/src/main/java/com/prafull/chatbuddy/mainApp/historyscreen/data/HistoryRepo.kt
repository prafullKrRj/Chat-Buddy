package com.prafull.chatbuddy.mainApp.historyscreen.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.historyscreen.model.HistoryClass
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryHistory
import com.prafull.chatbuddy.utils.Const
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


    suspend fun getHistory(): Flow<Resource<HistoryClass>> {
        return callbackFlow {
            try {
                val historyClass = HistoryClass()
                firesStore.collection("users").document(fireAuth.currentUser!!.email.toString())
                    .collection(Const.MODELS_HISTORY).get().await().documents.forEach { document ->
                        document.toObject(ModelsHistory::class.java)
                            ?.let { historyClass.modelsHistory.add(it) }
                    }
                /*
                firesStore.collection("users").document(fireAuth.currentUser!!.email.toString())
                    .collection(Const.NORMAL_HISTORY).get().await().documents.forEach { document ->
                        document.toObject(ChatHistoryNormal::class.java)
                            ?.let { historyClass.normalHistory.add(it) }
                    }*/
                firesStore.collection("users").document(fireAuth.currentUser!!.email.toString())
                    .collection(Const.LIBRARY_HISTORY).get().await().documents.forEach { document ->
                        document.toObject(PromptLibraryHistory::class.java)
                            ?.let { historyClass.promptLibHistory.add(it) }
                    }
                trySend(Resource.Success(historyClass))
            } catch (e: Exception) {
                trySend(Resource.Error(e))
            }
            awaitClose { }
        }
    }
}
