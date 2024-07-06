package com.prafull.chatbuddy.mainApp.modelsScreen.ui.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.common.data.repos.HomeChatAbstract
import com.prafull.chatbuddy.mainApp.common.model.BaseChatViewModel
import com.prafull.chatbuddy.mainApp.common.model.ChatUIState
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.common.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.common.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.common.model.isGptModel
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsMessage
import com.prafull.chatbuddy.utils.CryptoEncryption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ModelsChatNewVM(
    suppliedModel: Model,
    id: String
) : BaseChatViewModel<ModelsMessage, ModelsHistory>() {

    var model by mutableStateOf(suppliedModel)
    var historyLoading by mutableStateOf(false)

    override var chatHistory: ModelsHistory by mutableStateOf(ModelsHistory())

    init {
        Log.d("Models View Model", id)
        model = suppliedModel
        if (id.isEmpty()) {
            chatHistory = chatHistory.copy(
                    model = model.actualName,
                    system = model.system,
                    safetySettings = model.safetySetting,
                    temperature = model.temperature
            )
        }
        if (id.isNotEmpty()) {
            getCharacterHistory(id)
        }
    }

    /**
     *      Get the character history from the firebase
     * */
    private fun getCharacterHistory(id: String) = viewModelScope.launch(Dispatchers.IO) {
        historyLoading = true
        firebaseRepo.getModelsHistory(id, model).collectLatest { it ->
            chatHistory = it
            historyLoading = false
            _chatUiState.update {
                ChatUIState(
                        messages = chatHistory.messages.map { message ->
                            message.convertToDecryptedMessages()
                        }
                )
            }
        }
    }

    private fun ModelsMessage.convertToDecryptedMessages(): ModelsMessage {
        return ModelsMessage(
                id,
                text = CryptoEncryption.decrypt(text),
                participant,
                model,
                botImage,
                imageBase64,
                imageBitmaps
        )
    }

    override fun getResponse() {
        when {
            chatHistory.model.isGeminiModel() -> getResponseFromGemini()
            chatHistory.model.isClaudeModel() -> getResponseFromClaude()
            chatHistory.model.isGptModel() -> getResponseFromOpenAI()
            else -> getResponseFromGemini()
        }
    }

    override fun getResponseFromRepository(repo: HomeChatAbstract) {
        viewModelScope.launch {
            repo.getResponse(
                    chatHistory.toHistoryItem(),
                    chatUiState.value.getLast().toHistoryMessage()
            ).collect { response ->
                chatHistory.apply {
                    messages.addAll(
                            listOf(
                                    _chatUiState.value.getLast(),
                                    response.toModelsHisMsg()
                            )
                    )
                }
                saveToFirebase()
                _chatUiState.value.addMessage(response.toModelsHisMsg())
                isLoading = false
            }
        }
    }

    override fun regenerateResponse() {
        viewModelScope.launch {
            isLoading = true
            chatUiState.value.removeLastMessage()
            /*
            *       chatUiState.value.removeLastTwo() because we are adding both prompt and response in the lat the time of response
            * */
            chatHistory.messages.removeLast()
            chatHistory.messages.removeLast()
            if (removeLastTwoMessages(
                        chatHistory.id,
                        chatHistory.promptType
                )
            ) {       // Delete the last two messages from the database as while getting response we are adding both prompt and response
                getResponse()
            }
        }
    }

    override fun saveToFirebase() {
        firebaseRepo.saveModelsMessage(chatHistory.copy())
    }

    override fun sendMessage(message: ModelsMessage) {
        isLoading = true
        _chatUiState.value.addMessage(message)
        getResponse()
    }

    override fun changeModel(newModel: Model, homeViewModel: HomeViewModel?) {
        TODO("Not yet implemented")
    }
}