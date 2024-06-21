package com.prafull.chatbuddy.mainApp

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.ChatRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.ClaudeRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.GeminiRepository
import com.prafull.chatbuddy.mainApp.home.data.repos.chats.OpenAiRepository
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.mainApp.home.model.ChatMessage
import com.prafull.chatbuddy.mainApp.home.model.isClaudeModel
import com.prafull.chatbuddy.mainApp.home.model.isGeminiModel
import com.prafull.chatbuddy.mainApp.home.model.isGptModel
import com.prafull.chatbuddy.mainApp.home.ui.homescreen.ChatUiState
import com.prafull.chatbuddy.mainApp.promptlibrary.model.PromptLibraryItem
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.utils.Const
import com.prafull.chatbuddy.utils.SharedPrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

abstract class ChatViewModelAbstraction : KoinComponent, ViewModel() {

    private val geminiRepository: GeminiRepository by inject()
    private val claudeRepository: ClaudeRepository by inject()
    private val openAiRepository: OpenAiRepository by inject()
    private val sharedPref: SharedPrefManager by inject()
    private val firestore by inject<FirebaseFirestore>()

    var currChatUUID by mutableStateOf(UUID.randomUUID().toString())

    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState())   // UI state
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    var currPrompt by mutableStateOf(ChatMessage())    // current prompt
    var chatting by mutableStateOf(false)
    var loading by mutableStateOf(false)

    protected var chat =
        ChatHistory(id = currChatUUID)         // current chat

    protected val _currentModel = MutableStateFlow(Model())
    val currentModel: StateFlow<Model> = _currentModel.asStateFlow()
    private var defaultModel by mutableStateOf(Model())

    init {
        getDefaultModel()
    }

    fun getDefaultModel() {
        viewModelScope.launch {
            val savedModel = sharedPref.getModel()
            Log.d("ChatViewModel", "getDefaultModel: $savedModel")
            if (savedModel.generalName == Const.CHAT_BUDDY) {
                val respondedModel =
                    firestore.collection("models").document("nlp").collection(Const.CHAT_BUDDY)
                        .document(Const.CHAT_BUDDY).get().await().toObject(Model::class.java)
                respondedModel?.let { response ->
                    sharedPref.setModel(response)
                    _currentModel.update {
                        response
                    }
                    defaultModel = response
                    chat.apply {
                        model = response.actualName
                    }
                }
            } else {
                val respondedModel =
                    firestore.collection("models").document("nlp").collection(savedModel.modelGroup)
                        .document(savedModel.actualName).get().await().toObject(Model::class.java)
                Log.d("ChatViewModel", "getDefaultModel: $respondedModel")
                respondedModel?.let { response ->
                    sharedPref.setModel(response)
                    _currentModel.update {
                        response
                    }
                    defaultModel = response
                    chat.apply {
                        model = response.actualName
                    }
                }
            }
            Log.d("ChatViewModel", "getDefaultModel: ${currentModel.value.generalName}")
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            loading = true
            chatting = true
            _uiState.value.addMessage(currPrompt)
            saveMessage(currPrompt)
            currPrompt = ChatMessage(
                    model = currentModel.value.generalName
            )
            getResponse()
        }
    }

    fun regenerateResponse() {
        viewModelScope.launch {
            chat.messages.removeLast()
            chat.messages.removeLast()
            _uiState.value.removeLastMessage()
            currPrompt = _uiState.value.messages.last()
            loading = true
            chatting = true
            currPrompt = ChatMessage()
            geminiRepository.deleteLast(currChatUUID)
            getResponse()
        }
    }

    fun updateLastPrompt(images: List<Bitmap>, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading = true
            _uiState.value.removeLastMessage()
            _uiState.value.removeLastMessage()
            currPrompt = ChatMessage(
                    text = text,
                    imageBitmaps = images
            )
            val x = geminiRepository.deleteLastTwo(currChatUUID)
            if (x) {
                chat.messages.removeLast()
                chat.messages.removeLast()
                chatting = true
                sendMessage()
            }
        }
    }

    private fun saveMessage(message: ChatMessage) {
        geminiRepository.saveMessage(chat, message)
    }

    private fun getResponse() {
        when {
            chat.model.isGeminiModel() -> getResponseFromGemini()
            chat.model.isClaudeModel() -> getResponseFromClaude()
            chat.model.isGptModel() -> getResponseFromOpenAI()
            else -> getResponseFromGemini()
        }
    }

    private fun getResponseFromOpenAI() = getResponseFromRepository(openAiRepository)
    private fun getResponseFromClaude() = getResponseFromRepository(claudeRepository)
    private fun getResponseFromGemini() = getResponseFromRepository(geminiRepository)
    private fun getResponseFromRepository(repository: ChatRepository) {
        viewModelScope.launch {
            repository.getResponse(chat, _uiState.value.messages.last()).collect { response ->
                response.apply {
                    model = chat.modelGeneralName
                }
                chat.apply {
                    messages.add(_uiState.value.messages.last())
                }
                chat.apply {
                    messages.add(response)
                }
                updateChatWithResponse(response)
            }
        }
    }


    private fun updateChatWithResponse(response: ChatMessage) {
        _uiState.value.addMessage(response)
        Log.d("ChatViewModel", "Response: ${response.model}")
        saveMessage(response)
        loading = false
    }

    fun chatFromHistory(chatHistory: ChatHistory) {
        viewModelScope.launch {
            currChatUUID = chatHistory.id
            currPrompt = ChatMessage()
            Log.d("ChatViewModel", "chatFromHistory: ${chatHistory.modelGeneralName}")
            chat = chatHistory.copy()
            Log.d("ChatViewModel", "chatFromHistory: ${chatHistory.modelGeneralName}")
            _currentModel.update {
                chatHistory.toModel()
            }
            Log.d("ChatViewModel", "chatFromHistory: ${chatHistory.modelGeneralName}")
            _uiState.update {
                ChatUiState(messages = chatHistory.messages)
            }
            chatting = true
            loading = false
        }
    }

    fun chatFromHistoryCharacter(chatHistory: ChatHistory, character: Model) = chatFromHistory(
            chatHistory.apply {
                model = character.actualName
                temperature = character.temperature
                safetySetting = character.safetySetting
                systemPrompt = character.system
                modelGeneralName = character.generalName
                botImage = character.image
            }
    )

    fun loadNewChat() {
        if (chatting) {
            updateScreenState()
            chat = ChatHistory(id = currChatUUID)
            _currentModel.update {
                Model()
            }
            chatting = false
        }
    }

    fun newCharacterChat(character: Model) {
        currChatUUID = character.generalName
        currPrompt = ChatMessage()
        chat = ChatHistory(
                id = currChatUUID,
                model = character.actualName,
                temperature = character.temperature,
                systemPrompt = character.system,
                safetySetting = character.safetySetting,
                modelGeneralName = character.generalName,
                botImage = character.image,
        )
        _currentModel.update {
            character
        }
        chatting = false
        loading = false
    }

    fun loadFromPromptLibrary(promptLibraryItem: PromptLibraryItem) {
        updateScreenState()
        chat.apply {
            id = currChatUUID
            messages = mutableListOf()
            systemPrompt = promptLibraryItem.system
            promptDescription = promptLibraryItem.description
            promptName = promptLibraryItem.name
        }
        chatting = false
    }

    private fun updateScreenState() {
        currChatUUID = UUID.randomUUID().toString()
        currPrompt = ChatMessage()
        loading = false
        _uiState.update {
            ChatUiState()
        }
    }

    fun onModelSelected(model: Model) {
        chat.apply {
            this.model = model.actualName
            modelGeneralName = model.generalName
            temperature = model.temperature
        }
        _currentModel.update {
            model
        }
    }

    fun getCurrChat() = chat // for prompt details
}