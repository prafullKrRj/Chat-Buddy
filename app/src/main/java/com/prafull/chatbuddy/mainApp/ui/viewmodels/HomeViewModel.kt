package com.prafull.chatbuddy.mainApp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafull.chatbuddy.mainApp.data.HomeRepository
import com.prafull.chatbuddy.mainApp.models.ChatHistory
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
    private val _adButtonEnabled = MutableStateFlow(true)
    val adButtonEnabled = _adButtonEnabled.asStateFlow()

    private val _coins = MutableStateFlow(CoinState())
    val coins = _coins.asStateFlow()

    private val _watchedAd = MutableStateFlow(false)
    val watchedAd = _watchedAd.asStateFlow()
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
        getCoins()
    }

    private fun getCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getCoins().collect { resp ->
                when (resp) {
                    is Resource.Success -> {
                        _coins.update {
                            CoinState(
                                    currCoins = resp.data,
                                    initial = false
                            )
                        }
                    }

                    is Resource.Initial -> {
                        _coins.update {
                            CoinState(
                                    currCoins = 2000L,
                                    initial = true
                            )
                        }
                    }

                    is Resource.Error -> {
                        _coins.update { CoinState(initial = false) }
                    }
                }
            }
        }
    }

    fun updateAdButtonState(enabled: Boolean) {
        _adButtonEnabled.update {
            enabled
        }
    }

    init {
        getPreviousChats()
    }

    private val _previousChats = MutableStateFlow<Resource<List<ChatHistory>>>(Resource.Initial)
    val previousChats = _previousChats.asStateFlow()
    fun getPreviousChats() {
        viewModelScope.launch(Dispatchers.IO) {
            homeRepository.getPreviousChats().collect { resp ->
                when (resp) {
                    is Resource.Success -> {
                        _previousChats.update { Resource.Success(resp.data) }
                    }

                    is Resource.Initial -> {
                        _previousChats.update { Resource.Initial }
                    }

                    is Resource.Error -> {
                        _previousChats.update { Resource.Error(resp.exception) }
                    }
                }
            }
        }
    }
}

data class CoinState(
    val currCoins: Long = 2000L,
    val initial: Boolean = true
)