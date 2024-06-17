package com.prafull.chatbuddy.mainApp.modelsScreen.chat

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.ChatViewModelAbstraction
import com.prafull.chatbuddy.mainApp.modelsScreen.ModelSafety
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.utils.Const
import kotlinx.coroutines.launch
import org.koin.core.component.inject

class ModelsChatVM(
    private val actualModel: Model
) : ChatViewModelAbstraction() {

    private val firestore: FirebaseFirestore by inject()

    init {
        viewModelScope.launch {
            updateChat()
        }
    }

    private fun updateChat() {
        chatting = true
        loadNewChat()
        currModel = actualModel
        chat.apply {
            model = currModel.actualName
            temperature = actualModel.temperature
            systemPrompt = actualModel.system
            safetySetting = when (actualModel.safetySetting) {
                ModelSafety.UNSPECIFIED.name -> Const.SAFETY_SETTINGS_NORMAL
                ModelSafety.ROMANTIC.name -> Const.SAFETY_SETTINGS_ROMANTIC
                else -> Const.SAFETY_SETTINGS_NORMAL
            }
        }
    }
}