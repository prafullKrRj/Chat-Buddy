package com.prafull.chatbuddy.mainApp.newHome.presentation.homechatscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.common.data.repos.ClaudeRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.GeminiRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.HomeChatAbstract
import com.prafull.chatbuddy.mainApp.common.data.repos.OpenAiRepo
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.mainApp.newHome.models.ChatHistoryNormal
import com.prafull.chatbuddy.mainApp.newHome.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.newHome.presentation.homescreen.NewHomeViewModel
import com.prafull.chatbuddy.model.Model
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeChatVM(
    firstPrompt: NormalHistoryMsg,
    initialModel: Model
) : ViewModel(), KoinComponent {

    private val geminiRepository: GeminiRepo by inject()
    private val claudeRepository: ClaudeRepo by inject()
    private val openAiRepository: OpenAiRepo by inject()

    private val _chatUiState = MutableStateFlow(ChatUIState<NormalHistoryMsg>())
    val chatUiState = _chatUiState.asStateFlow()

    var isLoading by mutableStateOf(false)
    private var chatHistory by mutableStateOf(ChatHistoryNormal(model = "gemini-1.5-flash-latest"))

    init {
        chatHistory = chatHistory.copy(model = initialModel.actualName)
        sendMessage(firstPrompt)
    }

    fun sendMessage(message: NormalHistoryMsg) {
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
                    messages.addAll(
                            listOf(
                                    _chatUiState.value.getLast(),
                                    response.toNormalHistoryMsg()
                            )
                    )
                }
                _chatUiState.value.addMessage(response.toNormalHistoryMsg())
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

    fun changeModel(homeViewModel: NewHomeViewModel, newModel: Model) {
        Log.d("HomeChatVM", "changeModel: ${newModel}")
        chatHistory = chatHistory.copy(
                model = newModel.actualName
        )
        homeViewModel.currModel = newModel
    }
}

class ChatUIState<T>(
    messages: List<T> = emptyList()
) {
    private val _messages: MutableList<T> = messages.toMutableStateList()
    val messages: List<T> = _messages
    fun addMessage(msg: T) {
        _messages.add(msg)
    }

    fun removeLastMessage() {
        _messages.removeLast()
    }

    fun removeLastTwo() {
        _messages.removeLast()
        _messages.removeLast()
    }

    fun getLast() = _messages.last()
}