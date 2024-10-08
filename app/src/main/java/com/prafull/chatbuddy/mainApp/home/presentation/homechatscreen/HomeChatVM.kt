package com.prafull.chatbuddy.mainApp.home.presentation.homechatscreen

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
import com.prafull.chatbuddy.mainApp.home.models.ChatHistoryNormal
import com.prafull.chatbuddy.mainApp.home.models.NormalHistoryMsg
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import com.prafull.chatbuddy.utils.CryptoEncryption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeChatVM(
    firstPrompt: NormalHistoryMsg,
    initialModel: Model,
    chatId: String
) : BaseChatViewModel<NormalHistoryMsg, ChatHistoryNormal>() {

    override var chatHistory by mutableStateOf(ChatHistoryNormal(model = "gemini-1.5-flash-latest"))
    override fun changeModel(newModel: Model, homeViewModel: HomeViewModel?) {
        chatHistory = chatHistory.copy(
                model = newModel.actualName
        )
        homeViewModel?.currModel = newModel
    }

    var screenLoading by mutableStateOf(false)

    init {
        if (chatId.isEmpty()) {
            chatHistory = chatHistory.copy(model = initialModel.actualName)
            sendMessage(firstPrompt)
        } else {
            screenLoading = true
            getHistory(id = chatId)
        }
    }

    private fun getHistory(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.getNormalHistory(id).collect { history ->
                chatHistory = history
                _chatUiState.update {
                    ChatUIState(
                            messages = history.messages.map { it.convertToDecryptedMessage() }
                    )
                }
                screenLoading = false
            }
        }
    }

    private fun NormalHistoryMsg.convertToDecryptedMessage() = this.copy(
            text = CryptoEncryption.decrypt(text)
    )

    override fun sendMessage(message: NormalHistoryMsg) {
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
                    messages.addAll(
                            listOf(
                                    _chatUiState.value.getLast(),
                                    response.toNormalHistoryMsg()
                            )
                    )
                }
                saveToFirebase()
                _chatUiState.value.addMessage(response.toNormalHistoryMsg())
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
        firebaseRepo.saveNormalMessage(chatHistory.copy())
    }
}
