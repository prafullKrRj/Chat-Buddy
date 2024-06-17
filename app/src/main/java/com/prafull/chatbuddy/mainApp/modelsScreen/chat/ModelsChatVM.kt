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
        /* if (currModel.generalName == "Lucia") chat.apply {
             safetySetting = Const.SAFETY_SETTINGS_ROMANTIC
         }*/
        Log.d("ModelChatVM", "updateChat: ${actualModel.system}")
        chat.apply {
            model = currModel.actualName
            temperature = actualModel.temperature
            systemPrompt = actualModel.system
        }
        Log.d("ModelChatVM", "updateChat: $chat")
    }
}