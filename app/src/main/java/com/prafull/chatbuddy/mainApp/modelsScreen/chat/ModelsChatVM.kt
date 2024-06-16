package com.prafull.chatbuddy.mainApp.modelsScreen.chat

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.ChatViewModelAbstraction
import com.prafull.chatbuddy.model.Model
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ModelsChatVM(
    private val actualModel: Model
) : ChatViewModelAbstraction() {

    private val firestore: FirebaseFirestore by inject()

    init {
        viewModelScope.launch {
            updateChat()
            Log.d("ModelChatVM", "init: $chat")
        }
    }

    private fun updateChat() {
        chatting = true
        loadNewChat()
        currModel = actualModel
        chat.apply {
            model = currModel.actualName
            temperature = actualModel.temperature
        }
    }
}