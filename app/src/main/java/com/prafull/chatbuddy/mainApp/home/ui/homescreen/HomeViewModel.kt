package com.prafull.chatbuddy.mainApp.home.ui.homescreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.home.data.repos.HomeRepository
import com.prafull.chatbuddy.mainApp.home.model.ChatHistory
import com.prafull.chatbuddy.model.Model
import com.prafull.chatbuddy.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val homeRepository by inject<HomeRepository>()
    var adButtonEnabled by mutableStateOf(true)
    private val _coins = MutableStateFlow(CoinState())
    val coins = _coins.asStateFlow()

    private val _watchedAd = MutableStateFlow(false)
    val watchedAd = _watchedAd.asStateFlow()

    private val _previousChats = MutableStateFlow<Resource<List<ChatHistory>>>(Resource.Initial)
    val previousChats = _previousChats.asStateFlow()

    private val _dialogState = MutableStateFlow<Resource<List<Model>>>(Resource.Initial)
    var modelButtonClicked by mutableStateOf(false)
    val modelDialogState = _dialogState.asStateFlow()


    fun adWatched() {
        _watchedAd.update {
            true
        }
        if (watchedAd.value) {
            viewModelScope.launch(Dispatchers.IO) {
                homeRepository.updateCoins(coins.value.currCoins).collect {
                    if (it) {
                        getCoins()
                    }
                }
                _watchedAd.update { false }
            }
        }
    }

    init {
        getPreviousChats()
        getCoins()
    }

    private fun getCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getCoins().collect { resp ->
                resp.also { response ->
                    _coins.update { _ ->
                        when (response) {
                            is Resource.Success -> CoinState(
                                    currCoins = response.data,
                                    initial = false
                            )

                            is Resource.Initial -> CoinState(currCoins = 2000L, initial = true)
                            is Resource.Error -> CoinState(initial = false)
                        }
                    }
                }
            }
        }
    }

    fun getPreviousChats() {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getPreviousChats().collect { resp ->
                _previousChats.update {
                    resp
                }
            }
        }
    }

    fun getModels() {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getModels().collect { resp ->
                _dialogState.update {
                    resp
                }
            }
        }
    }

    fun deleteChat(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.deleteChat(id).collect {
                getPreviousChats()
            }
        }
    }
}

data class CoinState(
    val currCoins: Long = 2000L,
    val initial: Boolean = true
)