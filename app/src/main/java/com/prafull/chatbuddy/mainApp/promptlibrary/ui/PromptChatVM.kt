package com.prafull.chatbuddy.mainApp.promptlibrary.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.common.data.repos.ClaudeRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.GeminiRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.HomeChatAbstract
import com.prafull.chatbuddy.mainApp.common.data.repos.OpenAiRepo
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen.ChatUIState
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryHistory
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryMessage
import com.prafull.chatbuddy.model.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class PromptChatVM(
    private val promptLibraryItem: PromptLibraryItem
) : ViewModel(), KoinComponent {

    private val geminiRepository: GeminiRepo by inject()
    private val claudeRepository: ClaudeRepo by inject()
    private val openAiRepository: OpenAiRepo by inject()

    private val _chatUiState = MutableStateFlow(ChatUIState<PromptLibraryMessage>())
    val chatUiState = _chatUiState.asStateFlow()

    var isLoading by mutableStateOf(false)

    private var chatHistory by mutableStateOf(PromptLibraryHistory())
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

    fun sendMessage(message: PromptLibraryMessage) {
        isLoading = true
        _chatUiState.value.addMessage(message)
        getResponse()
    }

    private fun getResponse() {
        when {
            chatHistory.model.isGeminiModel() -> getResponseFromGemini()
            chatHistory.model.isClaudeModel() -> getResponseFromClaude()
            chatHistory.model.isGptModel() -> getResponseFromOpenAI()
            else -> getResponseFromGemini()
        }
    }

    private fun getResponseFromOpenAI() = getResponseFromRepository(openAiRepository)
    private fun getResponseFromClaude() = getResponseFromRepository(claudeRepository)
    private fun getResponseFromGemini() = getResponseFromRepository(geminiRepository)

    private fun getResponseFromRepository(repository: HomeChatAbstract) {
        viewModelScope.launch {
            repository.getResponse(
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

    fun regenerateResponse() {
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

    fun changeModel(newModel: Model) {
        promptModel = newModel
        chatHistory = chatHistory.copy(
                model = newModel.actualName
        )
    }
}