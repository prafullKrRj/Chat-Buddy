package com.prafull.chatbuddy.mainApp.promptlibrary.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.common.BaseChatViewModel
import com.prafull.chatbuddy.mainApp.common.data.repos.HomeChatAbstract
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomeViewModel
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryHistory
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryMessage
import com.prafull.chatbuddy.model.Model
import kotlinx.coroutines.launch


class PromptChatVM(
    private val promptLibraryItem: PromptLibraryItem
) : BaseChatViewModel<PromptLibraryMessage, PromptLibraryHistory>(
) {

    override var chatHistory by mutableStateOf(PromptLibraryHistory())

    override fun changeModel(newModel: Model, homeViewModel: NewHomeViewModel?) {
        promptModel = newModel
        chatHistory = chatHistory.copy(
                model = newModel.actualName
        )
    }

    var promptModel by mutableStateOf(Model())
    fun getPromptItem() = promptLibraryItem

    init {
        chatHistory = PromptLibraryHistory(
                name = promptLibraryItem.name,
                description = promptLibraryItem.description,
                system = promptLibraryItem.system,
                user = promptLibraryItem.user,
                model = promptModel.actualName
        )
    }

    override fun sendMessage(message: PromptLibraryMessage) {
        isLoading = true
        _chatUiState.value.addMessage(message)
        getResponse()
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
                    messages.addAll(listOf(_chatUiState.value.getLast(), response.toPromptLibMsg()))
                }
                _chatUiState.value.addMessage(response.toPromptLibMsg())
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
}