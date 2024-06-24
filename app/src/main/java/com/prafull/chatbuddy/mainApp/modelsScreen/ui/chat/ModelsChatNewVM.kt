package com.prafull.chatbuddy.mainApp.modelsScreen.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.common.data.repos.HomeChatAbstract
import com.prafull.chatbuddy.mainApp.common.model.BaseChatViewModel
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.common.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.common.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.common.model.isGptModel
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModelsChatNewVM(
    suppliedModel: Model
) : BaseChatViewModel<ModelsMessage, ModelsHistory>() {

    var model by mutableStateOf(suppliedModel)
    override var chatHistory: ModelsHistory by mutableStateOf(ModelsHistory())

    init {
        model = suppliedModel
        chatHistory = chatHistory.copy(
                model = model.actualName,
                system = model.system,
                safetySettings = model.safetySetting,
                temperature = model.temperature
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

    override fun saveAndUpdate(message: ModelsMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.saveModelsMessage(chatHistory.copy(), _chatUiState.value.getLast())
            chatHistory.apply {
                messages.add(_chatUiState.value.getLast())
            }
        }
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