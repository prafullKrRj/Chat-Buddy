package com.prafull.chatbuddy.mainApp.modelsScreen.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.common.BaseChatViewModel
import com.prafull.chatbuddy.mainApp.common.data.repos.HomeChatAbstract
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsHistory
import com.prafull.chatbuddy.mainApp.modelsScreen.model.ModelsMessage
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomeViewModel
import com.prafull.chatbuddy.model.Model
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
                    messages.addAll(listOf(_chatUiState.value.getLast(), response.toModelsHisMsg()))
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
            if (geminiRepository.deleteLastTwo(chatHistory.toHistoryItem())) {       // Delete the last two messages from the database as while getting response we are adding both prompt and response
                getResponse()
            }
        }
    }

    override fun sendMessage(message: ModelsMessage) {
        isLoading = true
        _chatUiState.value.addMessage(message)
        getResponse()
    }

    override fun changeModel(newModel: Model, homeViewModel: NewHomeViewModel?) {
        TODO("Not yet implemented")
    }
}