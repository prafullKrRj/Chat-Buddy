package com.prafull.chatbuddy.mainApp.modelsScreen.chat

import com.prafull.chatbuddy.mainApp.ChatViewModelAbstraction
import com.prafull.chatbuddy.model.Model

class ModelsChatVM(
    private val actualModel: Model
) : ChatViewModelAbstraction() {
    /*
        private val firestore: FirebaseFirestore by inject()
        private val mAuth: FirebaseAuth by inject()
        var historyLoading by mutableStateOf(false)
        var historyError by mutableStateOf(false)

        init {
            updateChat()
        }

        fun updateChat() {
            viewModelScope.launch {
                chatting = true
                if (actualModel.modelGroup != "Characters") {
                    loadNewChat()
                    _currentModel.update {
                        actualModel
                    }
                    chat.apply {
                        model = actualModel.actualName
                        temperature = actualModel.temperature
                        systemPrompt = actualModel.system
                        safetySetting = actualModel.safetySetting
                        modelGeneralName = actualModel.generalName
                        botImage = actualModel.image
                    }
                    historyError = false
                } else {
                    try {
                        mAuth.currentUser?.email?.let {
                            val response = firestore.collection("users").document(it)
                                .collection("history").document(actualModel.generalName).get().await()
                            val history = response.toObject(ChatHistory::class.java)
                            history?.let { chatHistory ->
                                chatHistory.messages = chatHistory.messages.map { message ->
                                    message.text = CryptoEncryption.decrypt(message.text)
                                    message
                                }.toMutableList()
                            }
                            _currentModel.update {
                                actualModel
                            }
                            if (history == null) {
                                newCharacterChat(actualModel)
                            } else {
                                chatFromHistoryCharacter(history, actualModel)
                            }
                        }
                        historyError = false
                        historyLoading = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                        historyLoading = false
                    }
                }
            }
        }*/
}