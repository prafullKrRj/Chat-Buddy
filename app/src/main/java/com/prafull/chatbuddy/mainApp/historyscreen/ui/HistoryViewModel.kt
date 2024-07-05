package com.prafull.chatbuddy.mainApp.historyscreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.historyscreen.data.HistoryRepo
import com.prafull.chatbuddy.mainApp.historyscreen.model.HistoryClass
import com.prafull.chatbuddy.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HistoryViewModel : ViewModel(), KoinComponent {
    private val historyRepo: HistoryRepo by inject()

    private val _historyState = MutableStateFlow(HistoryUiState())
    val historyState = _historyState.asStateFlow()

    init {
        getHistory()
    }

    fun getHistory() {
        _historyState.update { HistoryUiState(loading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            historyRepo.getHistory().collectLatest { response ->
                when (response) {
                    is Resource.Error -> {
                        _historyState.update {
                            HistoryUiState(
                                    error = Pair(true, response.exception.message ?: ""),
                                    loading = false
                            )
                        }
                    }

                    Resource.Initial -> {
                        _historyState.update {
                            HistoryUiState(loading = true, error = Pair(false, ""))
                        }
                    }

                    is Resource.Success -> {
                        _historyState.update {
                            HistoryUiState(
                                    error = Pair(false, ""),
                                    loading = false,
                                    history = response.data
                            )
                        }
                    }
                }
            }
        }
    }

    fun deleteChat(chatId: String, promptType: String) = viewModelScope.launch(Dispatchers.IO) {
        historyRepo.deleteChat(chatId, promptType).collectLatest {
            getHistory()
        }
    }
}

data class HistoryUiState(
    val loading: Boolean = false,
    val error: Pair<Boolean, String> = Pair(false, ""),
    val history: HistoryClass = HistoryClass()
)