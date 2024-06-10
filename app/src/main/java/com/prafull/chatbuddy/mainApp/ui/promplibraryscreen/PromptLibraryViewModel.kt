package com.prafull.chatbuddy.mainApp.ui.promplibraryscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.data.PromptLibraryRepo
import com.prafull.chatbuddy.mainApp.models.PromptLibraryItem
import com.prafull.chatbuddy.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PromptLibraryViewModel : ViewModel(), KoinComponent {

    private val promptLibraryRepo: PromptLibraryRepo by inject()

    private val _uiState =
        MutableStateFlow(PromptLibraryUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        getPrompts()
    }

    fun getPrompts() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            promptLibraryRepo.getAllPrompts().collect { response ->
                when (response) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                    personalPrompts = response.data.first,
                                    businessPrompts = response.data.second,
                                    isLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                    isLoading = false,
                                    error = response.exception.message ?: "An error occurred"
                            )
                        }
                    }

                    is Resource.Initial -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }
}

data class PromptLibraryUiState(
    val personalPrompts: List<PromptLibraryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val businessPrompts: List<PromptLibraryItem> = emptyList()
)