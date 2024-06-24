package com.prafull.chatbuddy.mainApp.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.prafull.chatbuddy.mainApp.common.data.repos.ClaudeRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.FirebaseRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.GeminiRepo
import com.prafull.chatbuddy.mainApp.common.data.repos.HomeChatAbstract
import com.prafull.chatbuddy.mainApp.common.data.repos.OpenAiRepo
import com.prafull.chatbuddy.mainApp.common.model.Model
import com.prafull.chatbuddy.mainApp.home.presentation.homescreen.HomeViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


// T for message type, H for history type
abstract class BaseChatViewModel<T : Any, H : Any>(
) : ViewModel(), KoinComponent {
    protected val geminiRepository: GeminiRepo by inject()
    protected val claudeRepository: ClaudeRepo by inject()
    protected val openAiRepository: OpenAiRepo by inject()

    protected val firebaseRepo by inject<FirebaseRepo>()

    protected val _chatUiState = MutableStateFlow(ChatUIState<T>())
    val chatUiState = _chatUiState.asStateFlow()

    var isLoading by mutableStateOf(false)
    protected open lateinit var chatHistory: H

    abstract fun changeModel(newModel: Model, homeViewModel: HomeViewModel?)

    protected fun getResponseFromOpenAI() = getResponseFromRepository(openAiRepository)
    protected fun getResponseFromClaude() = getResponseFromRepository(claudeRepository)
    protected fun getResponseFromGemini() = getResponseFromRepository(geminiRepository)

    abstract fun sendMessage(message: T)
    abstract fun getResponse()
    abstract fun getResponseFromRepository(repo: HomeChatAbstract)

    abstract fun regenerateResponse()

    abstract fun saveAndUpdate(message: T)
    fun removeLastTwoMessages(id: String, promptType: String) =
        firebaseRepo.removeLastTwoMessages(id, promptType)

    fun removeLastMessage(id: String, promptType: String) =
        firebaseRepo.removeLastMessage(id, promptType)
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